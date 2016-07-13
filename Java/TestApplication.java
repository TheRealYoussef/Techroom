package com.google.firebase.quickstart.fcm;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Youssef on 05-Jul-16.
 */
public class TestApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public static boolean APPLICATION_ON_PAUSE = true;
    private static final String TAG = "TestApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity arg0, Bundle arg1) {
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        APPLICATION_ON_PAUSE = true;
        Log.d(TAG, "onActivityPaused " + activity.getClass());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        APPLICATION_ON_PAUSE = false;
        Log.d(TAG, "onActivityResumed " + activity.getClass());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "onActivityStarted");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped");
    }
}