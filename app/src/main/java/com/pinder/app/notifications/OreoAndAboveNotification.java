package com.pinder.app.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.RemoteMessage;
import com.pinder.app.MainActivity;
import com.pinder.app.R;

import java.util.Random;

public class OreoAndAboveNotification extends ContextWrapper {
    private static final String ID = "some id";
    private static final String NAME = "FirebaseAPP";
    private NotificationManager notificationManager;

    public OreoAndAboveNotification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotifications(String title, String body, PendingIntent pIntent, Uri soundUri, Bitmap userImage) {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return new Notification.Builder(getApplicationContext(), ID)
                .setSmallIcon(R.drawable.ic_logovector)
                .setColor(color)
                .setLargeIcon(userImage)
                .setContentIntent(pIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setGroup(title)
                .setAutoCancel(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String profileImageUrl = remoteMessage.getData().get("profileImageUrl");
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("matchId", user);
        intent.putExtra("matchName", title);
        intent.putExtra("matchImageUrl", profileImageUrl);
        intent.putExtra("fromActivity", "notification");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (profileImageUrl.equals("default")) {
            Bitmap resource = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.ic_logo_256);
            Notification.Builder builder = getNotifications(title, body, pIntent, defSoundUri, resource);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            getManager().notify(j, builder.build());
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(profileImageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Notification.Builder builder = getNotifications(title, body, pIntent, defSoundUri, resource);
                            int j = 0;
                            if (i > 0) {
                                j = i;
                            }
                            getManager().notify(j, builder.build());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
    }
}
