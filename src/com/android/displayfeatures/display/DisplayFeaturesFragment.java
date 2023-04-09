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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.android.displayfeatures.R;
import com.android.displayfeatures.utils.FileUtils;

public class DisplayFeaturesFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener {

    private SwitchPreference mDcDimmingPreference;
    private SwitchPreference mHBMPreference;
    private DisplayFeaturesConfig mConfig;
    private boolean mInternalHbmStart = false;
    private boolean mInternalDcDimStart = false;

    private final BroadcastReceiver mServiceStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mConfig.ACTION_HBM_SERVICE_CHANGED)) {
                if (mInternalHbmStart) {
                        mInternalHbmStart = false;
                        return;
                }

                if (mHBMPreference == null) return;

                final boolean hbmStarted = intent.getBooleanExtra(
                            mConfig.EXTRA_HBM_STATE, false);

                mHBMPreference.setChecked(hbmStarted);

            } else if (action.equals(mConfig.ACTION_DC_DIM_SERVICE_CHANGED)) {
                if (mInternalDcDimStart) {
                        mInternalDcDimStart = false;
                        return;
                }

                if (mDcDimmingPreference == null) return;

                final boolean dcDimStarted = intent.getBooleanExtra(
                            mConfig.EXTRA_DC_DIM_STATE, false);

                mDcDimmingPreference.setChecked(dcDimStarted);

           }
        }
    };

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
        mDcDimmingPreference.setChecked(mConfig.isCurrentlyEnabled(mConfig.getDcDimPath()));
        mHBMPreference.setChecked(mConfig.isCurrentlyEnabled(mConfig.getHbmPath()));

        // Registering observers
        IntentFilter filter = new IntentFilter();
        filter.addAction(mConfig.ACTION_HBM_SERVICE_CHANGED);
        filter.addAction(mConfig.ACTION_DC_DIM_SERVICE_CHANGED);
        getContext().registerReceiver(mServiceStateReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mDcDimmingPreference.setChecked(mConfig.isCurrentlyEnabled(mConfig.getDcDimPath()));
        mHBMPreference.setChecked(mConfig.isCurrentlyEnabled(mConfig.getHbmPath()));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (mConfig.DISPLAYFEATURES_DC_DIMMING_KEY.equals(preference.getKey())) {
            mInternalHbmStart = true;
            Context mContext = getContext();

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            FileUtils.writeLine(mConfig.getDcDimPath(), (Boolean) newValue ? "1":"0");

            boolean enabled = mConfig.isCurrentlyEnabled(mConfig.getDcDimPath());

            sharedPrefs.edit().putBoolean(mConfig.DISPLAYFEATURES_DC_DIMMING_KEY, enabled).commit();

            Intent intent = new Intent(mConfig.ACTION_DC_DIM_SERVICE_CHANGED);

            intent.putExtra(mConfig.EXTRA_DC_DIM_STATE, enabled);
            intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);;
        }
        if (mConfig.DISPLAYFEATURES_HBM_KEY.equals(preference.getKey())) {
            mInternalHbmStart = true;
            Context mContext = getContext();

            FileUtils.writeLine(mConfig.getHbmPath(), (Boolean) newValue ? "1" : "0");

            boolean enabled = mConfig.isCurrentlyEnabled(mConfig.getHbmPath());

            Intent hbmIntent = new Intent(mContext,
                    com.android.displayfeatures.display.DisplayFeaturesHbmService.class);

            if (enabled) mContext.startService(hbmIntent);
            else mContext.stopService(hbmIntent);

            Intent intent = new Intent(mConfig.ACTION_HBM_SERVICE_CHANGED);

            intent.putExtra(mConfig.EXTRA_HBM_STATE, enabled);
            intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);;
        }
        return true;
    }

}
