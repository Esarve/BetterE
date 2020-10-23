/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {

    companion object {
        const val CHANNEL_ID: String = "ServiceNotification"
    }

    override fun onCreate() {
        super.onCreate()
        CreateNotificationChannel()
    }

    private fun CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(notificationChannel)
        }
    }
}