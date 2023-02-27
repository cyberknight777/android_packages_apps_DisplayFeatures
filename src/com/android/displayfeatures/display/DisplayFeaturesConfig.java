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
 }
