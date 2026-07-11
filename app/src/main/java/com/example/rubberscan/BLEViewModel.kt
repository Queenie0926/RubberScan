package com.example.rubberscan

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val BLE_TAG = "BLEPairing"

@SuppressLint("MissingPermission")
class BleViewModel(app: Application) : AndroidViewModel(app) {

    private val btManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val btAdapter = btManager.adapter
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _bleState = MutableStateFlow(BleState.IDLE)
    val bleState: StateFlow<BleState> = _bleState

    private val _foundDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    val foundDevices: StateFlow<List<BleDevice>> = _foundDevices

    private val _connectedName = MutableStateFlow("")
    val connectedName: StateFlow<String> = _connectedName

    private val _temperature = MutableStateFlow<Float?>(null)
    val temperature: StateFlow<Float?> = _temperature

    private val _humidity = MutableStateFlow<Float?>(null)
    val humidity: StateFlow<Float?> = _humidity

    private val _statusMsg = MutableStateFlow("Tap 'Scan for Devices' to find your sensor")
    val statusMsg: StateFlow<String> = _statusMsg

    private val _toastMsg = MutableStateFlow("")
    val toastMsg: StateFlow<String> = _toastMsg

    private var gatt: BluetoothGatt? = null

    // ── Add these properties near the top, after gatt declaration ──
    private var lastConnectedDevice: BleDevice? = null
    var autoReconnect: Boolean = false
    var notificationsEnabled: Boolean = false  // set from SettingsViewModel
    private var isIntentionalDisconnect = false
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5


    // ── Permission helpers ────────────────────────────────────────────────
    fun hasPerm(perm: String): Boolean =
        ContextCompat.checkSelfPermission(getApplication(), perm) == PackageManager.PERMISSION_GRANTED

