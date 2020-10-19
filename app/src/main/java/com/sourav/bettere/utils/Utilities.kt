/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.utils

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.data.Entry
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.model.PercentageAmpModel
import java.text.SimpleDateFormat
import java.util.*

class Utilities private constructor(context: Context) {

    private val TAG = Constants.UTILS
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private var mContext: Context? = null
        private var instance: Utilities? = null
        fun getInstance(context: Context): Utilities {
            if (instance == null) {
                instance = Utilities(context)
            }
            mContext = context
            return instance as Utilities
        }
    }

    fun writeToPref(
        type: String,
        key: String,
        valueStr: String = "",
        valueInt: Int = -1,
        valueBool: Boolean = false
    ) {
        with(
            PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
        ) {
            when (type) {
                Constants.PREF_TYPE_INT -> {
                    putInt(key, valueInt)
                    apply()
                    Log.d(
                        TAG,
                        "writeToPref: Success with type: $type key: $key, value: $valueInt"
                    )
                }
                Constants.PREF_TYPE_BOOL -> {
                    putBoolean(key, valueBool)
                    apply()
                    Log.d(
                        TAG,
                        "writeToPref: Success with type: $type key: $key, value: $valueBool"
                    )

                }
                Constants.PREF_TYPE_STRING -> {
                    putString(key, valueStr)
                    apply()
                    Log.d(
                        TAG,
                        "writeToPref: Success with type: $type key: $key, value: $valueStr"
                    )

                }
                else -> throw InputMismatchException("Wrong TYPE OR WHATEVER")
            }
        }
    }

    suspend fun <T> readPref(type: String, key: String): T {
        return when (type) {
            Constants.PREF_TYPE_STRING -> sharedPreferences.getString(key, "") as T
            Constants.PREF_TYPE_INT -> sharedPreferences.getInt(key, -1) as T
            Constants.PREF_TYPE_BOOL -> sharedPreferences.getBoolean(key, false) as T
            else -> throw InputMismatchException("WRONG TYPE BRUH")
        }
    }

    fun getDateTime(timestamp: Long): String {
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val now: Date = Date(timestamp)
        return sdf.format(now)
    }

    fun generateAverage(data: List<ChargingLog>): List<Entry> {
        val newList = arrayListOf<PercentageAmpModel>()
        val amps = arrayListOf<Long>()
        var currentP = data[0].percentage
        for (log in data) {
            if (log.percentage == currentP) {
                amps.add(log.current)
            } else {
                newList.add(
                    PercentageAmpModel(
                        log.percentage - 1,
                        amps.average().toLong()
                    )
                )
                currentP = log.percentage
                amps.clear()
            }
        }

        val entry = arrayListOf<Entry>()

        for (chargingLog in newList) {
            entry.add(
                Entry(
                    chargingLog.percentage.toFloat(),
                    chargingLog.current.toFloat()
                )
            )
        }

        return entry
    }

    fun <T> loadBroadcastReceiver(inst: T, intentFilter: IntentFilter) {
        Log.d(Constants.DEFAULT, "Current Thread ${Thread.currentThread().name}")
        mContext?.registerReceiver(inst as BroadcastReceiver, intentFilter)
    }

    @Suppress("DEPRECATION")
    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = mContext!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
