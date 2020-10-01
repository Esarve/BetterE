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
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.listeners.OnChargingListener
import com.sourav.bettere.utils.Constants
import com.sourav.bettere.utils.RoomHelper
import kotlinx.coroutines.*

class ChargeLoggerService : Service(), OnChargingListener {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var receiver: ChargingBroadcast
    private lateinit var mBatteryManager: BatteryManager
    private lateinit var roomHelper: RoomHelper

    private var cycle: Long = 0
    private var startTime: Long = 0
    private var startedP = -1
    private var endedP = -1

    private var isCharging: Boolean = false
    private var isFinished: Boolean = true
    private var isRecorded: Boolean = false


    override fun onCreate() {
        super.onCreate()
        initNotificationManager()
        initNotification()
        loadBroadcastReceiver()
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
        Log.d(Constants.SERVICE, "loadBroadcastReceiver: Finished")
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
        isCharging = true
        roomHelper = RoomHelper.getInstance(this)

        Log.d(Constants.SERVICE, "ON Charging: Logging Started")
        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Charging: Logging Started"))
        showNotification(1)

        mBatteryManager = this.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        GlobalScope.launch(Dispatchers.IO) {
            GlobalScope.async {

            }
            cycle =  RoomHelper.getInstance(this).getLastCycle()
        }

        if (isFinished){
            Log.d(Constants.SERVICE, "ON Charging: Previous Charging Finished. New cycle will begin.")
            startTime = System.currentTimeMillis()
            cycle++
        }

    }

    override fun onDischarging() {
        isCharging = false

        Log.d(Constants.SERVICE, "On Discharging: Fired")
        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Discharging"))
        showNotification(1)

        object : CountDownTimer(300000, 10000){
            override fun onTick(p0: Long) {
                isFinished = false
            }

            override fun onFinish() {
                finishLog()
            }
        }.start()
    }

    private fun finishLog() {
        if (!isCharging){
            isFinished = true
            isRecorded = false
            logHistory()
        }

    }

    private fun logHistory() {
        val chargingHistory = ChargingHistory(
            cycle,
            startTime,
            System.currentTimeMillis(),
            startedP,
            endedP
        )

        roomHelper.addHistoryData(chargingHistory)
    }

    override fun onReceive(voltage: Double, percentage: Int, temp: Double) {
        if (isCharging) {
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

        val chargingLog = ChargingLog(
            System.currentTimeMillis(),
            cycle,
            percentage,
            getCurrent(),
            voltage,
            temp
        )

       roomHelper.addLogData(chargingLog)
    }

    private fun getCurrent(): Long {
        return  mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            .div(1000)
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