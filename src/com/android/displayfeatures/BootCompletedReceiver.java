/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2023 cyberknight777
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

package com.android.displayfeatures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.displayfeatures.utils.FileUtils;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final Context mContext;
    private static final boolean DEBUG = false;
    private static final String TAG = "DisplayFeatures";
    private static final String DISPLAYFEATURES_DC_DIMMING_KEY = "dc_dimming";
    private static final String DISPLAYFEATURES_DC_DIMMING_NODE = mContext.getResources().getString(com.android.displayfeatures.R.string.config_DisplayFeaturesDcDimPath);
    private static final String DISPLAYFEATURES_HBM_KEY = "hbm";
    private static final String DISPLAYFEATURES_HBM_NODE = mContext.getResources().getString(com.android.displayfeatures.R.string.config_DisplayFeaturesHbmPath);

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (DEBUG)
            Log.d(TAG, "Received boot completed intent");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean dcDimmingEnabled = sharedPrefs.getBoolean(DISPLAYFEATURES_DC_DIMMING_KEY, false);
        boolean hbmEnabled = sharedPrefs.getBoolean(DISPLAYFEATURES_HBM_KEY, false);
        FileUtils.writeLine(DISPLAYFEATURES_DC_DIMMING_NODE, dcDimmingEnabled ? "1" : "0");
        FileUtils.writeLine(DISPLAYFEATURES_HBM_NODE, hbmEnabled ? "1" : "0");

    }
}
