/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.repository

import androidx.lifecycle.LiveData
import com.sourav.bettere.db.dao.HistoryDao
import com.sourav.bettere.db.entity.ChargingHistory

class HistoryRepository(private val historyDao: HistoryDao) {

    val readAllData: LiveData<List<ChargingHistory>> = historyDao.readALlData()

    suspend fun getLastCycle():Long{
        return historyDao.getLastCycle()
    }

    suspend fun addHistory(chargingHistory: ChargingHistory) {
        historyDao.addHistory(chargingHistory)
    }
}