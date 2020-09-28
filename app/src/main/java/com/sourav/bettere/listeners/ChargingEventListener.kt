package com.sourav.bettere.listeners

interface ChargingEventListener {
    fun onVoltageChange(value: String)
    fun onChargingStatusChange(status: String)
    fun onBatteryPercentageChange(value: String)
}