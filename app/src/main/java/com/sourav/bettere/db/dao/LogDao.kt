/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sourav.bettere.db.entity.ChargingLog

@Dao
interface LogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLog(obj: ChargingLog)

    @Query("SELECT * FROM charging_log ORDER BY percentage ASC")
    fun readALlDataLive(): LiveData<List<ChargingLog>>

    @Query("SELECT * FROM charging_log WHERE cycle=:x ORDER BY percentage ASC")
    fun readDataForAHistory(x: Long): List<ChargingLog>

    @Query("SELECT * FROM charging_log ORDER BY percentage ASC")
    fun readALlData(): List<ChargingLog>
}