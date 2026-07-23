package com.example.rubberscan.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rubberscan.db.dao.PlantationDao
import com.example.rubberscan.db.dao.ScanHistoryDao
import com.example.rubberscan.db.dao.SensorReadingDao
import com.example.rubberscan.db.dao.UserDao
import com.example.rubberscan.db.entity.ScanHistory
import com.example.rubberscan.db.entity.SensorReading
import com.example.rubberscan.db.entity.User
import com.example.rubberscan.db.entity.Plantation

@Database(
    entities = [User::class, ScanHistory::class, SensorReading::class, Plantation::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun sensorReadingDao(): SensorReadingDao
    abstract fun plantationDao(): PlantationDao


    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rubberscan.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}
