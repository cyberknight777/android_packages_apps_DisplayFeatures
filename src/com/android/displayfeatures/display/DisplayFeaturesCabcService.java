/*
* Copyright (C) 2020 Yet Another AOSP Project
*               2023-2025 cyberknight777
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.android.displayfeatures.display;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Handler;
import android.os.UserHandle;

import com.android.displayfeatures.utils.FileUtils;

public class DisplayFeaturesCabcService extends Service {

    private DisplayFeaturesConfig mConfig;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mConfig = DisplayFeaturesConfig.getInstance(context);

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                String value = mConfig.isCabcCurrentlyEnabled(mConfig.getCabcPath());
                Boolean enabled = (!value.equals("0"));
                if (enabled) FileUtils.writeLine(mConfig.getCabcPath(), "0");
                Intent cabcIntent = new Intent(context,
                com.android.displayfeatures.display.DisplayFeaturesCabcService.class);

                if (enabled) context.startService(cabcIntent);
                else context.stopService(cabcIntent);

                Intent mChangedIntent = new Intent(mConfig.ACTION_CABC_SERVICE_CHANGED);

                mChangedIntent.putExtra(mConfig.EXTRA_CABC_STATE, enabled);
                mChangedIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
                context.sendBroadcastAsUser(mChangedIntent, UserHandle.CURRENT);;
                stopSelf();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, intentFilter, Context.RECEIVER_EXPORTED);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
