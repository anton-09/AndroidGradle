package ru.home.yoga;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

public class MyApplication extends Application
{
    private static Context mContext;
    private static YogaDbAdapter mYogaDbAdapter;

    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }

    public static YogaDbAdapter getDBAdapter()
    {
        return MyApplication.mYogaDbAdapter;
    }

    static
    {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();

        mYogaDbAdapter = new YogaDbAdapter();
        mYogaDbAdapter.open();

    }
}