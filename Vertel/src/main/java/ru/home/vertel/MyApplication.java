package ru.home.vertel;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application
{
    private static Context mContext;
    private static VertelDbAdapter mVertelDbAdapter;
    private static int mCropFactor;


    @Override
	public void onCreate()
    {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();

        mVertelDbAdapter = new VertelDbAdapter();
        mVertelDbAdapter.open();

        mCropFactor = mVertelDbAdapter.getCropFactor();
    }

    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }

    public static VertelDbAdapter getDBAdapter()
    {
        return MyApplication.mVertelDbAdapter;
    }
    
    public static int getCropFactor()
    {
        return mCropFactor;
    }

    public static void zoomIn()
    {
        if (mCropFactor > 5)
        {
            mCropFactor--;
            mVertelDbAdapter.updateCropFactor(mCropFactor);
        }
    }

    public static void zoomOut()
    {
        if (mCropFactor < 10)
        {
            mCropFactor++;
            mVertelDbAdapter.updateCropFactor(mCropFactor);
        }
    }

}