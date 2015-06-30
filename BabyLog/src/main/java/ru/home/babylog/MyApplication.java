package ru.home.babylog;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application
{
    private static Context mContext;
    private static BabyLogDbAdapter mBabyLogDbAdapter;
    private static Integer mTheme;

    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }

    public static BabyLogDbAdapter getDBAdapter()
    {
        return MyApplication.mBabyLogDbAdapter;
    }

    public static void toggleTheme()
    {
        if (MyApplication.mTheme == R.style.AppTheme_Light)
            MyApplication.mTheme = R.style.AppTheme_Dark;
        else
            MyApplication.mTheme = R.style.AppTheme_Light;
    }

    public static Integer getCurrentTheme()
    {
        return MyApplication.mTheme;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();

        mBabyLogDbAdapter = new BabyLogDbAdapter();
        mBabyLogDbAdapter.open();

        mTheme = R.style.AppTheme_Light;
    }
}