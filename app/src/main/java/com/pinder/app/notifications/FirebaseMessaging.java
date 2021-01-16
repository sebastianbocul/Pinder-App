package com.pinder.app.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pinder.app.BaseApplication;
import com.pinder.app.MainActivity;
import com.pinder.app.R;
import com.pinder.app.ui.dialogs.SharedPreferencesHelper;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessaging";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (!BaseApplication.inForeground) {
            Log.d(TAG, "onMessageReceived: APP IN BACKGROUND");
            String saveCurrentUser = SharedPreferencesHelper.getCurrentUserID(this);
            String sent = remoteMessage.getData().get("sent");
            String user = remoteMessage.getData().get("user");
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fUser != null && sent.equals(fUser.getUid())) {
                if (!saveCurrentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
                        notification1.sendOAndAboveNotification(remoteMessage);
                    } else {
                        sendNormalNotification(remoteMessage);
                    }
                }
            }
        }else {
            Log.d(TAG, "onMessageReceived: APP IN FOREGROUND");
        }
    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String profileImageUrl = remoteMessage.getData().get("profileImageUrl");
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("matchId", user);
        intent.putExtra("matchName", title);
        intent.putExtra("matchImageUrl", profileImageUrl);
        intent.putExtra("fromActivity", "notification");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (profileImageUrl.equals("default")) {
            Bitmap resource = BitmapFactory.decodeResource(getApplication().getResources(),
                    R.drawable.ic_logo_256);
            NotificationCompat.Builder builder = getNotifications(title, body, pIntent, defSoundUri, resource);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            notificationManager.notify(j, builder.build());
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(profileImageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            NotificationCompat.Builder builder = getNotifications(title, body, pIntent, defSoundUri, resource);
                            int j = 0;
                            if (i > 0) {
                                j = i;
                            }
                            notificationManager.notify(j, builder.build());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
    }

    public NotificationCompat.Builder getNotifications(String title, String body, PendingIntent pIntent, Uri soundUri, Bitmap userImage) {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_logovector)
                .setColor(color)
                .setLargeIcon(userImage)
                .setContentIntent(pIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setAutoCancel(true);
    }
}
