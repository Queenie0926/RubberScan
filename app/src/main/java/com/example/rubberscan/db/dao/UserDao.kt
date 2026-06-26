package com.example.rubberscan.db.dao

import androidx.room.*
import com.example.rubberscan.db.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long          // throws if email already exists

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?
}