/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
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
        val dummyPrefVersion = findPreference<Preference>("pref_version")
        cdTimePref!!.dialogMessage = Constants.PREF_DIALOG_TITLE_CDTIME
        cdTimePref.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        sampleRatePref!!.dialogMessage = Constants.PREF_DIALOG_TITLE_SAMPLINGRATE
        sampleRatePref.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        dummyPrefVersion!!.summary = getVersion()

    }

    private fun getVersion(): String {
        try {
            val pInfo =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

}