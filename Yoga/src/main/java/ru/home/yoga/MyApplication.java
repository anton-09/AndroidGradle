package ru.home.yoga;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import java.text.SimpleDateFormat;

public class MyApplication extends Application
{
    private static Context mContext;
    private static YogaDbAdapter mYogaDbAdapter;

    public static SimpleDateFormat mBackupDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat mDbDateFormat = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat mViewFullDateFormat = new SimpleDateFormat("dd MMM yyyy, EEEE");
    public static SimpleDateFormat mViewShortDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static SimpleDateFormat mHumanReadableDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss, EEEE");


    public static final int ITEMS_PER_PAGE = 30;

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