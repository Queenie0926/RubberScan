package com.example.rubberscan.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rubberscan.db.entity.Plantation
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantationDao {
    @Upsert
    suspend fun upsert(plantation: Plantation)

    @Query("SELECT * FROM plantation WHERE userId = :userId")
    fun observedPlantation(userId: String): Flow<Plantation?>

    @Query("SELECT * FROM plantation WHERE userId = :userId")
    suspend fun getPlantation(userId: String): Plantation?

    @Query("DELETE FROM plantation WHERE userId = :userId")
    suspend fun deletePlantation(userId: String)
}