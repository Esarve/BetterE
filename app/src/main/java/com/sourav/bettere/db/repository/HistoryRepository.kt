package com.sourav.bettere.db.repository

import androidx.lifecycle.LiveData
import com.sourav.bettere.db.dao.HistoryDao
import com.sourav.bettere.db.entity.ChargingHistory

class HistoryRepository(private val historyDao: HistoryDao) {

    val readAllData: LiveData<List<ChargingHistory>> = historyDao.readALlData()

    suspend fun addHistory(chargingHistory: ChargingHistory) {
        historyDao.addHistory(chargingHistory)
    }
}