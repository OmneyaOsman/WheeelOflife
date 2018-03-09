package com.omni.wheeeloflife.utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


public class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
