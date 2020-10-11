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
        Log.d(Constants.BROADCAST, "Broadcast RUN")

        val isPresent = intent?.getBooleanExtra("present", false)

        val bundle = intent?.extras

        val string = bundle.toString()
        Log.d(Constants.BROADCAST, "Battery Info: $string")

        if (isPresent!!) {
            val volt: Double? = bundle?.getInt("voltage")?.div(1000.0);
            val level: Int? = bundle?.getInt("level")
            val temp: Double? = bundle?.getInt("temperature")?.div(10.0)
            val plugged = bundle?.getInt(BatteryManager.EXTRA_PLUGGED, 0)
            try {
                listener!!.onVoltageChange(volt.toString())
                listener!!.onBatteryPercentageChange(level.toString().plus("%"))
                listener!!.onTempChange(temp.toString().plus("\u2103"))
                Log.d(Constants.BROADCAST, "voltage: $volt")
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