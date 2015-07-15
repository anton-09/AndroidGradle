package ru.home.fitness;

import android.app.Application;
import android.content.Context;

import java.text.SimpleDateFormat;

import ru.home.fitness.adapters.FitnessDbAdapter;

public class MyApplication extends Application
{
    private static Context mContext;
    private static FitnessDbAdapter mFitnessDbAdapter;
    private static Integer mTheme;
    public static final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat viewDateFormat = new SimpleDateFormat("dd MMMM yyyy (EEEE)");



    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }

    public static FitnessDbAdapter getDBAdapter()
    {
        return MyApplication.mFitnessDbAdapter;
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

        mFitnessDbAdapter = new FitnessDbAdapter();
        mFitnessDbAdapter.open();

        mTheme = R.style.AppTheme_Light;
    }
}