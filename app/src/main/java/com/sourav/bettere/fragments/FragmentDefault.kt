package com.sourav.bettere.fragments

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourav.bettere.R
import com.sourav.bettere.broadcasts.BatteryBroadcast
import com.sourav.bettere.listeners.AmpReceived
import com.sourav.bettere.listeners.ChargingStatus
import com.sourav.bettere.listeners.VoltReceived
import kotlinx.android.synthetic.main.default_fragment.*
import kotlinx.coroutines.*

class FragmentDefault : Fragment(),
    AmpReceived,
    VoltReceived,
    ChargingStatus {
    private lateinit var mContext: Context
    private lateinit var receiver: BatteryBroadcast
    private lateinit var job: Job

    companion object {
        fun newInstance(): FragmentDefault {
            return FragmentDefault()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = activity!!.applicationContext
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.default_fragment, container, false)
        loadAmpEngine()
        loadBroadcastReceiver()
        return mView;
    }

    private fun loadBroadcastReceiver() {
        receiver = BatteryBroadcast()

        receiver.setChargingStatus(this@FragmentDefault)
        receiver.setVoltReceived(this@FragmentDefault)
        mContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun loadAmpEngine() {
        job = GlobalScope.launch(Dispatchers.Main) {
            getCurrent()
        }
    }

    private suspend fun getCurrent() {
        val listener: AmpReceived = this
        val mBatteryManager = mContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        while (true) {
            val current =
                mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                    .div(1000).toString()
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

    override fun onChargingStatusChange(status: String) {
        chargingStatus.text = status
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        mContext.unregisterReceiver(receiver)
    }
}