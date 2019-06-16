package ru.home.mediafilerenamer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyApplication extends Application
{
    private static Context mContext;

    private static SharedPreferences mSharedPreferences;

    private static boolean mJpgOn;
    private static boolean mMp4On;
    private static boolean mJpegOn;
    private static boolean mVerboseLog;
    private static boolean mShowMediaFiles;
    private static String mInitialFolder;
    private static String mRenameMask;


    public static boolean isJpgOn()
    {
        return MyApplication.mJpgOn;
    }

    public static void setJpgOn(boolean isJpgOn)
    {
        MyApplication.mJpgOn = isJpgOn;
    }

    public static boolean isMp4On()
    {
        return MyApplication.mMp4On;
    }

    public static void setMp4On(boolean isMp4On)
    {
        MyApplication.mMp4On = isMp4On;
    }

    public static boolean isJpegOn()
    {
        return MyApplication.mJpegOn;
    }

    public static void setJpegOn(boolean isJpegOn)
    {
        MyApplication.mJpegOn = isJpegOn;
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

        MyApplication.mJpgOn = MyApplication.mSharedPreferences.getBoolean("mJpgOn", false);
        MyApplication.mMp4On = MyApplication.mSharedPreferences.getBoolean("mMp4On", true);
        MyApplication.mJpegOn = MyApplication.mSharedPreferences.getBoolean("mJpegOn", false);
        MyApplication.mVerboseLog = MyApplication.mSharedPreferences.getBoolean("mVerboseLog", true);
        MyApplication.mShowMediaFiles = MyApplication.mSharedPreferences.getBoolean("mShowMediaFiles", true);
        MyApplication.mInitialFolder = MyApplication.mSharedPreferences.getString("mInitialFolder", "/storage/emulated/0/");
        MyApplication.mRenameMask = MyApplication.mSharedPreferences.getString("mRenameMask", "yyyy-MM-dd HH-mm-ss");

    }
}