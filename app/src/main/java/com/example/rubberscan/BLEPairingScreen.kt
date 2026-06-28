package com.example.rubberscan

import android.annotation.SuppressLint
import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.example.rubberscan.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "BLEPairing"

// ── BLE constants (must match ESP32 firmware) ─────────────────────────────
const val TARGET_DEVICE_NAME = "RubberSense"
val SERVICE_UUID: UUID       = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b")
val SENSOR_CHAR_UUID: UUID   = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8")
val CLIENT_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

// ── State & model ─────────────────────────────────────────────────────────
enum class BleState { IDLE, SCANNING, CONNECTING, CONNECTED, DISCONNECTED, ERROR }

data class BleDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val device: BluetoothDevice
)

// ── Screen ────────────────────────────────────────────────────────────────
@SuppressLint("MissingPermission")
@Composable
fun BLEPairingScreen(viewModel: BleViewModel, onBack: () -> Unit = {}) {
    val context = LocalContext.current

    val bleState     by viewModel.bleState.collectAsState()
    val foundDevices by viewModel.foundDevices.collectAsState()
    val connectedName by viewModel.connectedName.collectAsState()
    val temperature  by viewModel.temperature.collectAsState()
    val humidity     by viewModel.humidity.collectAsState()
    val statusMsg    by viewModel.statusMsg.collectAsState()
    val toastMsg     by viewModel.toastMsg.collectAsState()

    val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val btAdapter = btManager.adapter

    // ── Permissions ───────────────────────────────────────────────────────
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    var hasPermissions by remember {
        mutableStateOf(requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasPermissions = results.values.all { it }
        if (!hasPermissions) viewModel.onPermissionDenied()
    }

    val enableBtLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (btAdapter?.isEnabled != true) viewModel.onBluetoothNotEnabled()
    }

    fun startScan() {
        if (!hasPermissions) { permissionLauncher.launch(requiredPermissions); return }
        if (btAdapter?.isEnabled != true) {
            @Suppress("DEPRECATION")
            enableBtLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            return
        }
        viewModel.startScan()
    }

    // ── UI ────────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8F1))
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B5E20))
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Pair Sensor", color = Color.White,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Scrollable body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Status card ───────────────────────────────────────────────
            BleStatusCard(bleState, statusMsg, connectedName)

            // ── Connected: sensor readings + disconnect ───────────────────
            if (bleState == BleState.CONNECTED) {
                LiveSensorCard(temperature, humidity)
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    onClick = { viewModel.disconnect() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFC62828)
                    )
                ) {
                    Icon(Icons.Default.BluetoothDisabled, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Disconnect Sensor")
                }
            }

            // ── Not connected: scan button ────────────────────────────────
            if (bleState != BleState.CONNECTED) {
                // Device list or empty hint
                if (foundDevices.isNotEmpty()) {
                    Text("Found Devices",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A4A))
                    foundDevices.forEach { device ->
                        BleDeviceCard(
                            device      = device,
                            isTarget    = device.name.contains(TARGET_DEVICE_NAME, ignoreCase = true),
                            isConnecting = bleState == BleState.CONNECTING,
                            onClick     = { viewModel.connectTo(device, context) }
                        )
                    }
                } else if (bleState == BleState.IDLE || bleState == BleState.ERROR) {
                    BleEmptyHint()
                }
                Spacer(Modifier.weight(1f))
                ScanButton(
                    bleState = bleState,
                    onScan   = { startScan() },
                    onStop   = { viewModel.stopScan() }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    // ── 2-second toast at the bottom ─────────────────────────────────────
    AnimatedVisibility(
        visible = toastMsg.isNotEmpty(),
        enter   = fadeIn(),
        exit    = fadeOut(),
        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
    ) {
        Surface(
            shape  = RoundedCornerShape(24.dp),
            color  = Color(0xFF1C1C1C).copy(alpha = 0.85f),
            shadowElevation = 6.dp
        ) {
            Text(
                text     = toastMsg,
                color    = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }
    } // end Box
}

// ── Status Card ───────────────────────────────────────────────────────────
private data class StatusStyle(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val bgColor: Color
)

@Composable
private fun BleStatusCard(state: BleState, message: String, deviceName: String) {
    val style = when (state) {
        BleState.CONNECTED    -> StatusStyle("Connected",     Icons.Default.BluetoothConnected, Color(0xFF1B5E20), Color(0xFFE8F5E9))
        BleState.SCANNING     -> StatusStyle("Scanning…",     Icons.Default.Bluetooth,          Color(0xFF0D47A1), Color(0xFFE3F2FD))
        BleState.CONNECTING   -> StatusStyle("Connecting…",   Icons.Default.Bluetooth,          Color(0xFFE65100), Color(0xFFFFF3E0))
        BleState.DISCONNECTED -> StatusStyle("Disconnected",  Icons.Default.BluetoothDisabled,  Color(0xFFC62828), Color(0xFFFFEBEE))
        BleState.ERROR        -> StatusStyle("Error",         Icons.Default.Warning,            Color(0xFFC62828), Color(0xFFFFEBEE))
        BleState.IDLE         -> StatusStyle("Ready",         Icons.Default.Bluetooth,          Color(0xFF37474F), Color(0xFFF5F5F5))
    }

    // Spinning animation when scanning
    val infiniteTransition = rememberInfiniteTransition(label = "ble_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "rotation"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(style.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    style.icon,
                    contentDescription = null,
                    tint = style.iconColor,
                    modifier = Modifier
                        .size(28.dp)
                        .then(if (state == BleState.SCANNING) Modifier.rotate(rotation) else Modifier)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(style.title, fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = Color(0xFF1C1C1C))
                Spacer(Modifier.height(2.dp))
                Text(message, color = Color(0xFF9E9E9E),
                    fontSize = 12.sp, lineHeight = 17.sp)
                if (state == BleState.CONNECTED && deviceName.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFE8F5E9)) {
                        Text(deviceName, color = Color(0xFF1B5E20),
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

// ── Live Sensor Card ──────────────────────────────────────────────────────
@Composable
private fun LiveSensorCard(temperature: Float?, humidity: Float?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Live Sensor Readings",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = Color(0xFF4A4A4A))
                // Pulsing live dot
                val infiniteTransition = rememberInfiniteTransition(label = "live_dot")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                    label = "alpha"
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50).copy(alpha = alpha))
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("LIVE", color = Color(0xFF4CAF50),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Temperature
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape)
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Thermostat, contentDescription = null,
                            tint = Color(0xFFE65100), modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (temperature != null) "%.1f°C".format(temperature) else "—",
                        fontWeight = FontWeight.Bold, fontSize = 22.sp,
                        color = if (temperature != null) Color(0xFFE65100) else Color(0xFF9E9E9E)
                    )
                    Text("Temperature", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                }

                Box(
                    modifier = Modifier.width(1.dp).height(64.dp)
                        .align(Alignment.CenterVertically)
                        .background(Color(0xFFF0F0F0))
                )

                // Humidity
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null,
                            tint = Color(0xFF0D47A1), modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (humidity != null) "%.1f%%".format(humidity) else "—",
                        fontWeight = FontWeight.Bold, fontSize = 22.sp,
                        color = if (humidity != null) Color(0xFF0D47A1) else Color(0xFF9E9E9E)
                    )
                    Text("Humidity", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                }
            }
        }
    }
}

