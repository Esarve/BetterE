/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.sourav.bettere.service.ChargeLoggerService
import com.sourav.bettere.utils.Constants
import java.lang.Exception

class StartOnBootBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("BOOTCAST", "onReceive: FIRED")
        val cdTime = intent?.getStringExtra("cdTime")

        val serviceIntent = Intent(context, ChargeLoggerService::class.java)
        serviceIntent.putExtra("cdTime", cdTime)
        if (checkOnBoot(context)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context!!.startForegroundService(serviceIntent)
            } else {
                context!!.startService(serviceIntent)
            }
            Toast.makeText(context, "SERVICE STARTED", Toast.LENGTH_LONG).show()
        }else
            Toast.makeText(context, "HAHAHA SERVICE WONT START", Toast.LENGTH_LONG).show()
    }

    private fun checkOnBoot(context: Context?): Boolean{
        return try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.getBoolean(Constants.PREF_BOOT_KEY,false)
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
}