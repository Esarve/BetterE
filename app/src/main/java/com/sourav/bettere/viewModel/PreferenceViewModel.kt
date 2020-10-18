/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import booleanLiveData
import com.sourav.bettere.utils.Constants
import stringLiveData

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {
    val getSamplingRate: LiveData<String>
    val getBootStatus: LiveData<Boolean>
    val getCDTime: LiveData<String>

    init {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

        getSamplingRate = sharedPreferences.stringLiveData(Constants.PREF_SAMPLING_KEY, "")
        getBootStatus = sharedPreferences.booleanLiveData(Constants.PREF_BOOT_KEY, false)
        getCDTime = sharedPreferences.stringLiveData(Constants.PREF_CDTIME_KEY, "300000")
    }
}