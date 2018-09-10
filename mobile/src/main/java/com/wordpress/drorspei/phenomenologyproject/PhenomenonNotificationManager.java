package com.wordpress.drorspei.phenomenologyproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.*;

public class PhenomenonNotificationManager {
    private Context context;
    private AlarmManager alarmManager;

    public PhenomenonNotificationManager(Context context_) {
        context = context_;
        alarmManager = (AlarmManager)context_.getSystemService(Context.ALARM_SERVICE);
    }

    public PendingIntent notificationPendingIntent(int phenomenonIndex, Phenomenon phenomenon) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.setAction("showNotification");
        intent.putExtra("title", phenomenon.title);
        intent.putExtra("button1", phenomenon.button1);
        intent.putExtra("button2", phenomenon.button2);
        intent.putExtra("button3", phenomenon.button3);

        return PendingIntent.getBroadcast(context,
                phenomenonIndex,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void setNotification(int phenomenonIndex, Phenomenon phenomenon, long when) {
        Log.d("PhenomenonNotificationManager", "Setting notification");

        PendingIntent pendingIntent = notificationPendingIntent(phenomenonIndex, phenomenon);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);

            alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
        } else {
            Log.d("PhenomenonNotificationManager", "Couldn't schedule phenomenon notification");
        }
    }

    public void setRandomTimeNotification(int phenomenonIndex, Phenomenon phenomenon) {
        if (phenomenon.endtime > phenomenon.starttime && phenomenon.howmany > 0) {
            double lambda = phenomenon.howmany / (double)(phenomenon.endtime - phenomenon.starttime);
            double totalHours = -Math.log(new Random().nextDouble()) / lambda;
            int days = (int)(totalHours / (phenomenon.endtime - phenomenon.starttime));
            double hoursIn = (totalHours - days * (phenomenon.endtime - phenomenon.starttime));

            Calendar cal = new GregorianCalendar();

            cal.add(Calendar.DAY_OF_MONTH,  days);
            if (cal.get(Calendar.HOUR_OF_DAY) < phenomenon.starttime) {
                cal.set(Calendar.HOUR_OF_DAY, phenomenon.starttime);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
            } else if (cal.get(Calendar.HOUR_OF_DAY) > phenomenon.endtime) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, phenomenon.starttime);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
            }

            double hoursToEndOfDay = phenomenon.endtime - (cal.get(Calendar.HOUR_OF_DAY) + (double)(60 * cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND)) / 3600.);

            if (hoursToEndOfDay > hoursIn) {
                cal.add(Calendar.SECOND, (int)(hoursIn * 3600));
            } else {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, phenomenon.starttime);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.add(Calendar.SECOND, (int)((hoursIn - hoursToEndOfDay) * 3600));
            }



            Log.d("PhenomenonNotificationManager",
                    String.format("Notification scheduled for: %s",
                            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",
                                    Locale.getDefault()).format(cal.getTime())));

            setNotification(phenomenonIndex, phenomenon, cal.getTimeInMillis());
        }
    }

    public void cancelNotification(int phenomenonIndex, Phenomenon phenomenon) {
        Log.d("PhenomenonNotificationManager", "Cancelling notification");
        PendingIntent pendingIntent = notificationPendingIntent(phenomenonIndex, phenomenon);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void setRandomTimeAll() {
        ArrayList<Phenomenon> phenomena = FileUtils.loadPhenomena();
        int i = 0;

        for (Phenomenon phenomenon : phenomena) {
            setRandomTimeNotification(i++, phenomenon);
        }
    }
}
