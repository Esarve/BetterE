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

package com.sourav.bettere.repository

import androidx.lifecycle.LiveData
import com.sourav.bettere.db.dao.HistoryDao
import com.sourav.bettere.db.entity.ChargingHistory

class HistoryRepository(private val historyDao: HistoryDao) {

    val readAllData: LiveData<List<ChargingHistory>> = historyDao.readALlData()

    suspend fun getLastCycle():Long{
        return historyDao.getLastCycle()
    }

    suspend fun addHistory(chargingHistory: ChargingHistory) {
        historyDao.addHistory(chargingHistory)
    }
}