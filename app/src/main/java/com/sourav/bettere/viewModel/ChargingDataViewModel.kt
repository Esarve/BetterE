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

package com.sourav.bettere.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sourav.bettere.db.ChargingDB
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.repository.HistoryRepository
import com.sourav.bettere.repository.LogRepository

class ChargingDataViewModel(application: Application): AndroidViewModel(application) {

    val readHistoryData: LiveData<List<ChargingHistory>>

    private val historyRepository: HistoryRepository
    private val logRepository: LogRepository

    init {
        val logDao = ChargingDB.getInstance(application).logDao()
        val historyDao = ChargingDB.getInstance(application).historyDao()

        historyRepository = HistoryRepository(historyDao)
        logRepository = LogRepository(logDao)

        readHistoryData = historyRepository.readAllData

    }
}