package com.sourav.bettere.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sourav.bettere.R
import com.sourav.bettere.utils.Constants

class ChargingLogger(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d(Constants.GRAPH, "doWork: WORKING")
        displayPersistentNotification()
        return Result.success()
    }

    fun displayPersistentNotification(){
        val manager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel: NotificationChannel =
                NotificationChannel("logging", "Logging", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(notificationChannel)
        }

        val builder: Notification? = NotificationCompat.Builder(applicationContext,"logging")
            .setContentTitle("Charging Monitoring")
            .setContentText("BetterE is monitoring your charging")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()

        manager.notify(1,builder);

    }
}