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
import com.android.displayfeatures.display.DisplayFeaturesConfig;
import com.android.displayfeatures.utils.FileUtils;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final boolean DEBUG = false;
    private static final String TAG = "DisplayFeatures";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (DEBUG)
            Log.d(TAG, "Received boot completed intent");

        DisplayFeaturesConfig mConfig = DisplayFeaturesConfig.getInstance(context);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean dcDimmingEnabled = sharedPrefs.getBoolean(mConfig.DISPLAYFEATURES_DC_DIMMING_KEY, false);
        boolean hbmEnabled = sharedPrefs.getBoolean(mConfig.DISPLAYFEATURES_HBM_KEY, false);
        FileUtils.writeLine(mConfig.getDcDimPath(), dcDimmingEnabled ? "1" : "0");
        FileUtils.writeLine(mConfig.getHbmPath(), hbmEnabled ? "1" : "0");

        // reset prefs that reflect a state that does not retain a reboot
        sharedPrefs.edit().remove(mConfig.PREF_KEY_FPS_STATE).commit();

    }
}
