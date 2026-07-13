package com.example.rubberscan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AppNotification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val time: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
    val type: NotifType = NotifType.INFO,
    val isRead: Boolean = false
)

enum class NotifType { DISEASE, SENSOR, SCAN, INFO, TREATMENT}

class NotificationViewModel(app: Application) : AndroidViewModel(app) {

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    val unreadCount: Int get() = _notifications.value.count { !it.isRead }

    fun add(notification: AppNotification) {
        _notifications.value = listOf(notification) + _notifications.value
    }

    fun markAllRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clear() {
        _notifications.value = emptyList()
    }
}