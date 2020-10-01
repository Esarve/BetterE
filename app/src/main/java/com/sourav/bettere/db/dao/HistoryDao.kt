package com.sourav.bettere.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sourav.bettere.db.entity.ChargingHistory

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHistory(obj: ChargingHistory)

    @Query("SELECT * FROM charging_history ORDER BY cycle ASC")
    fun readALlData(): LiveData<List<ChargingHistory>>

    @Query("SELECT cycle FROM charging_history ORDER BY cycle DESC LIMIT 1")
    suspend fun getLastCycle(): Long
}