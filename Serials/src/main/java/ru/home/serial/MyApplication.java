package ru.home.serial;

import android.app.Application;
import android.content.Context;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class MyApplication extends Application
{
    public static final DateTimeFormatter mHumanReadableDateFormat = DateTimeFormat.forPattern("dd.MM  HH:mm:ss").withZoneUTC();
    public static final DateTimeFormatter mHumanHiddenDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss").withZoneUTC();
    public static final DateTimeFormatter mHumanFullDateFormat = DateTimeFormat.forPattern("dd MMMM yyyy").withZoneUTC();
    public static final String mEpisodeNumberFormat = "s%02de%03d";
    public static final String mEpisodeNameFormat = "%03d. %s";
    public static final String mEpisodeWithoutNameFormat = "%03d";
    public static final String mSerialDatesRangeFormat = "%4d - %4d";
    public static final String mSerialCountsFormat = "%3d";

    /**
     * Количество лет в крутилке выбора даты
     */
    public static final int mYearDepth = 25;

    /**
     * 0 - показывать все сериалы
     * 1 - показывать только активные сериалы
     * 2 - показывать только сериалы, по которым необходимы действия
     * 3 - то же, что и 4, только на основе таблицы history, а не episodes (NEW)
     * 4 - показывать только сериалы, по которым были активные действия за последний месяц
     */
    public static int mShowSerialFormat = 3;

    /**
     * Количество дней, за которые будут отслеживаться активные действия (по-умолчанию, месяц)
     */
    public static final long mRecentDays = 1l * 1000 * 60 * 60 * 24 * 30;

    /**
     *
     */
    public static final int HISTORY_RELEASE_EPISODE = 1;
    public static final int HISTORY_DOWNLOAD_EPISODE = 2;
    public static final int HISTORY_WATCH_EPISODE = 3;
    public static final int HISTORY_ADD_SERIAL = 4;
    public static final int HISTORY_ADD_SEASON = 5;
    public static final int HISTORY_ADD_EPISODE = 6;



    private static Context mContext;
    private static SerialDbAdapter mSerialDbAdapter;
    private static boolean mNeed2RebuildSerialList;

    public static int mAsyncTaskCount;
    public static int mAsyncTaskMax;
    public static int mAsyncEpisodeMax;
    private static AtomicInteger mAsyncEpisodeCount = new AtomicInteger();

    public static int getAsyncEpisodeCount()
    {
        return mAsyncEpisodeCount.get();
    }

    public static int incrementAsyncEpisodeCount()
    {
        return mAsyncEpisodeCount.incrementAndGet();
    }

    public static void resetAsyncEpisodeCount()
    {
        mAsyncEpisodeCount.set(0);
    }

    @Override
	public void onCreate()
    {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();

        MyApplication.mSerialDbAdapter = new SerialDbAdapter();
        MyApplication.mSerialDbAdapter.open();

        MyApplication.mNeed2RebuildSerialList = false;
    }

    public static Context getAppContext()
    {
        return MyApplication.mContext;
    }

    public static SerialDbAdapter getDBAdapter()
    {
        return MyApplication.mSerialDbAdapter;
    }

    public static boolean need2RebuildSerialList()
    {
        return MyApplication.mNeed2RebuildSerialList;
    }

    public static void setNeed2RebuildSerialList(boolean need2RebuildSerialList)
    {
        MyApplication.mNeed2RebuildSerialList = need2RebuildSerialList;
    }

}
