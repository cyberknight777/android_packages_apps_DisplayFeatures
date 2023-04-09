/*
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
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import com.android.displayfeatures.R;
import com.android.displayfeatures.utils.FileUtils;

public class DisplayFeaturesDcDimTileService extends TileService {

    private DisplayFeaturesConfig mConfig;

    private Intent mDcDimIntent;

    private boolean mInternalStart;

    private final BroadcastReceiver mServiceStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mInternalStart) {
                mInternalStart = false;
                return;
            }
            updateUI();
        }
    };

    private void updateUI() {
        final Tile tile = getQsTile();
        boolean enabled = mConfig.isCurrentlyEnabled(mConfig.getDcDimPath());

        if (!enabled) tryStopService();

        tile.setIcon(Icon.createWithResource(this, enabled ? R.drawable.ic_dc_dimming_on : R.drawable.ic_dc_dimming_off));
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        mConfig = DisplayFeaturesConfig.getInstance(this);

        updateUI();

        IntentFilter filter = new IntentFilter(mConfig.ACTION_DC_DIM_SERVICE_CHANGED);
        registerReceiver(mServiceStateReceiver, filter);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        unregisterReceiver(mServiceStateReceiver);
    }

    @Override
    public void onClick() {
        super.onClick();
        mInternalStart = true;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean enabled = !mConfig.isCurrentlyEnabled(mConfig.getDcDimPath());
        FileUtils.writeLine(mConfig.getDcDimPath(), enabled ? "1" : "0");

        sharedPrefs.edit().putBoolean(mConfig.DISPLAYFEATURES_DC_DIMMING_KEY, enabled).commit();

        Intent intent = new Intent(mConfig.ACTION_DC_DIM_SERVICE_CHANGED);

        intent.putExtra(mConfig.EXTRA_DC_DIM_STATE, enabled);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        this.sendBroadcastAsUser(intent, UserHandle.CURRENT);;

        updateUI();
    }

    private void tryStopService() {
        if (mDcDimIntent == null) return;
        this.stopService(mDcDimIntent);
        mDcDimIntent = null;
    }
}