    fun canScan(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            hasPerm(Manifest.permission.BLUETOOTH_SCAN)

    fun canConnect(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            hasPerm(Manifest.permission.BLUETOOTH_CONNECT)

    // ── Toast (auto-clears after 2 seconds) ──────────────────────────────
    fun showToast(msg: String) {
        _toastMsg.value = msg
        viewModelScope.launch {
            delay(2_000)
            _toastMsg.value = ""
        }
    }

    // ── GATT callback ─────────────────────────────────────────────────────
    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            mainHandler.post {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d(BLE_TAG, "Connected to GATT server, discovering services...")
                        _bleState.value  = BleState.CONNECTED
                        _statusMsg.value = "Discovering sensor services..."
                        if (canConnect()) g.discoverServices()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(BLE_TAG, "Disconnected from GATT server (status=$status)")
                        _bleState.value    = BleState.DISCONNECTED
                        _temperature.value = null
                        _humidity.value    = null
                        g.close()
                        gatt = null

                        val deviceName = _connectedName.value.ifBlank { "Sensor" }

                        val shouldReconnect = autoReconnect
                                && !isIntentionalDisconnect
                                && lastConnectedDevice != null
                                && reconnectAttempts < maxReconnectAttempts

                        if (shouldReconnect) {
                            reconnectAttempts++
                            _statusMsg.value = "Sensor disconnected. Reconnecting... (attempt $reconnectAttempts)"

                            // Notify disconnection only on first attempt
                            if (reconnectAttempts == 1 && notificationsEnabled) {
                                NotificationHelper.notifySensorDisconnected(getApplication(), deviceName)
                            }

                            viewModelScope.launch {
                                delay(3_000)
                                val device = lastConnectedDevice ?: return@launch
                                if (_bleState.value != BleState.CONNECTED && canConnect()) {
                                    _bleState.value = BleState.CONNECTING
                                    gatt = device.device.connectGatt(getApplication(), false, gattCallback)
                                }
                            }
                        } else {
                            isIntentionalDisconnect = false

                            if (autoReconnect && reconnectAttempts >= maxReconnectAttempts) {
                                _statusMsg.value = "Could not reconnect after $maxReconnectAttempts attempts. Tap scan to try again."
                                if (notificationsEnabled) {
                                    NotificationHelper.notifyReconnectFailed(getApplication(), deviceName, maxReconnectAttempts)
                                }
                            } else {
                                // Only notify on unexpected disconnect (not intentional)
                                if (!isIntentionalDisconnect && notificationsEnabled) {
                                    NotificationHelper.notifySensorDisconnected(getApplication(), deviceName)
                                }
                                _statusMsg.value = "Sensor disconnected. Tap scan to reconnect."
                            }
                        }
                    }
                }
            }
        }

        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS || !canConnect()) return
            val char = g.getService(SERVICE_UUID)?.getCharacteristic(SENSOR_CHAR_UUID) ?: run {
                Log.e(BLE_TAG, "Sensor service or characteristic not found on device")
                mainHandler.post { showToast("Sensor service not found on this device.") }
                return
            }
            @Suppress("DEPRECATION")
            g.setCharacteristicNotification(char, true)
            @Suppress("DEPRECATION")
            val descriptor = char.getDescriptor(CLIENT_CONFIG_UUID)
            if (descriptor != null) {
                @Suppress("DEPRECATION")
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                g.writeDescriptor(descriptor)
            }
            Log.d(BLE_TAG, "Notifications enabled — receiving live sensor data")
            mainHandler.post { _statusMsg.value = "Receiving live data from your sensor." }
        }

        private fun handleSensorData(bytes: ByteArray) {
            val json = bytes.toString(Charsets.UTF_8)
            val t = Regex(""""t":([\d.]+)""").find(json)?.groupValues?.get(1)?.toFloatOrNull()
            val h = Regex(""""h":([\d.]+)""").find(json)?.groupValues?.get(1)?.toFloatOrNull()
            Log.d(BLE_TAG, "Sensor data received: t=$t, h=$h")
            mainHandler.post {
                if (t != null) _temperature.value = t
                if (h != null) _humidity.value    = h
            }
        }

        @Deprecated("Deprecated in Java")
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(g: BluetoothGatt, char: BluetoothGattCharacteristic) {
            handleSensorData(char.value ?: return)
        }

        override fun onCharacteristicChanged(g: BluetoothGatt, char: BluetoothGattCharacteristic, value: ByteArray) {
            handleSensorData(value)
        }
    }

    // ── Scan callback ─────────────────────────────────────────────────────
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val name = if (canConnect()) result.device.name ?: "Unknown" else "Unknown"
            val entry = BleDevice(name, result.device.address, result.rssi, result.device)
            mainHandler.post {
                _foundDevices.value = (_foundDevices.value.filter { it.address != entry.address } + entry)
                    .sortedByDescending { if (it.name.contains(TARGET_DEVICE_NAME)) 1000 else it.rssi }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(BLE_TAG, "BLE scan failed with error code: $errorCode")
            mainHandler.post {
                _bleState.value = BleState.ERROR
                showToast("Scan failed. Please try again.")
            }
        }
    }

    // ── Public actions ────────────────────────────────────────────────────
    fun startScan() {
        _foundDevices.value = emptyList()
        _bleState.value     = BleState.SCANNING
        _statusMsg.value    = "Looking for nearby sensors..."
        Log.d(BLE_TAG, "Starting BLE scan...")

        viewModelScope.launch {
            btAdapter?.bluetoothLeScanner?.startScan(scanCallback)
            delay(12_000)
            if (_bleState.value == BleState.SCANNING) {
                btAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
                _bleState.value  = BleState.IDLE
                _statusMsg.value = if (_foundDevices.value.isEmpty())
                    "Tap 'Scan for Devices' to find your sensor"
                else "Tap a device below to connect"
                Log.d(BLE_TAG, "Scan timed out. Devices found: ${_foundDevices.value.size}")
                if (_foundDevices.value.isEmpty())
                    showToast("No devices found. Make sure your sensor is on.")
            }
        }
    }

    fun stopScan() {
        if (canScan()) btAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        _bleState.value  = BleState.IDLE
        _statusMsg.value = "Tap 'Scan for Devices' to find your sensor"
        Log.d(BLE_TAG, "Scan stopped by user")
    }

    fun connectTo(device: BleDevice, context: Context) {
        stopScan()
        _bleState.value      = BleState.CONNECTING
        _connectedName.value = device.name
        _statusMsg.value     = "Connecting to ${device.name}..."
        lastConnectedDevice  = device          // ← add this
        reconnectAttempts    = 0               // ← add this
        Log.d(BLE_TAG, "Connecting to ${device.name} (${device.address})...")
        if (canConnect()) {
            gatt = device.device.connectGatt(context, false, gattCallback)
        }
    }

    fun disconnect() {
        isIntentionalDisconnect = true    // ← add this
        reconnectAttempts = 0             // ← add this
        if (canConnect()) gatt?.disconnect()
    }

    fun onPermissionDenied() {
        Log.w(BLE_TAG, "Bluetooth permissions denied by user")
        _bleState.value = BleState.ERROR
        showToast("Bluetooth permission is required to scan for sensors.")
    }

    fun onBluetoothNotEnabled() {
        Log.w(BLE_TAG, "Bluetooth not enabled by user")
        _bleState.value = BleState.ERROR
        showToast("Please turn on Bluetooth to pair your sensor.")
    }

    override fun onCleared() {
        super.onCleared()
        if (canScan()) btAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        if (canConnect()) gatt?.disconnect()
        gatt?.close()
        gatt = null
    }
}
