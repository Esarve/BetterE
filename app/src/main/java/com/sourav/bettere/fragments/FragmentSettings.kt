package com.sourav.bettere.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourav.bettere.R

class FragmentSettings : Fragment() {
    private lateinit var mContext: Context
    companion object {
        fun newInstance(): FragmentSettings {
            return FragmentSettings()
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
        val mView = inflater.inflate(R.layout.settings_fragment, container, false)
        return mView;
    }
}