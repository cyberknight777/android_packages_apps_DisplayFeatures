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
import android.content.res.Resources;
import com.android.settingslib.widget.R;

import com.android.displayfeatures.utils.FileUtils;

public class DisplayFeaturesConfig {

    private static DisplayFeaturesConfig instance = null;

    public static DisplayFeaturesConfig getInstance(Context context) {

        if (instance == null) {
            instance = new DisplayFeaturesConfig(context.getApplicationContext());
        }

        return instance;
    }

    public static final String DISPLAYFEATURES_DC_DIMMING_KEY = "dc_dimming";
    public static final String DISPLAYFEATURES_HBM_KEY = "hbm";

    private final String config_DisplayFeaturesDcDimPath;
    private final String config_DisplayFeaturesHbmPath;

    public static final String ACTION_HBM_SERVICE_CHANGED = "com.android.displayfeatures.display.HBM_SERVICE_CHANGED";
    public static final String EXTRA_HBM_STATE = "hbmenabled";

    public static final String ACTION_DC_DIM_SERVICE_CHANGED = "com.android.displayfeatures.display.DC_DIM_SERVICE_CHANGED";
    public static final String EXTRA_DC_DIM_STATE = "dcdimenabled";

    private DisplayFeaturesConfig(Context context) {

	Resources res = context.getResources();

	config_DisplayFeaturesDcDimPath = res.getString(R.string.config_DisplayFeaturesDcDimPath);
	config_DisplayFeaturesHbmPath = res.getString(R.string.config_DisplayFeaturesHbmPath);

    }

    public String getDcDimPath() {
        return config_DisplayFeaturesDcDimPath;
    }

    public String getHbmPath() {
        return config_DisplayFeaturesHbmPath;
    }

    public boolean isCurrentlyEnabled(String node) {
        return FileUtils.getNodeValueAsBoolean(node, false);
    }
 }
