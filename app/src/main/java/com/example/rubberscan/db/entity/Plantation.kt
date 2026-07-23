package com.example.rubberscan.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plantation")

data class Plantation(
    @PrimaryKey val userId: String,
    val name: String,
    val region: String,
    val province: String,
    val city: String,
    val barangay: String,
    val address: String = "",
)
