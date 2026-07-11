package com.example.rubberscan.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_readings")
data class SensorReading(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,        // System.currentTimeMillis()
    val temperature: Float,
    val humidity: Float
)
