/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2023-2025 cyberknight777
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

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.displayfeatures.display.DisplayFeaturesConfig;
import com.android.displayfeatures.utils.FileUtils;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.preference.PreferenceManager;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final boolean DEBUG = false;
    private static final String TAG = "DisplayFeatures";
    private static final String PKG_NAME = "com.android.displayfeatures";
    private static final String PKG_NAME_SUFFIX = PKG_NAME + ".display";
    private static final String DC_DIM_TILE_CLASS_NAME = PKG_NAME_SUFFIX + ".DisplayFeaturesDcDimTileService";
    private static final String HBM_TILE_CLASS_NAME = PKG_NAME_SUFFIX + ".DisplayFeaturesHbmTileService";
    private static final String FPS_TILE_CLASS_NAME = PKG_NAME_SUFFIX + ".DisplayFeaturesFpsTileService";
    private static final String CABC_TILE_CLASS_NAME = PKG_NAME_SUFFIX + ".DisplayFeaturesCabcTileService";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (DEBUG)
            Log.d(TAG, "Received boot completed intent");

        DisplayFeaturesConfig mConfig = DisplayFeaturesConfig.getInstance(context);

        PackageManager pm = context.getPackageManager();
        ComponentName cn;
        int state;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        cn = new ComponentName(PKG_NAME, DC_DIM_TILE_CLASS_NAME);
        if (FileUtils.fileExists(mConfig.getDcDimPath())) {
            boolean dcDimmingEnabled = sharedPrefs.getBoolean(mConfig.DISPLAYFEATURES_DC_DIMMING_KEY, false);
            FileUtils.writeLine(mConfig.getDcDimPath(), dcDimmingEnabled ? "1" : "0");
            state = COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            state = COMPONENT_ENABLED_STATE_DISABLED;
        }
        pm.setComponentEnabledSetting(cn, state, 0);

        cn = new ComponentName(PKG_NAME, HBM_TILE_CLASS_NAME);
        if (FileUtils.fileExists(mConfig.getHbmPath())) {
            boolean hbmEnabled = sharedPrefs.getBoolean(mConfig.DISPLAYFEATURES_HBM_KEY, false);
            FileUtils.writeLine(mConfig.getHbmPath(), hbmEnabled ? "1" : "0");
            state = COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            state = COMPONENT_ENABLED_STATE_DISABLED;
        }
        pm.setComponentEnabledSetting(cn, state, 0);

        cn = new ComponentName(PKG_NAME, FPS_TILE_CLASS_NAME);
        if (FileUtils.fileExists(mConfig.getFpsPath())) {
            // reset prefs that reflect a state that does not retain a reboot
            sharedPrefs.edit().remove(mConfig.PREF_KEY_FPS_STATE).commit();
            state = COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            state = COMPONENT_ENABLED_STATE_DISABLED;
        }
        pm.setComponentEnabledSetting(cn, state, 0);

        cn = new ComponentName(PKG_NAME, CABC_TILE_CLASS_NAME);
        state = FileUtils.fileExists(mConfig.getCabcPath())
                ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(cn, state, 0);

    }
}
