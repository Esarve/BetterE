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
    override fun onReceive(context: Context?, intent: Intent?) {
        val intent = Intent(context, ChargeLoggerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context!!.startForegroundService(intent)
        } else {
            context!!.startService(intent)
        }
        Toast.makeText(context, "SERVICE STARTED", Toast.LENGTH_LONG).show()
    }
}