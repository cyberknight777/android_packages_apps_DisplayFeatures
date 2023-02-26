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

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import com.android.displayfeatures.R;
import com.android.displayfeatures.utils.FileUtils;

public class DisplayFeaturesDcDimTileService extends TileService {

    private Context mContext;
    private static final String DISPLAYFEATURES_DC_DIMMING_KEY = "dc_dimming";
    private String DISPLAYFEATURES_DC_DIMMING_NODE;

    private void updateUI(boolean enabled) {
        final Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this, enabled ? R.drawable.ic_dc_dimming_on : R.drawable.ic_dc_dimming_off));
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        updateUI(sharedPrefs.getBoolean(DISPLAYFEATURES_DC_DIMMING_KEY, false));
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();

        mContext = this;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        DISPLAYFEATURES_DC_DIMMING_NODE = mContext.getResources().getString(com.android.displayfeatures.R.string.config_DisplayFeaturesDcDimPath);
        final boolean enabled = !(sharedPrefs.getBoolean(DISPLAYFEATURES_DC_DIMMING_KEY, false));

        FileUtils.writeLine(DISPLAYFEATURES_DC_DIMMING_NODE, enabled ? "1" : "0");
        sharedPrefs.edit().putBoolean(DISPLAYFEATURES_DC_DIMMING_KEY, enabled).commit();
        updateUI(enabled);
    }
}
