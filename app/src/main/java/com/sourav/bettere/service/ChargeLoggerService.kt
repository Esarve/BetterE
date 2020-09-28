package com.sourav.bettere.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sourav.bettere.App.Companion.CHANNEL_ID
import com.sourav.bettere.R
import com.sourav.bettere.broadcasts.ChargingBroadcast
import com.sourav.bettere.listeners.OnChargingListener
import com.sourav.bettere.utils.Constants

class ChargeLoggerService : Service(), OnChargingListener {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var receiver: ChargingBroadcast

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
        Log.d(Constants.SERVICE, "ON Charging: Fired")
        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Charging"))
        showNotification(1)
    }

    override fun onDischarging() {
        Log.d(Constants.SERVICE, "On Discharging: Fired")
        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Discharging"))
        showNotification(1)
    }

    private fun showNotification(id: Int) {
        with(NotificationManagerCompat.from(this)) {
            notify(id, buildNotification())
        }
    }

}