/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
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
import com.sourav.bettere.viewModel.PreferenceViewModel
import kotlinx.android.synthetic.main.graph_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentGraph : Fragment(), OnChartValueSelectedListener {
    private val TAG = Constants.GRAPH
    private lateinit var utilities: Utilities
    private lateinit var mContext: Context
    private lateinit var viewModelHistory: ChargingDataViewModel
    private lateinit var linechart: LineChart
    private lateinit var switch: SwitchMaterial
    private lateinit var mView: View
    private lateinit var backButton: TextView
    private lateinit var intent: Intent
    private lateinit var bundle: Bundle
    private lateinit var dataset: LineDataSet
    private var cdTime: Long = 300000


    companion object {
        fun newInstance(): FragmentGraph {
            return FragmentGraph()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = requireActivity().applicationContext
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
        bundle = Bundle()

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

        val prefViewmodel = ViewModelProvider(this).get(PreferenceViewModel::class.java)

        prefViewmodel.getGraphPoint.observe(viewLifecycleOwner, Observer { value ->
            if (this::dataset.isInitialized) {
                dataset.setDrawCircles(value)
                dataset.setDrawCircleHole(value)
                linechart.invalidate()
            }
        })

        prefViewmodel.getInteractableGraph.observe(viewLifecycleOwner, Observer { value ->
            if (this::dataset.isInitialized) {
                linechart.setTouchEnabled(value)
                linechart.invalidate()
            }
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


    private fun initToggle() {
        switch = mView.findViewById(R.id.loggerTrigger)
        if (Utilities.getInstance(requireContext())
                .isMyServiceRunning(ChargeLoggerService::class.java)
        ) {
            switch.isChecked = true
        }

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Utilities.getInstance(mContext)
                    .writeToPref(
                        Constants.PREF_TYPE_BOOL,
                        Constants.PREF_LOGGER_ACTIVE,
                        valueBool = true
                    )
                Utilities.getInstance(mContext).startService(ChargeLoggerService::class.java)
            } else {
                Utilities.getInstance(mContext)
                    .writeToPref(
                        Constants.PREF_TYPE_BOOL,
                        Constants.PREF_LOGGER_ACTIVE,
                        valueBool = false
                    )
                Utilities.getInstance(mContext).stopService(ChargeLoggerService::class.java)
            }
        }
    }

    private suspend fun plotGraph(data: List<ChargingLog>) {
        withContext(Dispatchers.Main) {
            dataset = LineDataSet(
                Utilities.getInstance(mContext).generateAverage(data), "Ampere - Percentage Chart"
            )

            dataset.valueTextColor = resources.getColor(R.color.textPrimaryLight, mContext.theme)
            dataset.setDrawCircles(false)
            dataset.setDrawCircleHole(false)
            dataset.setDrawFilled(true)
            dataset.fillColor = resources.getColor(R.color.colorAccent, mContext.theme)
            dataset.setDrawHighlightIndicators(true)
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

            linechart.description.isEnabled = false
            linechart.setBorderColor(resources.getColor(R.color.textPrimaryLight, mContext.theme))

            linechart.setOnChartValueSelectedListener(this@FragmentGraph)
            linechart.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        initToggle()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        xval.text = "Percentage: ".plus(h?.x.toString())
        yval.text = "Current: ".plus(h?.y.toString()).plus(" mAh")
    }

    override fun onNothingSelected() {
        xval.text = ""
        yval.text = ""
    }

}