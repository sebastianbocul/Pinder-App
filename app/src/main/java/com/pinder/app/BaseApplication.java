package com.pinder.app;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.database.FirebaseDatabase;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class BaseApplication extends Application implements LifecycleObserver {
    public static boolean inForeground = false;
    private static final String TAG = "BaseApplication";

    public enum LoginEnum {
        LOGGED, NOT_LOGGED
    }

    public static LoginEnum UserStatus;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
//        // Initialize the Couchbase Lite system
//        CouchbaseLite.init(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        Log.d(TAG, "onAppBackgrounded: ");
        inForeground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        clearNotifications();
        Log.d(TAG, "onAppForegrounded: ");
        inForeground = true;
    }

    private void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}