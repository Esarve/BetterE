/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.utils

class Constants {

    companion object {
        const val DB_VER = 1

        const val CHARGING_AC = "Charging (AC)"
        const val CHARGING_USB = "Charging (USB)"
        const val DISCHARGING = "Discharging"

        //Tags For Logs
        const val BAT_BROADCAST = "BBR"
        const val CHAR_BROADCAST = "CBR"
        const val SOB_BROADCAST = "SOBBR"
        const val SERVICE = "Service"
        const val DEFAULT = "DefaultFragment"
        const val GRAPH = "GraphFragment"
        const val SETTINGS = "SettingsFragment"
        const val DATABASE = "dbresult"
        const val UTILS = "utils"

        //FOR notifications
        const val TITLE = "Monitoring Service is Running"
        const val BODY = "BetterE will log charging events as soon as plugged in"

        //FOR INTENT BUNDLE
        const val EXTRA_CD = "extracd"

        //FOR PREF TYPE
        const val PREF_NAME = "betterE_pref"
        const val PREF_TYPE_BOOL = "bool"
        const val PREF_TYPE_INT = "int"
        const val PREF_TYPE_STRING = "string"

        //FOR PREF KEY
        const val PREF_KEY_SERVICE = "prefservice"
        const val PREF_SAMPLING_KEY = "prefkey_samplerate"
        const val PREF_BOOT_KEY = "prefkey_startonboot"
        const val PREF_CDTIME_KEY = "prefkey_cdtime"
        const val PREF_LOGGER_ACTIVE = "prefkey_logger"
        const val PREF_INTERACTABLE_GRAPH = "prefkey_interactable_graph"
        const val PREF_GRAPH_POINT = "prefkey_graphdot"

        //For PREF DIALOG TITLES
        const val PREF_DIALOG_TITLE_CDTIME = "(in Minutes)"

        const val PREF_DIALOG_TITLE_SAMPLINGRATE = "(in Milliseconds)"

        //TimeData
        const val TIME_DATE_FORMAT = "MMMM dd,  hh:mm a"

    }
}