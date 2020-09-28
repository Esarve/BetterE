package com.sourav.bettere.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import com.sourav.bettere.listeners.ChargingStatus
import com.sourav.bettere.listeners.OnChargingListener
import com.sourav.bettere.listeners.VoltReceived
import com.sourav.bettere.utils.Constants

class BatteryBroadcast: BroadcastReceiver() {

    var listenerV : VoltReceived? = null
    var listenerChargingStatus: ChargingStatus? = null
    var listenerOnCharging: OnChargingListener? = null

    fun setVoltReceived(context: VoltReceived?) {
        listenerV = context as VoltReceived
    }

    fun setChargingStatus(context: ChargingStatus?){
        listenerChargingStatus = context as ChargingStatus
    }

    fun setOnChargingListener(context: OnChargingListener?){
        listenerOnCharging = context as OnChargingListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(Constants.BROADCAST, "Broadcast RUN")

        val isPresent = intent?.getBooleanExtra("present", false)

        val bundle = intent?.extras

        val string = bundle.toString()
        Log.d(Constants.BROADCAST, "Battery Info: $string")

        if (isPresent!!){
            val volt:Double? = bundle?.getInt("voltage")?.div(1000.0);
            val plugged = bundle?.getInt(BatteryManager.EXTRA_PLUGGED, 0)
            listenerV!!.onVoltReceived(volt.toString())
            Log.d(Constants.BROADCAST, "voltage: $volt")
            listenerChargingStatus!!.onChargingStatusChange(getChargingStatus(plugged!!))
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