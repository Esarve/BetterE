package com.sourav.bettere.viewModel

import android.app.Application
import android.content.Context
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import com.sourav.bettere.listeners.ChargingEventListener
import com.sourav.bettere.utils.Utilities
import kotlinx.android.synthetic.main.default_fragment.*

class BasicDataViewModel(application: Application): AndroidViewModel(application), ChargingEventListener {
    var voltage: String = "0 V"
    var amp: String = "0 mA"
    var chargingStatus: String = "null"
    var percentage: String = "null"
    private val mBatteryManager: BatteryManager = application.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    override fun onVoltageChange(value: String) {
        voltage = value.plus(" V")
    }

    override fun onChargingStatusChange(status: String) {
        chargingStatus = status
    }

    override fun onBatteryPercentageChange(value: String) {
        percentage = value
    }


}