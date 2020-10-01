package com.sourav.bettere.utils

class Constants {

    companion object {
        const val DB_VER = 1

        const val CHARGING_AC = "Charging (AC)"
        const val CHARGING_USB = "Charging (USB)"
        const val DISCHARGING = "Discharging"

        //Tags For Logs
        const val BROADCAST = "BroadcastReceiver"
        const val SERVICE = "Service"
        const val DEFAULT = "DefaultFragment"
        const val GRAPH = "GraphFragment"
        const val SETTINGS = "SettingsFragment"
        const val DATABASE = "dbresult"

        //FOR notifications
        const val TITLE = "Monitoring Service is Running"
        const val BODY = "BetterE will log charging events as soon as plugged in"

        const val SERVICE_CYCLE = "cycle"
    }
}