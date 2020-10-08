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

package com.sourav.bettere.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sourav.bettere.App.Companion.CHANNEL_ID
import com.sourav.bettere.R
import com.sourav.bettere.broadcasts.ChargingBroadcast
import com.sourav.bettere.db.ChargingDB
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.repository.HistoryRepository
import com.sourav.bettere.repository.LogRepository
import com.sourav.bettere.listeners.OnChargingListener
import com.sourav.bettere.utils.Constants
import com.sourav.bettere.utils.RoomHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChargeLoggerService : Service(), OnChargingListener {
    private val TAG = Constants.SERVICE
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var receiver: ChargingBroadcast
    private lateinit var mBatteryManager: BatteryManager
    private lateinit var roomHelper: RoomHelper

    private var logRepository: LogRepository
    private var historyRepository: HistoryRepository

    private var lastCurrent: Long = -1L
    private var lastLevel: Int = -1

    private var cycle: Long = 0
    private var startTime: Long = 0
    private var startedP = -1
    private var endedP = -1

    private var isCharging: Boolean = false
    private var isFinished: Boolean = true
    private var isRecorded: Boolean = false


    override fun onCreate() {
        super.onCreate()
        roomHelper = RoomHelper.getInstance(this)
        initNotificationManager()
        initNotification()
        loadBroadcastReceiver()
    }

    init {
        val logDao = ChargingDB.getInstance(this).logDao()
        val historyDao = ChargingDB.getInstance(this).historyDao()

        logRepository = LogRepository(logDao)
        historyRepository = HistoryRepository(historyDao)

    }

    private fun initNotification() {
        builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_offline_bolt_24)
            .setContentTitle(Constants.TITLE)
            .setContentText(Constants.BODY)
            .setStyle(NotificationCompat.BigTextStyle().bigText(Constants.BODY))
            .setOnlyAlertOnce(true)
            .setOngoing(true)
    }

    private fun initNotificationManager() {
        manager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(NotificationManager::class.java)
        } else getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun loadBroadcastReceiver() {
        Log.d(Constants.DEFAULT, "Current Thread ${Thread.currentThread().name}")
        receiver = ChargingBroadcast.getinstance(this)
        receiver.setOnChargingListener(this)
        this.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        Log.d(TAG, "loadBroadcastReceiver: Finished")
    }

    private fun buildNotification(): Notification {
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        buildNotification()
        startForeground(1, buildNotification())
        return START_NOT_STICKY
    }

    override fun onCharging() {
        Log.d(TAG, "ON Charging: Logging Started")
        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Charging: Logging Started"))
        showNotification(1)

        mBatteryManager = this.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        GlobalScope.launch(Dispatchers.IO) {
            try {
                cycle = historyRepository.getLastCycle()
            } catch (e: Exception) {
                cycle = 0L
                e.printStackTrace()
            }

            Log.d(TAG, "onCharging: CYCLE FOUND $cycle")

            if (isFinished) {
                Log.d(TAG, "Current Value of Cycle is $cycle")
                Log.d(TAG, "ON Charging: Previous Charging Finished. New cycle will begin.")
                startTime = System.currentTimeMillis()
                cycle++
                Log.d(TAG, "onCharging: Cycle Incremented")
            }
        }
        isCharging = true
    }

    override fun onDischarging() {
        Log.e(TAG, "On Discharging: Fired")
        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Discharging"))
        showNotification(1)

        if (isCharging) {
            object : CountDownTimer(60000, 10000) {
                override fun onTick(p0: Long) {
                    isFinished = false
                }

                override fun onFinish() {
                    finishLog()
                    Log.d(TAG, "onFinish: Secession Finished")
                }
            }.start()
        }
        isCharging = false
    }

    private fun finishLog() {
        if (!isCharging){
            isFinished = true
            isRecorded = false
            logHistory()

        }

    }

    private fun logHistory() {
        GlobalScope.launch(Dispatchers.IO) {
            val chargingHistory = ChargingHistory(
                cycle,
                startTime,
                System.currentTimeMillis(),
                startedP,
                endedP
            )
            historyRepository.addHistory(chargingHistory)
            Log.d(TAG, "finishLog: ")
        }

    }

    override fun onReceive(voltage: Double, percentage: Int, temp: Double) {
        Log.d(TAG, "onReceive: isCharging $isCharging")
        if (isCharging) {
            Log.d(TAG, "onReceive: Inside Charging Block")
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Charging: voltage: $voltage, Temp: $temp, Percentage: $percentage")
            )
            showNotification(1)

            if (!isRecorded){
                startedP = percentage
                isRecorded = true
            }
            endedP = percentage
            logCharge(voltage, percentage, temp)
        }
    }

    private fun logCharge(voltage: Double, percentage: Int, temp: Double) {
        if (validateCharge(percentage, getCurrent())) {
            GlobalScope.launch(Dispatchers.IO) {
                val current = getCurrent()
                val chargingLog = ChargingLog(
                    System.currentTimeMillis(),
                    cycle,
                    percentage,
                    current,
                    voltage,
                    temp
                )
                lastCurrent = current
                lastLevel = percentage
                logRepository.addLog(chargingLog)
                Log.d(TAG, "Added To DB: $chargingLog")
            }
        }

    }

    private fun validateCharge(percentage: Int, current: Long): Boolean {
        return !(lastCurrent == current && lastLevel == percentage)
    }

    private fun getCurrent(): Long {
        return mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            .div(1000).times(-1)
    }

    private fun showNotification(id: Int) {
        with(NotificationManagerCompat.from(this)) {
            notify(id, buildNotification())
        }
    }

    override fun onDestroy() {
        this.unregisterReceiver(receiver)
        super.onDestroy()
    }

}