/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.listeners

interface OnChargingListener {
    fun onCharging()
    fun onDischarging()
    fun onReceive(voltage: Double, percentage: Int, temp: Double)
}