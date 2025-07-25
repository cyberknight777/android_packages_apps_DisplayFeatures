/*
 * Copyright (C) 2023-2025 cyberknight777
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
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import java.util.Arrays;

import com.android.displayfeatures.R;
import com.android.displayfeatures.utils.FileUtils;

public class DisplayFeaturesCabcTileService extends TileService {

    private DisplayFeaturesConfig mConfig;
    private Context context;

    private String[] CabcModes;
    private String[] CabcValues;
    private int currentCabcMode;
    private Tile tile;

    private Intent mCabcIntent;

    private boolean mInternalStart;

    private final BroadcastReceiver mServiceStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mInternalStart) {
                mInternalStart = false;
                return;
            }
            updateCurrentCabcmode();
            updateCabcTile();
        }
    };

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CabcModes = context.getResources().getStringArray(R.array.cabc_modes);
        CabcValues = context.getResources().getStringArray(R.array.cabc_values);
    }

    private void updateCurrentCabcmode() {
        currentCabcMode = Arrays.asList(CabcValues).indexOf(mConfig.isCabcCurrentlyEnabled(mConfig.getCabcPath()));
    }

    private void updateCabcTile() {
        tile.setState(currentCabcMode > 0 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setContentDescription(CabcModes[currentCabcMode]);
        tile.setSubtitle(CabcModes[currentCabcMode]);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        mConfig = DisplayFeaturesConfig.getInstance(this);
        tile = getQsTile();

        if (!FileUtils.fileExists(mConfig.getCabcPath())) {
            tile.setState(Tile.STATE_UNAVAILABLE);
            tile.setSubtitle(getResources().getString(R.string.cabc_summary_not_supported));
            tile.updateTile();
            return;
        }

        updateCurrentCabcmode();
        updateCabcTile();

        IntentFilter filter = new IntentFilter(mConfig.ACTION_CABC_SERVICE_CHANGED);
        registerReceiver(mServiceStateReceiver, filter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        if (!mInternalStart) unregisterReceiver(mServiceStateReceiver);
    }

    @Override
    public void onClick() {
        super.onClick();
        mInternalStart = true;
        updateCurrentCabcmode();

        if (currentCabcMode == CabcModes.length - 1) {
	currentCabcMode = 0;
        } else {
	currentCabcMode++;
        }

        FileUtils.writeLine(mConfig.getCabcPath(), CabcValues[currentCabcMode]);

        String value = mConfig.isCabcCurrentlyEnabled(mConfig.getCabcPath());
        Boolean enabled = (!value.equals("0"));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.edit().putString(mConfig.DISPLAYFEATURES_CABC_KEY, CabcValues[currentCabcMode]).commit();

        Intent intent = new Intent(mConfig.ACTION_CABC_SERVICE_CHANGED);

        intent.putExtra(mConfig.EXTRA_CABC_STATE, enabled);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        this.sendBroadcastAsUser(intent, UserHandle.CURRENT);;

        updateCabcTile();

    }

    private void tryStopService() {
        if (mCabcIntent == null) return;
        this.stopService(mCabcIntent);
        mCabcIntent = null;
    }
}
