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

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.android.displayfeatures.R;
import com.android.displayfeatures.utils.FileUtils;

public class DisplayFeaturesFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener {

    private SwitchPreference mDcDimmingPreference;
    private static final String DISPLAYFEATURES_DC_DIMMING_KEY = "dc_dimming";
    private String DISPLAYFEATURES_DC_DIMMING_NODE;

    private SwitchPreference mHBMPreference;
    private static final String DISPLAYFEATURES_HBM_KEY = "hbm";
    private String DISPLAYFEATURES_HBM_NODE;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Resources res = context.getResources();
        DISPLAYFEATURES_DC_DIMMING_NODE = res.getString(R.string.config_DisplayFeaturesDcDimPath);
        DISPLAYFEATURES_HBM_NODE = res.getString(R.string.config_DisplayFeaturesHbmPath);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.displayfeatures_settings, rootKey);
        mDcDimmingPreference = (SwitchPreference) findPreference(DISPLAYFEATURES_DC_DIMMING_KEY);
        if (FileUtils.fileExists(DISPLAYFEATURES_DC_DIMMING_NODE)) {
            mDcDimmingPreference.setEnabled(true);
            mDcDimmingPreference.setOnPreferenceChangeListener(this);
        } else {
            mDcDimmingPreference.setSummary(R.string.dc_dimming_summary_not_supported);
            mDcDimmingPreference.setEnabled(false);
        }
        mHBMPreference = (SwitchPreference) findPreference(DISPLAYFEATURES_HBM_KEY);
        if (FileUtils.fileExists(DISPLAYFEATURES_HBM_NODE)) {
            mHBMPreference.setEnabled(true);
            mHBMPreference.setOnPreferenceChangeListener(this);
        } else {
            mHBMPreference.setSummary(R.string.hbm_summary_not_supported);
            mHBMPreference.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (DISPLAYFEATURES_DC_DIMMING_KEY.equals(preference.getKey())) {
            FileUtils.writeLine(DISPLAYFEATURES_DC_DIMMING_NODE, (Boolean) newValue ? "1":"0");
        }
        if (DISPLAYFEATURES_HBM_KEY.equals(preference.getKey())) {
            FileUtils.writeLine(DISPLAYFEATURES_HBM_NODE, (Boolean) newValue ? "1" : "0");
        }
        return true;
    }

}
