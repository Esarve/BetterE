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

class FragmentGraph : Fragment() {
    private lateinit var mContext: Context

    companion object {
        fun newInstance(): FragmentGraph {
            return FragmentGraph()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = activity!!.applicationContext
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
//                WorkManager.getInstance(mContext).enqueue(request)
                activity!!.startService(intent)
                Log.d(Constants.GRAPH, "onCreateView: Switch Listener Fired")
            } else {
                activity!!.stopService(intent)
            }
        }

        return mView
    }
}