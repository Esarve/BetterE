package com.sourav.bettere.db.repository

import androidx.lifecycle.LiveData
import com.sourav.bettere.db.dao.LogDao
import com.sourav.bettere.db.entity.ChargingLog

class LogRepository(private val logDao: LogDao) {

    val readAllDataLive: LiveData<List<ChargingLog>> = logDao.readALlDataLive()
    val readAllData: List<ChargingLog> = logDao.readALlData()

    suspend fun addLog(chargingLog: ChargingLog) {
        logDao.addLog(chargingLog)
    }
}