package com.example.rubberscan

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("rubberscan_prefs", Context.MODE_PRIVATE)

    private val _notifications = MutableStateFlow(prefs.getBoolean("notifications", true))
    val notifications: StateFlow<Boolean> = _notifications

    private val _diseaseAlerts = MutableStateFlow(prefs.getBoolean("disease_alerts", true))
    val diseaseAlerts: StateFlow<Boolean> = _diseaseAlerts

    private val _weatherAlerts = MutableStateFlow(prefs.getBoolean("weather_alerts", false))
    val weatherAlerts: StateFlow<Boolean> = _weatherAlerts

    private val _autoReconnect = MutableStateFlow(prefs.getBoolean("auto_reconnect", true))
    val autoReconnect: StateFlow<Boolean> = _autoReconnect

    private val _language = MutableStateFlow(prefs.getString("language", "English") ?: "English")
    val language: StateFlow<String> = _language

    fun setNotifications(enabled: Boolean) {
        _notifications.value = enabled
        prefs.edit().putBoolean("notifications", enabled).apply()
    }

    fun setDiseaseAlerts(enabled: Boolean) {
        _diseaseAlerts.value = enabled
        prefs.edit().putBoolean("disease_alerts", enabled).apply()
    }


    fun setAutoReconnect(enabled: Boolean) {
        _autoReconnect.value = enabled
        prefs.edit().putBoolean("auto_reconnect", enabled).apply()
    }

}