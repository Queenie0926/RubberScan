package com.example.rubberscan.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rubberscan.db.entity.ScanHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistory): Long

    @Query("SELECT * FROM scan_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getScansForUser(userId: String): Flow<List<ScanHistory>>

    @Query("SELECT * FROM scan_history WHERE id = :scanId LIMIT 1")
    suspend fun getScanById(scanId: Int): ScanHistory?

    @Query("DELETE FROM scan_history WHERE id = :scanId")
    suspend fun deleteScan(scanId: Int)

    @Query("DELETE FROM scan_history WHERE userId = :userId")
    suspend fun deleteAllScansForUser(userId: String)
}
