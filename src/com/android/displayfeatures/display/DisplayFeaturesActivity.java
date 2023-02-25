/*
 * Copyright (C) 2015-2016 The CyanogenMod Project
 *               2020 YAAP
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

package com.android.displayfeatures.display;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DisplayFeaturesActivity extends PreferenceActivity {

    private static final String TAG_DISPLAYFEATURES = "displayfeatures";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new DisplayFeaturesFragment(), TAG_DISPLAYFEATURES).commit();
    }
}