// ── Scan Button ───────────────────────────────────────────────────────────
@Composable
private fun ScanButton(
    bleState: BleState,
    onScan: () -> Unit,
    onStop: () -> Unit
) {
    val isScanning   = bleState == BleState.SCANNING
    val isConnecting = bleState == BleState.CONNECTING

    val infiniteTransition = rememberInfiniteTransition(label = "scan_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "spin"
    )

    Button(
        onClick  = { if (isScanning) onStop() else onScan() },
        enabled  = !isConnecting,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape    = RoundedCornerShape(50.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = if (isScanning) Color(0xFF37474F) else Color(0xFF1B5E20)
        )
    ) {
        if (isScanning) {
            Icon(Icons.Default.Refresh, contentDescription = null,
                modifier = Modifier.size(18.dp).rotate(rotation))
        } else {
            Icon(Icons.Default.Bluetooth, contentDescription = null,
                modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = when {
                isConnecting -> "Connecting…"
                isScanning   -> "Stop Scanning"
                else         -> "Scan for Devices"
            },
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Device Card ───────────────────────────────────────────────────────────
@Composable
private fun BleDeviceCard(
    device: BleDevice,
    isTarget: Boolean,
    isConnecting: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isTarget) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier  = Modifier.fillMaxWidth()
            .clickable(enabled = !isConnecting) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isTarget) Color(0xFF1B5E20) else Color(0xFF546E7A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bluetooth, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(device.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color(0xFF1C1C1C))
                    if (isTarget) {
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFF1B5E20)) {
                            Text("Your Sensor", color = Color.White, fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Text(device.address, color = Color(0xFF9E9E9E), fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${device.rssi} dBm",
                    color = Color(0xFF9E9E9E), fontSize = 10.sp)
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isTarget) Color(0xFF1B5E20) else Color(0xFF546E7A)
                ) {
                    Text("Connect", color = Color.White,
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }
        }
    }
}

// ── Empty Hint ────────────────────────────────────────────────────────────
@Composable
private fun BleEmptyHint() {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.satellite_dish),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                colorFilter = ColorFilter.tint(Color(0xFF4A4A4A))
            )
            Spacer(Modifier.height(10.dp))
            Text("No devices found yet",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp, color = Color(0xFF4A4A4A))
            Spacer(Modifier.height(4.dp))
            Text(
                "Make sure your sensor is powered on\nand within Bluetooth range (~10 m)",
                color = Color(0xFF9E9E9E), fontSize = 12.sp,
                textAlign = TextAlign.Center, lineHeight = 18.sp
            )
        }
    }
}
