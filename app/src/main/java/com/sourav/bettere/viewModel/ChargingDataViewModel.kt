package com.sourav.bettere.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sourav.bettere.db.ChargingDB
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.repository.HistoryRepository
import com.sourav.bettere.repository.LogRepository

class ChargingDataViewModel(application: Application): AndroidViewModel(application) {

    val readHistoryData: LiveData<List<ChargingHistory>>
    val readLogData: LiveData<List<ChargingLog>>

    private val historyRepository: HistoryRepository
    private val logRepository: LogRepository

    init {
        val logDao = ChargingDB.getInstance(application).logDao()
        val historyDao = ChargingDB.getInstance(application).historyDao()

        historyRepository = HistoryRepository(historyDao)
        logRepository = LogRepository(logDao)

        readHistoryData = historyRepository.readAllData
        readLogData = logRepository.readAllDataLive

    }
}