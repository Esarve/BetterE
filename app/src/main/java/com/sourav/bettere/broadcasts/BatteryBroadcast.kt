package com.sourav.bettere.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import com.sourav.bettere.listeners.ChargingStatus
import com.sourav.bettere.listeners.VoltReceived

class BatteryBroadcast: BroadcastReceiver() {
    var listenerV : VoltReceived? = null
    var listenerChargingStatus: ChargingStatus? = null

    public fun setVoltReceived(context: VoltReceived?) {
        listenerV = context as VoltReceived
    }

    public fun setChargingStatus(context: ChargingStatus?){
        listenerChargingStatus = context as ChargingStatus
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val mBatteryManager: BatteryManager = context?.getSystemService(Context.BATTERY_SERVICE) as BatteryManager


        Log.d("Current", "Broadcast RUN")

        val isPresent = intent?.getBooleanExtra("present", false)

        val bundle = intent?.extras

        val string = bundle.toString()
        Log.i("Current", "Battery Info: $string")

        if (isPresent!!){
            val volt:Double? = bundle?.getInt("voltage")?.div(1000.0);
            val plugged = bundle?.getInt(BatteryManager.EXTRA_PLUGGED, 0)
            listenerV?.onVoltReceived(volt.toString())
            Log.d("Current", "voltage: $volt")
            listenerChargingStatus?.onChargingStatusChange(getChargingStatus(plugged!!))
        }

    }

    private fun getChargingStatus(plugged: Int):String{
        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "Charging (AC)"
            BatteryManager.BATTERY_PLUGGED_USB -> "Charging (USB)"
            else -> "Discharging"
        }
    }
}