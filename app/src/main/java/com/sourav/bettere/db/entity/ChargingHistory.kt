package com.sourav.bettere.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charging_history")
data class ChargingHistory(
    @PrimaryKey
    val pk: Int,
    val startTime: Long,
    val endTime: Long,
    val startedFrom: Int,
    val endedAt: Int,
    val totalCharged: Int
)