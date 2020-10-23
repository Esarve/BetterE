/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.repository

import androidx.lifecycle.LiveData
import com.sourav.bettere.db.dao.LogDao
import com.sourav.bettere.db.entity.ChargingLog

class LogRepository(private val logDao: LogDao) {

    val readAllDataLive: LiveData<List<ChargingLog>> = logDao.readALlDataLive()

    suspend fun readData(x: Long): List<ChargingLog> {
        return logDao.readDataForAHistory(x)
    }

    suspend fun realAllData(): List<ChargingLog> {
        return logDao.readALlData()
    }

    suspend fun addLog(chargingLog: ChargingLog) {
        logDao.addLog(chargingLog)
    }
}