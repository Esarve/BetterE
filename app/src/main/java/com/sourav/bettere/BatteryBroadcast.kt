package com.sourav.bettere

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class BatteryBroadcast: BroadcastReceiver() {
    var listenerC : AmpReceived? = null
    var listenerV : VoltReceived? = null

    public fun setAmpReceived(context: Context?){
        listenerC = context as AmpReceived;
    }

    public fun setVoltReceived(context: Context?) {
        listenerV = context as VoltReceived
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
            val percent =  level?.times(100)?.div(scale!!)
            var volt:Double? = bundle?.getInt("voltage")?.div(1000.0);
            listenerV?.onVoltReceived(volt.toString())
            Log.d("Current", "voltage: $volt")
            listenerC?.onAmpReceived(mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).div(1000).toString())

        }

    }
}