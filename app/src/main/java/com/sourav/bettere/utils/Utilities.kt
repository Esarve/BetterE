package com.sourav.bettere.utils

import android.content.Context
import android.os.BatteryManager

class Utilities private constructor(context: Context){

    companion object {
        private var mContext: Context? = null
        private var instance: Utilities? = null
        fun getInstance(context: Context): Utilities {
            if (instance == null) {
                instance = Utilities(context)
            }
            return instance as Utilities
        }
        private val mBatteryManager = mContext!!.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    fun getCurrent():String{
        return mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                .div(1000).toString()
    }
}