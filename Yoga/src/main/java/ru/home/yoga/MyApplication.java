// https://icons8.com/icon/l7Iqa1k8Ft-i/crowd
// https://icons8.com/icon/110276/crowd
// https://icons8.com/icon/11901/people
// https://icons8.com/icon/7csVZvHoQrLW/zoom
// https://icons8.com/icon/63204/skype
// https://icons8.com/icon/32323/instagram
// letter icons
// font @Gungsuh
// size 29
package ru.home.yoga;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.home.yoga.model.db.YogaDbAdapter;


public class MyApplication extends Application
{
    private static Context mContext;
    private static YogaDbAdapter mYogaDbAdapter;

    public static DateTimeFormatter mBackupDateFormat = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    public static DateTimeFormatter mDbDateFormat = DateTimeFormat.forPattern("yyyyMMdd");
    public static DateTimeFormatter mViewFullDateFormat = DateTimeFormat.forPattern("dd MMM yyyy, EEEE");
    public static DateTimeFormatter mViewShortDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
    public static DateTimeFormatter mHumanReadableDateFormat = DateTimeFormat.forPattern("dd MMM yyyy HH:mm:ss, EEEE");
    public static DateTimeFormatter mSummaryDbDateFormat = DateTimeFormat.forPattern("yyyyMM");
    public static DateTimeFormatter mSummaryViewDateFormat = DateTimeFormat.forPattern("MMMM yyyy");


    public static final int ITEMS_PER_PAGE = 100;

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