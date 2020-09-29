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

class RoomHelper {

    private lateinit var logRepository: LogRepository
    private lateinit var historyRepository: HistoryRepository

    companion object {
        private var mContext: Context? = null
        private var instance: RoomHelper? = null
        fun getInstance(context: Context): RoomHelper {
            if (instance == null) {
                instance = RoomHelper()
                mContext = context
                return instance as RoomHelper
            }
            return instance as RoomHelper
        }
    }

    init {
        val logDao = ChargingDB.getInstance(mContext!!).logDao()
        val historyDao = ChargingDB.getInstance(mContext!!).historyDao()

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


}