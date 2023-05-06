package com.aygreyg.trill;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "trill_notification_channel";
    private final int NOTIFICATION_ID = 2222;

    private NotificationManager mManager;
    private Context mContext;

    public NotificationHelper(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel
                (CHANNEL_ID, "Trill Notification", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.setLightColor(Color.MAGENTA);
        channel.enableVibration(true);
        channel.setDescription("Notifications from Trill");

        mManager.createNotificationChannel(channel);
    }

    public void send(String message) {
        Intent intent = new Intent(mContext, FeedActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Trill")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent);

        mManager.notify(NOTIFICATION_ID, builder.build());

        Log.d(NotificationHelper.class.toString(), "NOTIF: " + message);
    }

    public void cancel() {
        mManager.cancel(NOTIFICATION_ID);
    }
}
