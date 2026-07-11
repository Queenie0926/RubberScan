package com.example.rubberscan.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.rubberscan.db.entity.SensorReading

@Dao
interface SensorReadingDao {
    @Insert suspend fun insert(reading: SensorReading)

    @Query("SELECT * FROM sensor_readings WHERE timestamp >= :since ORDER BY timestamp ASC")
    suspend fun readingsSince(since: Long): List<SensorReading>

    @Query("DELETE FROM sensor_readings WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)   // retention
}