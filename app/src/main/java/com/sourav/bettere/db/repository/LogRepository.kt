package com.sourav.bettere.db.repository

import androidx.lifecycle.LiveData
import com.sourav.bettere.db.dao.LogDao
import com.sourav.bettere.db.entity.ChargingLog

class LogRepository(private val logDao: LogDao) {

    val readAllData: LiveData<List<ChargingLog>> = logDao.readALlData()

    suspend fun addLog(chargingLog: ChargingLog) {
        logDao.addLog(chargingLog)
    }
}