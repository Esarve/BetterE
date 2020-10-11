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
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.sourav.bettere.R
import com.sourav.bettere.service.ChargeLoggerService
import com.sourav.bettere.utils.Constants
import com.sourav.bettere.utils.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentGraph : Fragment() {
    private lateinit var utilities: Utilities
    private lateinit var mContext: Context

    companion object {
        fun newInstance(): FragmentGraph {
            return FragmentGraph()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = activity!!.applicationContext
        utilities = Utilities.getInstance(mContext)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val intent: Intent = Intent(mContext, ChargeLoggerService::class.java)
        val mView = inflater.inflate(R.layout.graph_fragment, container, false)


        /* val constrains = Constraints.Builder()
             .setRequiresCharging(true)
             .build()

         val request: WorkRequest =
             OneTimeWorkRequestBuilder<ChargingLogger>().setConstraints(constrains).build()*/

        val switch: SwitchMaterial? = mView?.findViewById(R.id.loggerTrigger)

        /*WorkManager.getInstance(mContext).getWorkInfoByIdLiveData(request.id).observe(
            this,
            Observer {
                if (it.state.isFinished) {
                    Log.d(Constants.GRAPH, "onCreateView: WORKING FINISHED")
                    val notificationManager:NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(1)
                    WorkManager.getInstance(mContext).enqueue(request)
                }
            })*/

        if (switch!!.isChecked){
//            WorkManager.getInstance(mContext).enqueue(request)
            Log.d(Constants.GRAPH, "onCreateView: Worker Activated")
        }

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                GlobalScope.launch(Dispatchers.IO) {
                    activity!!.startService(intent)
                    utilities.writeToPref(
                        type = Constants.PREF_TYPE_BOOL,
                        key = Constants.PREF_KEY_SERVICE,
                        valueBool = true
                    )
                    Log.d(Constants.GRAPH, "onCreateView: Switch Listener Fired")
                }
//                WorkManager.getInstance(mContext).enqueue(request)
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    activity!!.stopService(intent)
                    utilities.writeToPref(
                        type = Constants.PREF_TYPE_BOOL,
                        key = Constants.PREF_KEY_SERVICE,
                        valueBool = false
                    )
                    Log.d(Constants.GRAPH, "onCreateView: Switch Listener Fired")
                }
            }
        }

        return mView
    }
}