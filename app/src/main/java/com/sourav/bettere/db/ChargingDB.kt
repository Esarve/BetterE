/*
 * Copyright 2020 Sourav Das
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
                        context,
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