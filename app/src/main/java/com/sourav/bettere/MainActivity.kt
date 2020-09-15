package com.sourav.bettere

import android.app.ApplicationErrorReport
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlin.math.log

class MainActivity : AppCompatActivity(), AmpReceived, VoltReceived {
    private lateinit var receiver: BatteryBroadcast
    private lateinit var mBatteryManager: BatteryManager
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBatteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        job = GlobalScope.launch(Dispatchers.Main) {
            getCurrent()
        }

        receiver = BatteryBroadcast()

        receiver.setAmpReceived(this)
        receiver.setVoltReceived(this)
        this.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private suspend fun getCurrent(){
        val listener: AmpReceived = this
        while (true){
            val current =  mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).div(1000).toString()
            Log.d("Current", "Current: $current")

            Log.d("Current", "Current Thread ${Thread.currentThread().name}")
            listener.onAmpReceived(current.substring(1))
            delay(3000L)

        }
    }

    override fun onAmpReceived(value: String) {
        ampValue.text = value.plus(" mAh")
    }

    override fun onVoltReceived(value: String) {
        voltValue.text = value.plus(" V")
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        unregisterReceiver(receiver)
    }
}