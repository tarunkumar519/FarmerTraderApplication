package com.farmer.trader.applications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.stopService(new Intent(context, BackgroundService.class));
                context.startForegroundService(new Intent(context, BackgroundService.class));
            } else {
                context.stopService(new Intent(context, BackgroundService.class));
                context.startService(new Intent(context, BackgroundService.class));
            }
        } catch (Exception e) {
        }
    }
}
