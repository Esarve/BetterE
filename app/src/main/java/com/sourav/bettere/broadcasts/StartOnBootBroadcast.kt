/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.sourav.bettere.service.ChargeLoggerService

class StartOnBootBroadcast : BroadcastReceiver() {

    companion object {
        private var instance: StartOnBootBroadcast? = null
        fun getinstance(): StartOnBootBroadcast {
            if (instance == null) {
                instance = StartOnBootBroadcast()
            }
            return instance as StartOnBootBroadcast
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val cdTime = intent?.getStringExtra("cdTime")

        val serviceIntent = Intent(context, ChargeLoggerService::class.java)
        serviceIntent.putExtra("cdTime", cdTime)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context!!.startForegroundService(serviceIntent)
        } else {
            context!!.startService(serviceIntent)
        }
        Toast.makeText(context, "SERVICE STARTED", Toast.LENGTH_LONG).show()
    }
}