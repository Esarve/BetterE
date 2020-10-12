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

package com.sourav.bettere.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sourav.bettere.db.entity.ChargingLog

@Dao
interface LogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLog(obj: ChargingLog)

    @Query("SELECT * FROM charging_log ORDER BY percentage ASC")
    fun readALlDataLive(): LiveData<List<ChargingLog>>

    @Query("SELECT * FROM charging_log WHERE cycle=:x ORDER BY percentage ASC")
    fun readDataForAHistory(x: Long): List<ChargingLog>

    @Query("SELECT * FROM charging_log ORDER BY percentage ASC")
    fun readALlData(): List<ChargingLog>
}