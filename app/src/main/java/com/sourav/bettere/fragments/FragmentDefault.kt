/*
 * Copyright 2020 Sourav Das
 */

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sourav.bettere.R
import com.sourav.bettere.broadcasts.BatteryBroadcast
import com.sourav.bettere.listeners.ChargingEventListener
import com.sourav.bettere.utils.Constants
import com.sourav.bettere.utils.Utilities
import com.sourav.bettere.viewModel.PreferenceViewModel
import kotlinx.android.synthetic.main.default_fragment.*
import kotlinx.coroutines.*

class FragmentDefault : Fragment(), ChargingEventListener {
    private lateinit var mContext: Context
    private lateinit var jobC: Job
    private lateinit var jobB: Job
    private var delayTime = 3000L
    private var isOnForeground: Boolean = true
    private lateinit var mBatteryManager: BatteryManager
    private lateinit var receiver: BatteryBroadcast

    companion object {
        fun newInstance(): FragmentDefault {
            return FragmentDefault()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = requireActivity().applicationContext
        mBatteryManager = mContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.default_fragment, container, false)
        loadEngine()
        val prefViewmodel = ViewModelProvider(this).get(PreferenceViewModel::class.java)
        prefViewmodel.getSamplingRate.observe(viewLifecycleOwner, Observer { rate ->
            try {
                delayTime = rate.toLong()
            } catch (e: java.lang.Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        })
        return mView;
    }

    private fun loadEngine() {
        jobC = GlobalScope.launch(Dispatchers.Default) {
            getCurrent()
        }
        loadBatteryBroadcast()
    }

    private fun loadBatteryBroadcast() {
        receiver = BatteryBroadcast()
        receiver.setChargingEventListener(this@FragmentDefault)
        Utilities.getInstance(mContext)
            .loadBroadcastReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private suspend fun getCurrent() {
        while (jobC.isActive) {
            val current =
                mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                    .div(1000)
                    .toString()

            Log.d(Constants.DEFAULT, "Current: $current")
            Log.d(Constants.DEFAULT, "Current Thread ${Thread.currentThread().name}")
            delay(delayTime)
            viewOnMain(current)

        }
    }

    private suspend fun viewOnMain(text: String) {
        withContext(Dispatchers.Main) {
            ampValue?.text = text.replace("-", "", true).plus(" mAh")
        }
    }

    override fun onVoltageChange(value: String) {
        voltValue?.text = value.plus(" V")
    }

    override fun onTempChange(value: String) {
        tempValue?.text = value
    }

    override fun onChargingStatusChange(status: String) {
        if (isOnForeground) {
            if (status == Constants.CHARGING_AC || status == Constants.CHARGING_USB) {
                animationDischarging.visibility = View.GONE
                animationCharging.visibility = View.VISIBLE
            } else {
                animationDischarging.visibility = View.VISIBLE
                animationCharging.visibility = View.GONE
            }
            chargingStatus.text = status
        }
    }

    override fun onBatteryPercentageChange(value: String) {
        batteryPercentage.text = value
    }

    override fun onPause() {
        super.onPause()
        Log.d(Constants.DEFAULT, "onPause: RUN")
        isOnForeground = false
        jobC.cancel()
        mContext.unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        Log.d(Constants.DEFAULT, "onResume: RUN")
        isOnForeground = true
        loadEngine()
        loadBatteryBroadcast()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (jobC.isActive) jobC.cancel()
        try {
            mContext.unregisterReceiver(receiver)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}