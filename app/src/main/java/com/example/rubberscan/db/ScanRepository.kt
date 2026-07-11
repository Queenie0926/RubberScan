package com.example.rubberscan.db

import com.example.rubberscan.db.dao.ScanHistoryDao
import com.example.rubberscan.db.dao.UserDao
import com.example.rubberscan.db.entity.ScanHistory
import com.example.rubberscan.db.entity.User
import kotlinx.coroutines.flow.Flow

class ScanRepository(
    private val userDao: UserDao,
    private val scanHistoryDao: ScanHistoryDao
) {
    // User
    suspend fun saveUser(user: User) = userDao.upsertUser(user)
    suspend fun getUser(userId: String): User? = userDao.getUserById(userId)
    suspend fun deleteUser(userId: String) = userDao.deleteUser(userId)

    // Scan history
    suspend fun saveScan(scan: ScanHistory): Long = scanHistoryDao.insertScan(scan)
    fun getScansForUser(userId: String): Flow<List<ScanHistory>> = scanHistoryDao.getScansForUser(userId)
    suspend fun getScanById(scanId: Int): ScanHistory? = scanHistoryDao.getScanById(scanId)
    suspend fun deleteScan(scanId: Int) = scanHistoryDao.deleteScan(scanId)
    suspend fun clearHistory(userId: String) = scanHistoryDao.deleteAllScansForUser(userId)


}
