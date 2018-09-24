package com.wordpress.drorspei.phenomenologyproject;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.wordpress.drorspei.phenomenologyproject.data.*;
import com.wordpress.drorspei.phenomenologyproject.timedistributions.IPhenomenonTimeDistribution;
import com.wordpress.drorspei.phenomenologyproject.timedistributions.PhenomenonTimePoissonDistribution;
import jsondbs.JsonPhenomenaDb;
import jsondbs.JsonSavedPhenomenaDb;
import jsondbs.JsonScheduleDb;

import java.util.*;

public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationService");
    }

    private static int runningNotificationIndex = 0;
    private static int runningRequestCode = 1 << 16;

    static void showNotification(Context context, Phenomenon phenomenon) {
        Log.d("NotificationService", "Showing notification");
        int ind = runningNotificationIndex++;
        final String NOTIFICATION_CHANNEL_ID = "phenomenon_notifications_4";

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Gotta set a channel for new Androids. What a bummer, really.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "My Notifications",
                        NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 200, 1000});
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            // Start building the phenomenon notification.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Phenomenon Notification")
                    .setContentText(phenomenon.title)
                    .setLights(Color.BLUE, 500, 500)
                    .setVibrate(new long[] {0, 1000, 200, 1000})
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH);

            // Add the buttons.
            int buttonIndex = 0;
            for (String button : phenomenon.buttons) {
                if (!button.isEmpty()) {
                    Intent intent = new Intent(context, NotificationService.class);
                    intent.setAction("saveAndContinuePhenomenon");
                    intent.putExtra("phenomenon", phenomenon);
                    intent.putExtra("buttonIndex", buttonIndex);
                    intent.putExtra("notificationIndex", ind);

                    PendingIntent pendingIntent = PendingIntent.getService(context, runningRequestCode++,
                            intent, PendingIntent.FLAG_ONE_SHOT);
                    NotificationCompat.Action action = new NotificationCompat.Action
                            .Builder(android.R.drawable.ic_menu_add, button, pendingIntent).build();

                    builder.addAction(action);
                }

                buttonIndex++;
            }

            Notification notification = builder.build();

            // Finally show the notification.
            notificationManager.notify(ind, notification);
        }
    }

    private void saveAndContinuePhenomenon(Phenomenon phenomenon, int buttonIndex) {
        // Save phenomenon.
        ISavedPhenomenaDb savedPhenomenaDb = new JsonSavedPhenomenaDb();
        savedPhenomenaDb.add(new SavedPhenomenon(phenomenon, buttonIndex, new Date()));

        Log.d("PhenomenologyProject",
                String.format("NotificationService saveAndContinuePhenomenon: %s, %s",
                        phenomenon.title, phenomenon.buttons[buttonIndex]));

        // Get continuation if it exists and show it.
        IPhenomenaDb phenomenaDb = new JsonPhenomenaDb();
        Phenomenon continuation = phenomenaDb.getByTitle(phenomenon.continuations[buttonIndex]);

        if (continuation != null) {
            showNotification(this, continuation);
        }
    }

    @Override
    final protected void onHandleIntent(Intent intent) {
        Log.d("NotificationService", "Got intent");

        String action = intent.getAction();

        if (action != null) {
            if(intent.getAction().equals("showNotification")) {
                Log.d("NotificationService", "Got showNotification intent");

                IScheduleDb scheduleDb = new JsonScheduleDb();
                ScheduleItem scheduleItem = scheduleDb.getNext();

                // If we have anything scheduled.
                if (scheduleItem != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -5);
                    Date fiveMinutesAgo = calendar.getTime();

                    calendar.add(Calendar.MINUTE, 10);
                    Date inFiveMinutes = calendar.getTime();

                    // Check we're not too early for this notification.
                    if (scheduleItem.date.before(inFiveMinutes)) {
                        // If scheduled item is for the next five minutes, meh, show it now.
                        if (scheduleItem.date.after(fiveMinutesAgo)) {
                            showNotification(this, scheduleItem.phenomenon);
                        }

                        // Shown or old, remove it from schedule.
                        scheduleDb.remove(scheduleItem.phenomenon);

                        // Reschedule the phenomenon notification for a random time.
                        IPhenomenonTimeDistribution timeDistribution = new PhenomenonTimePoissonDistribution();
                        scheduleDb.add(scheduleItem.phenomenon,
                                timeDistribution.nextTime(scheduleItem.phenomenon, new Date()));
                    }

                    // Set notification in time for next schedule item, whether it's the same or a new one.
                    new PhenomenonNotificationManager(this).setNextNotification();
                }
            } else if(action.equals("saveAndContinuePhenomenon")) {
                Log.d("NotificationService", "Got saveAndContinuePhenomenon intent");

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Phenomenon phenomenon = bundle.getParcelable("phenomenon");
                    int buttonIndex = bundle.getInt("buttonIndex");
                    int ind = bundle.getInt("notificationIndex");

                    // Cancel present notification.
                    NotificationManager notificationManager = (NotificationManager) this.
                            getSystemService(NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.cancel(ind);
                    }

                    // Call to save phenomenon.
                    if (phenomenon != null) {
                        saveAndContinuePhenomenon(phenomenon, buttonIndex);
                    }
                }
            }
        }
    }
}
