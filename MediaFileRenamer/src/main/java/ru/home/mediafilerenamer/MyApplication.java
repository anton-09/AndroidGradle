package ru.home.mediafilerenamer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyApplication extends Application
{
    private static Context mContext;

    private static SharedPreferences mSharedPreferences;

    private static boolean mJPGon;
    private static boolean mMP4on;
    private static boolean mVerboseLog;
    private static boolean mShowMediaFiles;
    private static String mInitialFolder;
    private static String mRenameMask;


    public static boolean isJPGon()
    {
        return MyApplication.mJPGon;
    }

    public static void setJPGon(boolean isJPGon)
    {
        MyApplication.mJPGon = isJPGon;
    }

    public static boolean isMP4on()
    {
        return MyApplication.mMP4on;
    }

    public static void setMP4on(boolean isMP4on)
    {
        MyApplication.mMP4on = isMP4on;
    }

    public static boolean isVerboseLog()
    {
        return MyApplication.mVerboseLog;
    }

    public static void setVerboseLog(boolean verboseLog)
    {
        MyApplication.mVerboseLog = verboseLog;
    }

    public static boolean isShowMediaFiles()
    {
        return MyApplication.mShowMediaFiles;
    }

    public static void setShowMediaFiles(boolean showMediaFiles)
    {
        MyApplication.mShowMediaFiles = showMediaFiles;
    }

    public static String getInitialFolder()
    {
        return MyApplication.mInitialFolder;
    }

    public static void setInitialFolder(String initialFolder)
    {
        MyApplication.mInitialFolder = initialFolder;
    }

    public static String getRenameMask()
    {
        return MyApplication.mRenameMask;
    }

    public static void setRenameMask(String renameMask)
    {
        MyApplication.mRenameMask = renameMask;
    }

    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }

    public static SharedPreferences getSharedPreferences()
    {
        return MyApplication.mSharedPreferences;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();
        MyApplication.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MyApplication.mJPGon = MyApplication.mSharedPreferences.getBoolean("mJPGon", false);
        MyApplication.mMP4on = MyApplication.mSharedPreferences.getBoolean("mMP4on", true);
        MyApplication.mVerboseLog = MyApplication.mSharedPreferences.getBoolean("mVerboseLog", true);
        MyApplication.mShowMediaFiles = MyApplication.mSharedPreferences.getBoolean("mShowMediaFiles", true);
        MyApplication.mInitialFolder = MyApplication.mSharedPreferences.getString("mInitialFolder", "/");
        MyApplication.mRenameMask = MyApplication.mSharedPreferences.getString("mRenameMask", "yyyy-MM-dd HH-mm-ss");

    }
}