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