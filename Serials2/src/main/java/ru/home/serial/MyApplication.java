package ru.home.serial;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application
{
    private static Context mContext;

    @Override
	public void onCreate()
    {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }
}
