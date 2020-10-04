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

package com.sourav.bettere.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sourav.bettere.db.dao.HistoryDao
import com.sourav.bettere.db.dao.LogDao
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.utils.Constants

@Database(
    entities = [ChargingLog::class, ChargingHistory::class],
    version = Constants.DB_VER,
    exportSchema = true
)
abstract class ChargingDB : RoomDatabase() {

    abstract fun historyDao(): HistoryDao
    abstract fun logDao(): LogDao

    companion object {
        @Volatile
        private var INSTANCE: ChargingDB? = null

        fun getInstance(context: Context): ChargingDB {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ChargingDB::class.java,
                        "chargingDB"
                    )
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}