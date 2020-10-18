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