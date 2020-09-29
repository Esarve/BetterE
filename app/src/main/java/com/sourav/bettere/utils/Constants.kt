package com.sourav.bettere.utils

class Constants {

    companion object {
        const val DB_VER = 1

        const val CHARGING_AC = "Charging (AC)"
        const val CHARGING_USB = "Charging (USB)"
        const val DISCHARGING = "Discharging"

        //Tags For Logs
        const val BROADCAST = "Broadcast Receiver"
        const val SERVICE = "Service"
        const val DEFAULT = "Default Fragment"
        const val GRAPH = "Graph Fragment"
        const val SETTINGS = "Settings Fragment"

        const val TITLE = "Monitoring Service is Running"
        const val BODY = "BetterE will log charging events as soon as plugged in"
    }
}