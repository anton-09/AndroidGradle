package ru.home.babylog;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application
{
    private static Context mContext;
    private static BabyLogDbAdapter mBabyLogDbAdapter;

    @Override
    public void onCreate()
    {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();

        mBabyLogDbAdapter = new BabyLogDbAdapter();
        mBabyLogDbAdapter.open();

    }

    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }

    public static BabyLogDbAdapter getDBAdapter()
    {
        return MyApplication.mBabyLogDbAdapter;
    }
}