package com.sourav.bettere.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charging_log")
data class ChargingLog(
    @PrimaryKey
    val timestamp: Long,
    val cycle: Long,
    val percentage: Int,
    val current: Long,
    val voltage: Double,
    val temp: Double,
)