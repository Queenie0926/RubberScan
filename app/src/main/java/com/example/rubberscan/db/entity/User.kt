package com.example.rubberscan.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId: String,
    val name: String,
    val email: String,
    val profilePicUrl: String = ""
)
