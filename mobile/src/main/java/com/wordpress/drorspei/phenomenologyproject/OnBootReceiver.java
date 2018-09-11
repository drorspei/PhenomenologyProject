package com.wordpress.drorspei.phenomenologyproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {
            Log.d("OnBootReceiver", "Boot completed");
            new PhenomenonNotificationManager(context).setRandomTimeAll();
        }
    }
}
