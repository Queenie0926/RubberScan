package com.example.rubberscan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log

object NotificationHelper {

    private const val CHANNEL_SCAN    = "rubberscan_scan_v3"   // ← bump again since sound source is changing
    private const val CHANNEL_SENSOR  = "rubberscan_sensor_v3"

    fun createChannels(context: Context) {
        val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.notification_ping}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_SCAN,
                    "Scan Results",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications for scan results"
                    setSound(soundUri, audioAttributes)
                    enableVibration(true)
                }
            )

            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_SENSOR,
                    "Sensor Status",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for BLE sensor connection status"
                    setSound(soundUri, audioAttributes)
                    enableVibration(true)
                }
            )
        }
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun notifyScanComplete(context: Context, result: String, severity: String?) {
        val isDisease = result != "Healthy"
        val title = if (isDisease) "⚠️ Disease Detected" else "✅ Scan Complete"
        val body  = if (isDisease)
            "$result detected — Severity: ${severity ?: "Unknown"}. Check the app for details."
        else
            "Leaf scan complete. No disease detected. The leaf appears healthy."

        notify(context, CHANNEL_SCAN, 1001, title, body)
    }

    fun notifySensorDisconnected(context: Context, deviceName: String) {
        notify(
            context, CHANNEL_SENSOR, 1002,
            "📡 Sensor Disconnected",
            "$deviceName has disconnected from RubberScan."
        )
    }

    fun notifyReconnectFailed(context: Context, deviceName: String, attempts: Int) {
        notify(
            context, CHANNEL_SENSOR, 1003,
            "❌ Reconnect Failed",
            "Could not reconnect to $deviceName after $attempts attempts. Please reconnect manually."
        )
    }

    private fun notify(
        context: Context,
        channelId: String,
        notifId: Int,
        title: String,
        body: String
    ) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent(context))
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notifId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted — silently skip
        }
    }
}