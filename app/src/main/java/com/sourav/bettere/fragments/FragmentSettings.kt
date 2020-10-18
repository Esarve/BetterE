/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.fragments

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.sourav.bettere.R
import com.sourav.bettere.utils.Constants

class FragmentSettings : PreferenceFragmentCompat() {

    companion object {
        fun newInstance(): FragmentSettings {
            return FragmentSettings()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_ui, rootKey)
        val cdTimePref = findPreference<EditTextPreference>("prefkey_cdtime")
        val sampleRatePref = findPreference<EditTextPreference>("prefkey_samplerate")
        cdTimePref!!.dialogMessage = Constants.PREF_DIALOG_TITLE_CDTIME
        cdTimePref.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        sampleRatePref!!.dialogMessage = Constants.PREF_DIALOG_TITLE_SAMPLINGRATE
        sampleRatePref.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

}