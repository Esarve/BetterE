package com.sourav.bettere.utils

import android.content.Context
import com.sourav.bettere.db.ChargingDB
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.db.repository.HistoryRepository
import com.sourav.bettere.db.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RoomHelper private constructor(context: Context){

    private var logRepository: LogRepository
    private var historyRepository: HistoryRepository

    companion object {
        private var instance: RoomHelper? = null

        fun getInstance(context: CoroutineScope): RoomHelper {
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

    suspend fun getLastCycle(): Long{
        return historyRepository.getLastCycle()
    }


    fun getChargingLog(): List<ChargingLog>{
        return logRepository.readAllData
    }
}