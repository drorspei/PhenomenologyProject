package com.wordpress.drorspei.phenomenologyproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("PhenomenologyProject", "OnAlarmReceiver Received intent");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Intent serviceIntent = new Intent(context, NotificationService.class);
            serviceIntent.setAction(intent.getAction());
            serviceIntent.putExtras(intent);
            context.startService(serviceIntent);
        }
    }
}
