package com.farmer.trader.applications;


import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import static android.content.Context.ACTIVITY_SERVICE;

public class UtilConstants {

    public static boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackgroundService.class.getName().equals(service.service.getClassName())) {
                Log.d("TAG", "isMyServiceRunning() called with: context = [" + context + "] is true");
                return true;
            }
        }
        return false;
    }
}
