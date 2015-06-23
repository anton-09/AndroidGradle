package ru.home.housing;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application
{
    private static Context mContext;
    private static HousingDbAdapter mHousingDbAdapter;

    @Override
	public void onCreate()
    {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();

        mHousingDbAdapter = new HousingDbAdapter();
        mHousingDbAdapter.open();

    }

    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }

    public static HousingDbAdapter getDBAdapter()
    {
        return MyApplication.mHousingDbAdapter;
    }
}