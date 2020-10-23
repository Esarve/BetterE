/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.utils

import android.content.Context
import com.sourav.bettere.db.ChargingDB
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.repository.HistoryRepository
import com.sourav.bettere.repository.LogRepository

class RoomHelper private constructor(context: Context){

    private var logRepository: LogRepository
    private var historyRepository: HistoryRepository

    companion object {
        private var instance: RoomHelper? = null

        fun getInstance(context: Context): RoomHelper {
            if (instance == null) {
                instance = RoomHelper(context)

                return instance as RoomHelper
            }
            return instance as RoomHelper
        }
    }

    init {
        val logDao = ChargingDB.getInstance(context).logDao()
        val historyDao = ChargingDB.getInstance(context).historyDao()

        logRepository = LogRepository(logDao)
        historyRepository = HistoryRepository(historyDao)
    }



    suspend fun getChargingLog(): List<ChargingLog> {
        return logRepository.realAllData()
    }
}