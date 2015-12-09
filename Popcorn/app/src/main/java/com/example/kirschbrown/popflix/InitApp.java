package com.example.kirschbrown.popflix;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jrkirsch on 12/4/2015.
 */
public class InitApp extends Application {

    public boolean isFirstRun;

    @Override
    public void onCreate() {
        super.onCreate();
        isFirstRun = true;
    }
}
