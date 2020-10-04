/*
 * Copyright 2020 Sourav Das
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sourav.bettere.utils

import android.content.Context
import com.sourav.bettere.db.ChargingDB
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.db.repository.HistoryRepository
import com.sourav.bettere.db.repository.LogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    fun addLogData(chargingLog: ChargingLog) {
        GlobalScope.launch(Dispatchers.IO) {
            logRepository.addLog(chargingLog)
        }
    }

    fun addHistoryData(chargingHistory: ChargingHistory) {
        GlobalScope.launch(Dispatchers.IO) {
            historyRepository.addHistory(chargingHistory)
        }
    }

    suspend fun getLastCycle(): Long {
        return historyRepository.getLastCycle()
    }


    suspend fun getChargingLog(): List<ChargingLog> {
        return logRepository.realAllData()
    }
}