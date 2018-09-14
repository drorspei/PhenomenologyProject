package com.wordpress.drorspei.phenomenologyproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.wordpress.drorspei.phenomenologyproject.data.IPhenomenaDb;
import com.wordpress.drorspei.phenomenologyproject.data.IScheduleDb;
import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;
import com.wordpress.drorspei.phenomenologyproject.data.ScheduleItem;
import com.wordpress.drorspei.phenomenologyproject.timedistributions.IPhenomenonTimeDistribution;
import com.wordpress.drorspei.phenomenologyproject.timedistributions.PhenomenonTimePoissonDistribution;
import jsondbs.JsonPhenomenaDb;
import jsondbs.JsonScheduleDb;

import java.text.SimpleDateFormat;
import java.util.Date;

class PhenomenonNotificationManager {
    private Context context;
    private AlarmManager alarmManager;
    private final static int intentIndex = 0;

    PhenomenonNotificationManager(Context context_) {
        context = context_;
        alarmManager = (AlarmManager)context_.getSystemService(Context.ALARM_SERVICE);
    }

    void setNextNotification() {
        Log.d("PhenomenologyProject", "PhenomenonNotificationManager.setNextNotification called");

        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.setAction("showNotification");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intentIndex, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);

            IScheduleDb scheduleDb = new JsonScheduleDb();
            ScheduleItem scheduleItem = scheduleDb.getNext();
            if (scheduleItem != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, scheduleItem.date.getTime(), pendingIntent);
                Log.d("PhenomenologyProject",
                        String.format("setNextNotification next %s at: %s",
                                scheduleItem.phenomenon.title,
                                SimpleDateFormat.getDateTimeInstance().format(scheduleItem.date)));
            } else {
                Log.d("PhenomenologyProject", "setNextNotification no schedule items");
            }
        } else {
            Log.d("PhenomenologyProject", "setNextNotification failed to schedule phenomenon notification");
        }
    }

    void setRandomTimeAll() {
        IPhenomenaDb phenomenaDb = new JsonPhenomenaDb();
        IScheduleDb scheduleDb = new JsonScheduleDb();
        IPhenomenonTimeDistribution timeDistribution = new PhenomenonTimePoissonDistribution();

        // Clear schedule.
        scheduleDb.clear();

        // Schedule all phenomenons.
        // This is a bit inefficient with current implementation since it saves to storage on every addition.
        for (Phenomenon phenomenon : phenomenaDb.getAll()) {
            scheduleDb.add(phenomenon, timeDistribution.nextTime(phenomenon, new Date()));
        }

        // Set notification in time for next schudled phenomenon notification.
        setNextNotification();
    }
}
