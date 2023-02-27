/*
 * Copyright (C) 2020 YAAP
 * Copyright (C) 2023 cyberknight777
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.displayfeatures.display;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.android.displayfeatures.R;
import com.android.displayfeatures.utils.FileUtils;

public class DisplayFeaturesFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener {

    private SwitchPreference mDcDimmingPreference;
    private SwitchPreference mHBMPreference;
    private DisplayFeaturesConfig mConfig;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.displayfeatures_settings, rootKey);
        mConfig = DisplayFeaturesConfig.getInstance(getContext());
        mDcDimmingPreference = (SwitchPreference) findPreference(mConfig.DISPLAYFEATURES_DC_DIMMING_KEY);
        if (FileUtils.fileExists(mConfig.getDcDimPath())) {
            mDcDimmingPreference.setEnabled(true);
            mDcDimmingPreference.setOnPreferenceChangeListener(this);
        } else {
            mDcDimmingPreference.setSummary(R.string.dc_dimming_summary_not_supported);
            mDcDimmingPreference.setEnabled(false);
        }
        mHBMPreference = (SwitchPreference) findPreference(mConfig.DISPLAYFEATURES_HBM_KEY);
        if (FileUtils.fileExists(mConfig.getHbmPath())) {
            mHBMPreference.setEnabled(true);
            mHBMPreference.setOnPreferenceChangeListener(this);
        } else {
            mHBMPreference.setSummary(R.string.hbm_summary_not_supported);
            mHBMPreference.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (mConfig.DISPLAYFEATURES_DC_DIMMING_KEY.equals(preference.getKey())) {
            FileUtils.writeLine(mConfig.getDcDimPath(), (Boolean) newValue ? "1":"0");
        }
        if (mConfig.DISPLAYFEATURES_HBM_KEY.equals(preference.getKey())) {
            FileUtils.writeLine(mConfig.getHbmPath(), (Boolean) newValue ? "1" : "0");
        }
        return true;
    }

}
