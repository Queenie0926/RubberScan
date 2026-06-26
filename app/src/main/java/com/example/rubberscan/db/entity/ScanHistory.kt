package com.example.rubberscan.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scan_history",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class ScanHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String,

    // Filled in by the model — left empty until model is integrated
    val diseaseName: String = "",
    val confidence: Float = 0f,
    val severity: String = "",

    // Environmental sensor readings from the DHT22 via BLE
    val temperature: Float = 0f,
    val humidity: Float = 0f,

    // Risk level and recommendation derived from disease + sensor data
    val riskLevel: String = "",
    val recommendation: String = ""
)
