package com.sourav.bettere.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.SystemClock.sleep
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sourav.bettere.R
import com.sourav.bettere.utils.Constants

class ChargingLogger(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private lateinit var builder: Notification
    private lateinit var manager: NotificationManager

    override fun doWork(): Result {
        Log.d(Constants.GRAPH, "doWork: WORKING")
        displayPersistentNotification()
        //heavywork()
        return Result.success()
    }

    fun heavywork() {
        for (i in 1..20) {
            sleep(1000)
            Log.d(Constants.GRAPH, "doWork:HEAVILY WORKING")
            updateNotification(i.toString())
        }
    }

    fun displayPersistentNotification() {
        manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel: NotificationChannel =
                NotificationChannel("logging", "Logging", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(notificationChannel)

        }

        builder = NotificationCompat.Builder(applicationContext, "logging")
            .setContentTitle("Charging Monitoring")
            .setContentText("BetterE is monitoring your charging")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()

        displayPersistentNotification()
    }

    fun updateNotification(text: String) {
        builder =
            NotificationCompat.Builder(applicationContext, "logging").setContentText("text").build()
        displayNotification()
    }

    fun displayNotification() {
        manager.notify(1, builder);
    }
}