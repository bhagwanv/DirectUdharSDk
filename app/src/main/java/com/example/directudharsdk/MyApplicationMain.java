package com.example.directudharsdk;

import android.app.Activity;
import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.sk.directudhar.MyApplication;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Created by user on 5/26/2017.
 */

public class MyApplicationMain extends Application {
    private static MyApplicationMain mInstance;

    public Activity activity=null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        MyApplication.Companion.initialize(mInstance);
    }


   // public static synchronized MyApplicationMain getInstance() {
        //return mInstance;
    //}
}