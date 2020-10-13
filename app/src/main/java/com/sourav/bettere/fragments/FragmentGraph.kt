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

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.switchmaterial.SwitchMaterial
import com.sourav.bettere.R
import com.sourav.bettere.adapters.HistoryAdapter
import com.sourav.bettere.db.ChargingDB
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.repository.LogRepository
import com.sourav.bettere.service.ChargeLoggerService
import com.sourav.bettere.utils.Constants
import com.sourav.bettere.utils.Utilities
import com.sourav.bettere.viewModel.ChargingDataViewModel
import kotlinx.android.synthetic.main.graph_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentGraph : Fragment() {
    private lateinit var utilities: Utilities
    private lateinit var mContext: Context
    private lateinit var viewModelHistory: ChargingDataViewModel
    private lateinit var linechart: LineChart
    private lateinit var switch: SwitchMaterial
    private lateinit var mView: View
    private lateinit var backButton: TextView
    private lateinit var intent: Intent

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
        mView = inflater.inflate(R.layout.graph_fragment, container, false)
        backButton = mView.findViewById(R.id.backToRecyclerView) as TextView
        intent = Intent(mContext, ChargeLoggerService::class.java)

        val rview: RecyclerView = mView.findViewById(R.id.rvHistory)
        val linearLayoutManager = LinearLayoutManager(this.context)
        val historyList = mutableListOf<ChargingHistory>()
        val adapter = HistoryAdapter(R.layout.historyadapter, historyList)

        initToggle()
        linechart = mView.findViewById(R.id.lineChart) as LineChart

        rview.layoutManager = linearLayoutManager
        rview.adapter = adapter

        viewModelHistory = ViewModelProvider(this).get(ChargingDataViewModel::class.java)
        viewModelHistory.readHistoryData.observe(viewLifecycleOwner, Observer { history ->
            historyList.addAll(history)
            Log.d(Constants.GRAPH, "onCreateView: $history")
            adapter.notifyDataSetChanged()
        })

        adapter.setOnItemClickListener { adapter, view, position ->
            rvParent.visibility = View.GONE
            graphLayout.visibility = View.VISIBLE

            GlobalScope.launch(Dispatchers.IO) {
                val data: ChargingHistory = adapter.getItem(position) as ChargingHistory
                plotGraph(
                    LogRepository(
                        ChargingDB.getInstance(mContext).logDao()
                    ).readData(data.cycle)
                )
            }
        }

        backButton.setOnClickListener {
            if (!rvParent.isVisible && graphLayout.isVisible) {
                graphLayout.visibility = View.GONE
                rvParent.visibility = View.VISIBLE
            }
        }
        return mView
    }

    private fun startService() {
        activity!!.startService(intent)
        Log.d(Constants.GRAPH, "startService: Service Started")
    }

    private fun stopService() {
        activity!!.stopService(intent)
        Log.d(Constants.GRAPH, "stopService: Service Stopped")
    }

    private fun initToggle() {
        switch = mView.findViewById(R.id.loggerTrigger)
        if (isServiceRunning(ChargeLoggerService::class.java.simpleName)) {
            switch.isEnabled = true
        }

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) startService()
            else stopService()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun plotGraph(data: List<ChargingLog>) {
        withContext(Dispatchers.Main) {
            val dataset = LineDataSet(
                Utilities.getInstance(mContext).generateAverage(data), "Ampere - Percentage Chart"
            )

            dataset.valueTextColor = resources.getColor(R.color.textPrimaryLight, mContext.theme)
            dataset.setDrawFilled(true)
            dataset.fillColor = resources.getColor(R.color.colorAccent, mContext.theme)
            val lineData = LineData(dataset)
            linechart.data = lineData

            linechart.axisLeft.gridColor = resources.getColor(R.color.layoutBG, mContext.theme)
            linechart.axisRight.gridColor = resources.getColor(R.color.layoutBG, mContext.theme)

            linechart.axisRight.textColor =
                resources.getColor(R.color.textPrimaryLight, mContext.theme)
            linechart.axisLeft.textColor =
                resources.getColor(R.color.textPrimaryLight, mContext.theme)

            linechart.xAxis.gridColor = resources.getColor(R.color.layoutBG, mContext.theme)
            linechart.xAxis.textColor = resources.getColor(R.color.textPrimaryLight, mContext.theme)
            linechart.legend.textColor =
                resources.getColor(R.color.textPrimaryLight, mContext.theme)

            linechart.setTouchEnabled(false)
            linechart.description.isEnabled = false
            linechart.setBorderColor(resources.getColor(R.color.textPrimaryLight, mContext.theme))

            linechart.invalidate()
        }
    }

    @Suppress("DEPRECATION")
    fun isServiceRunning(serviceClassName: String?): Boolean {
        val activityManager =
            mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services: List<ActivityManager.RunningServiceInfo> =
            activityManager.getRunningServices(Int.MAX_VALUE)
        for (runningServiceInfo in services) {
            if (runningServiceInfo.service.className == serviceClassName) {
                return true
            }
        }
        return false
    }

}