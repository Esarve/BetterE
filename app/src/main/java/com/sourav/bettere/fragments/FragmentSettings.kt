/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.fragments

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sourav.bettere.R
import com.sourav.bettere.broadcasts.StartOnBootBroadcast
import com.sourav.bettere.utils.Constants
import com.sourav.bettere.utils.Utilities
import com.sourav.bettere.viewModel.PreferenceViewModel


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val prefViewmodel = ViewModelProvider(this).get(PreferenceViewModel::class.java)
        prefViewmodel.getBootStatus.observe(viewLifecycleOwner, Observer { onBoot ->
            if (onBoot) {
                Utilities.getInstance(requireContext()).loadBroadcastReceiver(
                    StartOnBootBroadcast(),
                    IntentFilter(Intent.ACTION_BOOT_COMPLETED)
                )
                Toast.makeText(requireContext(), "Service Will Start on Boot", Toast.LENGTH_LONG)
                    .show()
            }
        })

        return super.onCreateView(inflater, container, savedInstanceState)
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