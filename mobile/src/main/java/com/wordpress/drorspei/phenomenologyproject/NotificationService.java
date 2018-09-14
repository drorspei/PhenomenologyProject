package com.wordpress.drorspei.phenomenologyproject;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;
import com.wordpress.drorspei.phenomenologyproject.data.SavedPhenomenon;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationService");
    }

    private static int runningNotificationIndex = 0;
    private static int runningRequestCode = 1 << 16;

    private void showNotification(String title, String button1, String button2, String button3) {
        Log.d("NotificationService", "Showing notification");
        int ind = runningNotificationIndex++;
        final String NOTIFICATION_CHANNEL_ID = "phenomenon_notifications";

        NotificationManager notificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{1000, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Phenomenon Notification")
                .setContentText(title)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        for (String button : new String[] {button1, button2, button3}) {
            if (!button.isEmpty()) {
                Intent intent = new Intent(this, NotificationService.class);
                intent.setAction("recordPhenomenon");
                intent.putExtra("title", title);
                intent.putExtra("button", button);
                intent.putExtra("notificationIndex", ind);

                PendingIntent pendingIntent = PendingIntent.getService(this, runningRequestCode++,
                        intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Action action = new NotificationCompat.Action
                        .Builder(android.R.drawable.ic_menu_add, button, pendingIntent).build();

                builder.addAction(action);
            }
        }

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        if (notificationManager != null) {
            notificationManager.notify(ind, notification);
        }

        List<Phenomenon> phenomena = FileUtils.loadPhenomena();
        int i = 0;

        for (Phenomenon phenomenon : phenomena) {
            if (phenomenon.title.equals(title)) {
                new PhenomenonNotificationManager(this).setRandomTimeNotification(i, phenomenon);
                break;
            }
            i++;
        }
    }

    private void showNotification(Phenomenon phenomenon) {
        showNotification(phenomenon.title, phenomenon.button1, phenomenon.button2, phenomenon.button3);
    }

    private void savePhenomenon(String title, String button) {
        Log.d("NotificationService", "Saving phenomenon");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        String date = df.format(Calendar.getInstance().getTime());

        SavedPhenomenon savedPhenomenon = new SavedPhenomenon();
        savedPhenomenon.title = title;
        savedPhenomenon.button = button;
        savedPhenomenon.date = date;

        ArrayList<SavedPhenomenon> savedPhenomena = FileUtils.loadSavedPhenomena();
        savedPhenomena.add(savedPhenomenon);
        try {
            FileUtils.saveSavedPhenomena(savedPhenomena);
        } catch (IOException e) {
            Log.d("NotificationService", "Failed to save phenomenon entry");
            e.printStackTrace();
        }
    }

    @Override
    final protected void onHandleIntent(Intent intent) {
        Log.d("NotificationService", "Got intent");

        Bundle bundle = intent.getExtras();
        String action = intent.getAction();

        if (bundle != null && action != null) {
            if(intent.getAction().equals("showNotification")
                    && bundle.containsKey("title")
                    && bundle.containsKey("button1")
                    && bundle.containsKey("button3")
                    && bundle.containsKey("button3")) {
                Log.d("NotificationService", "Got showNotification intent");

                showNotification(bundle.getString("title"),
                        bundle.getString("button1"),
                        bundle.getString("button2"),
                        bundle.getString("button3"));
            }
            else if(action.equals("recordPhenomenon") && bundle.containsKey("title") && bundle.containsKey("button"))
            {
                Log.d("NotificationService", "Got recordPhenomenon intent");

                String title = bundle.getString("title");
                String button = bundle.getString("button");
                int ind = bundle.getInt("notificationIndex");

                savePhenomenon(title, button);

                NotificationManager notificationManager = (NotificationManager) this.
                        getSystemService(NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.cancel(ind);
                }

                Log.d("NotificationService", String.format("Clicked: %s, %s", title, button));

                List<Phenomenon> phenomena = FileUtils.loadPhenomena();
                Map<String, Phenomenon> phenomenaByTitle = new HashMap<>();
                String nextTitle = "";

                for (Phenomenon phenomenon : phenomena) {
                    phenomenaByTitle.put(phenomenon.title, phenomenon);

                    if (nextTitle.isEmpty() && phenomenon.title.equals(title)) {
                        if (phenomenon.button1.equals(button)) {
                            nextTitle = phenomenon.conn1;
                        } else if (phenomenon.button2.equals(button)) {
                            nextTitle = phenomenon.conn2;
                        } else if (phenomenon.button3.equals(button)) {
                            nextTitle = phenomenon.conn3;
                        }
                    }
                }

                if (!nextTitle.isEmpty() && !nextTitle.equals("None")) {
                    if (phenomenaByTitle.containsKey(nextTitle)) {
                        showNotification(phenomenaByTitle.get(nextTitle));
                    }
                }
            }
        }
    }
}
