/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import com.sourav.bettere.listeners.ChargingEventListener
import com.sourav.bettere.utils.Constants

class BatteryBroadcast: BroadcastReceiver() {

    var listener: ChargingEventListener? = null
    fun setChargingEventListener(context: ChargingEventListener) {
        listener = context as ChargingEventListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(Constants.BAT_BROADCAST, "Broadcast RUN")

        val isPresent = intent?.getBooleanExtra("present", false)

        val bundle = intent?.extras

        val string = bundle.toString()
        Log.d(Constants.BAT_BROADCAST, "Battery Info: $string")

        if (isPresent!!) {
            val volt: Double? = bundle?.getInt("voltage")?.div(1000.0);
            val level: Int? = bundle?.getInt("level")
            val temp: Double? = bundle?.getInt("temperature")?.div(10.0)
            val plugged = bundle?.getInt(BatteryManager.EXTRA_PLUGGED, 0)
            try {
                listener!!.onVoltageChange(volt.toString())
                listener!!.onBatteryPercentageChange(level.toString().plus("%"))
                listener!!.onTempChange(temp.toString().plus("\u2103"))
                Log.d(Constants.BAT_BROADCAST, "voltage: $volt")
                listener!!.onChargingStatusChange((getChargingStatus(plugged!!)))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    private fun getChargingStatus(plugged: Int):String{
        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> Constants.CHARGING_AC
            BatteryManager.BATTERY_PLUGGED_USB -> Constants.CHARGING_USB
            else -> Constants.DISCHARGING
        }
    }
}