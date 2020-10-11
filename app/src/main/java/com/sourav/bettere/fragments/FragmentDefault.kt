/*
 * Copyright 2020 Sourav Das
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.sourav.bettere.R
import com.sourav.bettere.broadcasts.BatteryBroadcast
import com.sourav.bettere.listeners.ChargingEventListener
import com.sourav.bettere.utils.Constants
import kotlinx.android.synthetic.main.default_fragment.*
import kotlinx.coroutines.*

class FragmentDefault : Fragment(), ChargingEventListener {
    private lateinit var mContext: Context
    private lateinit var receiver: BatteryBroadcast
    private lateinit var jobC: Job
    private lateinit var jobB: Job
    private var delayTime = 3000L
    private var isOnForeground: Boolean = true
    private lateinit var mBatteryManager: BatteryManager

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
        return mView;
    }

    private fun loadBroadcastReceiver() {
        Log.d(Constants.DEFAULT, "Current Thread ${Thread.currentThread().name}")
        receiver = BatteryBroadcast()
        receiver.setChargingEventListener(this@FragmentDefault)
        mContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    }

    private fun loadEngine() {
        jobC = GlobalScope.launch(Dispatchers.Default) {
            getCurrent()
            loadBroadcastReceiver()
        }

        jobB = GlobalScope.launch(Dispatchers.Default) { loadBroadcastReceiver() }
    }

    private suspend fun getCurrent() {
        while (jobC.isActive) {
            val current =
                mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                    .div(1000)
                    .toString()

            Log.d(Constants.DEFAULT, "Current: $current")
            Log.d(Constants.DEFAULT, "Current Thread ${Thread.currentThread().name}")

            viewOnMain(current)
            delay(delayTime)
        }
    }

    private suspend fun viewOnMain(text: String) {
        withContext(Dispatchers.Main) {
            val value = text.substring(1)
            ampValue?.text = value.plus(" mAh")
        }
    }

    override fun onVoltageChange(value: String) {
        voltValue?.text = value.plus(" V")
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
        loadBroadcastReceiver()
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