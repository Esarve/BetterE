package com.sourav.bettere.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import com.sourav.bettere.listeners.OnChargingListener
import com.sourav.bettere.utils.Constants

class ChargingBroadcast : BroadcastReceiver() {
    private var stillCharging: Boolean = false
    private var stilldischarging: Boolean = false

    companion object {
        lateinit var mContext: Context
        fun getinstance(context: Context): ChargingBroadcast {
            mContext = context
            return ChargingBroadcast()
        }
    }

    var listenerOnCharging: OnChargingListener? = null

    fun setOnChargingListener(context: OnChargingListener?) {
        listenerOnCharging = context as OnChargingListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(Constants.SERVICE, "Broadcast RUN")
        val isPresent = intent?.getBooleanExtra("present", false)
        val bundle = intent?.extras
        val string = bundle.toString()
        Log.d(Constants.SERVICE, "Battery Info: $string")

        if (isPresent!!) {
            val plugged = bundle?.getInt(BatteryManager.EXTRA_PLUGGED, 0)
            val volt: Double? = bundle?.getInt("voltage")?.div(1000.0)
            val level: Int? = bundle?.getInt("level")
            val temp: Double? = bundle?.getInt("temperature")?.div(10.0)

            try {
                getChargingStatus(plugged!!)
                listenerOnCharging!!.onReceive(volt!!, level!!, temp!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun getChargingStatus(plugged: Int) {
        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> {
                if (!stillCharging) {
                    Log.d(Constants.SERVICE, "CHARGING")
                    Toast.makeText(mContext, "CHARGING", Toast.LENGTH_LONG).show()
                    listenerOnCharging!!.onCharging()
                }
                stillCharging = true
                stilldischarging = false
            }
            BatteryManager.BATTERY_PLUGGED_USB -> {
            }
            else -> {
                if (!stilldischarging) {
                    Log.d(Constants.SERVICE, "DISCHARGING")
                    Toast.makeText(mContext, "DISCHARGING", Toast.LENGTH_LONG).show()
                    listenerOnCharging!!.onDischarging()
                }
                stilldischarging = true
                stillCharging = false
            }
        }
    }

}