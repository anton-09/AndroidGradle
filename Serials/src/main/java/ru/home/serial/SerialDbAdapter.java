package ru.home.serial;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.*;

public class SerialDbAdapter
{
    private static final String DATABASE_NAME = "serial_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SERIALS = "serials";
    public static final String TABLE_EPISODES = "episodes";
    public static final String TABLE_HISTORY = "history";
    public static final String TABLE_RELEASERS = "releasers";
    public static final String TABLE_TRANSLATORS = "translators";

    public static final String SERIALS_ID = "_id";
    public static final String SERIALS_NAME = "name";
    public static final String SERIALS_ALT_NAME = "alt_name";
    public static final String SERIALS_SEASON_COUNT = "season_count";
    public static final String SERIALS_WITH_EPISODE_NAMES = "episode_with_name";
    public static final String SERIALS_COLUMN_NUMBER = "column_number";
    public static final String SERIALS_IS_ACTIVE = "is_active";
    public static final String SERIALS_SEARCH_NAME = "search_name";
    public static final String SERIALS_RELEASE_STUDIO = "releaser_id";
    public static final String SERIALS_TRANSLATE_STUDIO = "translator_id";

    public static final String EPISODES_ID = "_id";
    public static final String EPISODES_NUMBER = "number";
    public static final String EPISODES_SEASON = "season";
    public static final String EPISODES_NAME = "name";
    public static final String EPISODES_RELEASE_DATE = "release_date";
    public static final String EPISODES_DOWNLOAD_DATE = "download_date";
    public static final String EPISODES_WATCH_DATE = "watch_date";
    public static final String EPISODES_SERIAL_ID = "serial_id";
    public static final String EPISODES_ALT_NAME = "alt_name";

    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_DATE = "event_date";
    public static final String HISTORY_TYPE = "event_type";
    public static final String HISTORY_SERIAL = "serial_id";
    public static final String HISTORY_EPISODE = "episode_id";

    public static final String RELEASERS_ID = "_id";
    public static final String RELEASERS_NAME = "name";
    public static final String RELEASERS_LOGO = "logo";

    public static final String TRANSLATORS_ID = "_id";
    public static final String TRANSLATORS_NAME = "name";
    public static final String TRANSLATORS_LOGO = "logo";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private class DatabaseHelper extends SQLiteOpenHelper
    {
        String mDbPath;

        DatabaseHelper()
        {
            super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
            mDbPath = "/data/data/" + MyApplication.getAppContext().getPackageName() + "/databases/";
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
        }

        public void createDataBase() throws IOException
        {
            boolean mDataBaseExist = new File(mDbPath + DATABASE_NAME).exists();
            if(!mDataBaseExist)
            {
                this.getReadableDatabase();
                this.close();
                try
                {
                    InputStream mInput = MyApplication.getAppContext().getAssets().open(DATABASE_NAME);
                    String outFileName = mDbPath + DATABASE_NAME;
                    OutputStream mOutput = new FileOutputStream(outFileName);
                    byte[] mBuffer = new byte[1024];
                    int mLength;
                    while ((mLength = mInput.read(mBuffer))>0)
                    {
                        mOutput.write(mBuffer, 0, mLength);
                    }
                    mOutput.flush();
                    mOutput.close();
                    mInput.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

    }

    public SerialDbAdapter()
    {
    }

    public SerialDbAdapter open() throws SQLException
    {
        mDbHelper = new DatabaseHelper();
        try {
            mDbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }



    public void addSeason(long serialId)
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("UPDATE " + TABLE_SERIALS + " SET " + SERIALS_SEASON_COUNT + " = " + SERIALS_SEASON_COUNT + " + 1  WHERE " + SERIALS_ID + " = " + serialId);
        s.execute();
    }

    public void deleteSeason(long serialId, int season)
    {
        if (!mDb.isOpen()) open();
        mDb.delete(TABLE_EPISODES, EPISODES_SEASON + " = " + season + " AND " + EPISODES_SERIAL_ID + " = " + serialId, null);
        SQLiteStatement s = mDb.compileStatement("UPDATE " + TABLE_SERIALS + " SET " + SERIALS_SEASON_COUNT + " = " + SERIALS_SEASON_COUNT + " - 1  WHERE " + SERIALS_ID + " = " + serialId);
        s.execute();
    }

    public long addSerial(String name, String altName, int isEpisodeWithName, int columnNumber, int isActive, int releaseStudioId, int translateStudioId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SERIALS_NAME, name);
        contentValues.put(SERIALS_ALT_NAME, altName);
        contentValues.put(SERIALS_SEASON_COUNT, 0);
        contentValues.put(SERIALS_WITH_EPISODE_NAMES, isEpisodeWithName);
        contentValues.put(SERIALS_COLUMN_NUMBER, columnNumber);
        contentValues.put(SERIALS_IS_ACTIVE, isActive);
        contentValues.put(SERIALS_SEARCH_NAME, name.toLowerCase());
        contentValues.put(SERIALS_RELEASE_STUDIO, releaseStudioId);
        contentValues.put(SERIALS_TRANSLATE_STUDIO, translateStudioId);

        if (!mDb.isOpen()) open();
        return mDb.insert(TABLE_SERIALS, null, contentValues);
    }

    public void updateSerial(long serialId, String name, String altName, int isEpisodeWithName, int columnNumber, int isActive, int releaseStudioId, int translateStudioId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SERIALS_NAME, name);
        contentValues.put(SERIALS_ALT_NAME, altName);
        contentValues.put(SERIALS_WITH_EPISODE_NAMES, isEpisodeWithName);
        contentValues.put(SERIALS_COLUMN_NUMBER, columnNumber);
        contentValues.put(SERIALS_IS_ACTIVE, isActive);
        contentValues.put(SERIALS_SEARCH_NAME, name.toLowerCase());
        contentValues.put(SERIALS_RELEASE_STUDIO, releaseStudioId);
        contentValues.put(SERIALS_TRANSLATE_STUDIO, translateStudioId);

        if (!mDb.isOpen()) open();
        mDb.update(TABLE_SERIALS, contentValues, SERIALS_ID + " = " + serialId, null);
    }

    public void deleteSerial(long serialId)
    {
        if (!mDb.isOpen()) open();
        mDb.delete(TABLE_EPISODES, EPISODES_SERIAL_ID + " = " + serialId, null);
        mDb.delete(TABLE_SERIALS, SERIALS_ID + " = " + serialId, null);
    }

    public Cursor getSerials(String filter)
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_SERIALS, new String[] {SERIALS_ID, SERIALS_NAME, SERIALS_ALT_NAME, SERIALS_SEASON_COUNT, SERIALS_WITH_EPISODE_NAMES, SERIALS_COLUMN_NUMBER, SERIALS_IS_ACTIVE, SERIALS_RELEASE_STUDIO, SERIALS_TRANSLATE_STUDIO}, SERIALS_SEARCH_NAME + " LIKE '%" + filter + "%' OR " + SERIALS_ALT_NAME + " LIKE '%" + filter + "%'", null, null, null, null);
    }

    public Cursor getSerialsActive()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_SERIALS, new String[] {SERIALS_ID, SERIALS_NAME, SERIALS_ALT_NAME, SERIALS_SEASON_COUNT, SERIALS_WITH_EPISODE_NAMES, SERIALS_COLUMN_NUMBER, SERIALS_IS_ACTIVE, SERIALS_RELEASE_STUDIO, SERIALS_TRANSLATE_STUDIO}, SERIALS_IS_ACTIVE + " = 1", null, null, null, null);
    }

    public Cursor getSerialsRecentMonthOld()
    {
        if (!mDb.isOpen()) open();
        String query = "SELECT " + SERIALS_ID + ", " + SERIALS_NAME + ", " + SERIALS_ALT_NAME + ", " + SERIALS_SEASON_COUNT + ", " + SERIALS_WITH_EPISODE_NAMES + ", " + SERIALS_COLUMN_NUMBER + ", " + SERIALS_IS_ACTIVE + ", " + SERIALS_RELEASE_STUDIO + ", " + SERIALS_TRANSLATE_STUDIO + ", (SELECT MAX(fld) FROM"  +
                " (SELECT MAX(" + EPISODES_DOWNLOAD_DATE + ")   AS fld FROM " + TABLE_EPISODES + " WHERE " + EPISODES_SERIAL_ID + " = " + TABLE_SERIALS + "." + SERIALS_ID + " AND " + EPISODES_DOWNLOAD_DATE + "   <=CAST(STRFTIME('%s000','now') AS INTEGER) UNION"  +
//              "  SELECT MAX(" + EPISODES_RELEASE_DATE + ")    AS fld FROM " + TABLE_EPISODES + " WHERE " + EPISODES_SERIAL_ID + " = " + TABLE_SERIALS + "." + SERIALS_ID + " AND " + EPISODES_RELEASE_DATE + "    <=CAST(STRFTIME('%s000','now') AS INTEGER) UNION"  +
                "  SELECT MAX(" + EPISODES_WATCH_DATE + ")      AS fld FROM " + TABLE_EPISODES + " WHERE " + EPISODES_SERIAL_ID + " = " + TABLE_SERIALS + "." + SERIALS_ID + " AND " + EPISODES_WATCH_DATE + "      <=CAST(STRFTIME('%s000','now') AS INTEGER)))"  +
                " AS a FROM " + TABLE_SERIALS  +  " WHERE a+" + MyApplication.mRecentDays + ">CAST(STRFTIME('%s000','now') AS INTEGER) " +
                " ORDER BY a DESC";

        return mDb.rawQuery(query, null);
    }

    public Cursor getSerialsRecentMonthNew()
    {
        if (!mDb.isOpen()) open();
        String query = "SELECT s." + SERIALS_ID + ", " + SERIALS_NAME + ", " + SERIALS_ALT_NAME + ", " + SERIALS_SEASON_COUNT + ", " + SERIALS_WITH_EPISODE_NAMES + ", " + SERIALS_COLUMN_NUMBER + ", " + SERIALS_IS_ACTIVE + ", " + SERIALS_RELEASE_STUDIO + ", " + SERIALS_TRANSLATE_STUDIO + ", MAX(h." + HISTORY_DATE + ") FROM " + TABLE_HISTORY + " h INNER JOIN " + TABLE_SERIALS + " s ON h." + HISTORY_SERIAL + " = s." + SERIALS_ID + " WHERE " + HISTORY_DATE + "+" + MyApplication.mRecentDays + ">CAST(STRFTIME('%s000','now') AS INTEGER) GROUP BY " + HISTORY_SERIAL + " ORDER BY MAX(" + HISTORY_DATE + ") DESC";

        return mDb.rawQuery(query, null);
    }

    public Cursor getSerialsToDo()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_SERIALS, new String[] {SERIALS_ID, SERIALS_NAME, SERIALS_ALT_NAME, SERIALS_SEASON_COUNT, SERIALS_WITH_EPISODE_NAMES, SERIALS_COLUMN_NUMBER, SERIALS_IS_ACTIVE, SERIALS_RELEASE_STUDIO, SERIALS_TRANSLATE_STUDIO}, " EXISTS (SELECT 1 FROM " + TABLE_EPISODES + " WHERE (" + EPISODES_RELEASE_DATE + " != -1 AND " + EPISODES_DOWNLOAD_DATE + " = -1 OR " + EPISODES_DOWNLOAD_DATE + " != -1 AND " + EPISODES_WATCH_DATE + " =- 1) AND " + EPISODES_SERIAL_ID + " = " + TABLE_SERIALS + "." + SERIALS_ID + ")", null, null, null, null);
    }
    
    public Cursor getSerialDetails(long serialId)
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_SERIALS, new String[] {SERIALS_ID, SERIALS_NAME, SERIALS_ALT_NAME, SERIALS_SEASON_COUNT, SERIALS_WITH_EPISODE_NAMES, SERIALS_COLUMN_NUMBER, SERIALS_IS_ACTIVE, SERIALS_RELEASE_STUDIO, SERIALS_TRANSLATE_STUDIO}, SERIALS_ID + " = " + serialId, null, null, null, null);
    }

    public int getSerialSeasonCountById(long serialId)
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT " + SERIALS_SEASON_COUNT + " FROM " + TABLE_SERIALS + " WHERE " + SERIALS_ID + " = " + serialId);
        return (int) s.simpleQueryForLong();
    }

    public Cursor getEpisodeDetails(long episodeId)
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_EPISODES, new String[] {EPISODES_ID, EPISODES_NUMBER, EPISODES_SEASON, EPISODES_NAME, EPISODES_RELEASE_DATE, EPISODES_DOWNLOAD_DATE, EPISODES_WATCH_DATE, EPISODES_SERIAL_ID, EPISODES_ALT_NAME}, EPISODES_ID + " = " + episodeId, null, null, null, null);
    }

    public Cursor getEpisodesForHelp()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_EPISODES + " e INNER JOIN " + TABLE_SERIALS + " s ON e." + EPISODES_SERIAL_ID + " = s." + SERIALS_ID, new String[] {EPISODES_NUMBER, EPISODES_SEASON, "e." + EPISODES_NAME, EPISODES_RELEASE_DATE, EPISODES_DOWNLOAD_DATE, EPISODES_WATCH_DATE, "s." + SERIALS_NAME}, null, null, null, null, EPISODES_SEASON + " * 1000 + " + EPISODES_NUMBER + " ASC");
    }

    public Cursor getEpisodes(long serialId, int season)
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_EPISODES, new String[] {EPISODES_ID, EPISODES_NUMBER, EPISODES_SEASON, EPISODES_NAME, EPISODES_RELEASE_DATE, EPISODES_DOWNLOAD_DATE, EPISODES_WATCH_DATE, EPISODES_SERIAL_ID, EPISODES_ALT_NAME}, EPISODES_SERIAL_ID + " = " + serialId + " AND " + EPISODES_SEASON + " = " + season, null, null, null, EPISODES_NUMBER + " DESC");
    }

    public Cursor getEpisodesTimeRange(long serialId)
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_EPISODES, new String[] {"MIN (" + EPISODES_RELEASE_DATE + ")", "MAX (" + EPISODES_RELEASE_DATE + ")"}, EPISODES_SERIAL_ID + " = " + serialId + " AND " + EPISODES_RELEASE_DATE + " != -1", null, null, null, null);
    }

    public int getMaxEpisodeNumberBySerialAndId(long serialId, long season)
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT MAX(" + EPISODES_NUMBER + ") FROM " + TABLE_EPISODES + " WHERE " + EPISODES_SERIAL_ID + " = " + serialId + " AND " + EPISODES_SEASON + " = " + season);
        return (int) s.simpleQueryForLong();
    }

    public int getEpisodeReleasedCountById(long serialId)
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT COUNT(*) FROM " + TABLE_EPISODES + " WHERE " + EPISODES_SERIAL_ID + " = " + serialId + " AND " + EPISODES_RELEASE_DATE + " != -1");
        return (int) s.simpleQueryForLong();
    }

    public int getEpisodeDownloadedCountById(long serialId)
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT COUNT(*) FROM " + TABLE_EPISODES + " WHERE " + EPISODES_SERIAL_ID + " = " + serialId + " AND " + EPISODES_DOWNLOAD_DATE + " != -1");
        return (int) s.simpleQueryForLong();
    }

    public int getEpisodeWatchedCountById(long serialId)
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT COUNT(*) FROM " + TABLE_EPISODES + " WHERE " + EPISODES_SERIAL_ID + " = " + serialId + " AND " + EPISODES_WATCH_DATE + " != -1");
        return (int) s.simpleQueryForLong();
    }

    public long addEpisode(int episodeNumber, int episodeSeason, String episodeName, long episodeReleaseDate, long episodeDownloadDate, long episodeWatchDate, long episodeSerialId, String episodeAltName)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EPISODES_NUMBER, episodeNumber);
        contentValues.put(EPISODES_SEASON, episodeSeason);
        contentValues.put(EPISODES_NAME, episodeName);
        contentValues.put(EPISODES_RELEASE_DATE, episodeReleaseDate);
        contentValues.put(EPISODES_DOWNLOAD_DATE, episodeDownloadDate);
        contentValues.put(EPISODES_WATCH_DATE, episodeWatchDate);
        contentValues.put(EPISODES_SERIAL_ID, episodeSerialId);
        contentValues.put(EPISODES_ALT_NAME, episodeAltName);

        if (!mDb.isOpen()) open();
        return mDb.insert(TABLE_EPISODES, null, contentValues);
    }

    public void updateEpisode(long episodeId, int episodeNumber, String episodeName, long episodeReleaseDate, long episodeDownloadDate, long episodeWatchDate, String episodeAltName)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EPISODES_NUMBER, episodeNumber);
        contentValues.put(EPISODES_NAME, episodeName);
        contentValues.put(EPISODES_RELEASE_DATE, episodeReleaseDate);
        contentValues.put(EPISODES_DOWNLOAD_DATE, episodeDownloadDate);
        contentValues.put(EPISODES_WATCH_DATE, episodeWatchDate);
        contentValues.put(EPISODES_ALT_NAME, episodeAltName);

        if (!mDb.isOpen()) open();
        mDb.update(TABLE_EPISODES, contentValues, EPISODES_ID + " = " + episodeId, null);
    }
    
    public void deleteEpisode(long episodeId)
    {
        if (!mDb.isOpen()) open();
        mDb.delete(TABLE_EPISODES, EPISODES_ID + " = " + episodeId, null);
    }

    public void addHistory(long eventDate, int eventType, long serialId, long episodeId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_DATE, eventDate);
        contentValues.put(HISTORY_TYPE, eventType);
        contentValues.put(HISTORY_SERIAL, serialId);
        contentValues.put(HISTORY_EPISODE, episodeId);

        if (!mDb.isOpen()) open();
        mDb.insert(TABLE_HISTORY, null, contentValues);
    }

    public void deleteHistorySerial(long serialId)
    {
        if (!mDb.isOpen()) open();
        mDb.delete(TABLE_HISTORY, HISTORY_SERIAL + " = " + serialId, null);
    }

    public void deleteHistorySeason(long serialId, int season)
    {
        if (!mDb.isOpen()) open();
        mDb.delete(TABLE_HISTORY, HISTORY_EPISODE + " IN (SELECT " + EPISODES_ID + " FROM " + TABLE_EPISODES + " WHERE " + EPISODES_SERIAL_ID + " = " + serialId + " AND " + EPISODES_SEASON + " = " + season + ")", null);
    }

    public void deleteHistoryEpisode(long episodeId)
    {
        if (!mDb.isOpen()) open();
        mDb.delete(TABLE_HISTORY, HISTORY_EPISODE + " = " + episodeId, null);
    }

    public void updateHistoryEpisodeStatus(long eventDate, int status, long episodeId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_DATE, eventDate);

        if (!mDb.isOpen()) open();
        mDb.update(TABLE_HISTORY, contentValues, HISTORY_EPISODE + " = " + episodeId + " AND " + HISTORY_TYPE + " = " + status, null);
    }

    public void deleteHistoryEpisodeStatus(long episodeId, int status)
    {
        if (!mDb.isOpen()) open();
        mDb.delete(TABLE_HISTORY, HISTORY_EPISODE + " = " + episodeId + " AND " + HISTORY_TYPE + " = " + status, null);
    }

    public Cursor getHistory()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_HISTORY, new String[] {HISTORY_ID, HISTORY_DATE, HISTORY_TYPE, HISTORY_SERIAL, HISTORY_EPISODE}, null, null, null, null, null);
    }

    public Cursor getHistoryLastActionEpisode(long serialId)
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_HISTORY + " h INNER JOIN " + TABLE_EPISODES + " e ON h." + HISTORY_EPISODE + " = e." + EPISODES_ID, new String[] {EPISODES_SEASON, EPISODES_NUMBER}, "h." + HISTORY_SERIAL + " = " + serialId + " AND " + HISTORY_EPISODE + " != 0 ORDER BY " + HISTORY_DATE + " DESC LIMIT 1", null, null, null, null);
    }

    public void clearHistory()
    {
        if (!mDb.isOpen()) open();
        mDb.delete(TABLE_HISTORY, null, null);
    }

    public Cursor getReleasers()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_RELEASERS, new String[] {RELEASERS_ID, RELEASERS_NAME, RELEASERS_LOGO}, null, null, null, null, null);
    }

    public Cursor getTranslators()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_TRANSLATORS, new String[] {TRANSLATORS_ID, TRANSLATORS_NAME, TRANSLATORS_LOGO}, null, null, null, null, null);
    }






    public long getStatisticsSerials()
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT COUNT(*) FROM " + TABLE_SERIALS);
        return (int) s.simpleQueryForLong();
    }

    public long getStatisticsEpisodes()
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT COUNT(*) FROM " + TABLE_EPISODES);
        return (int) s.simpleQueryForLong();
    }

    public long getStatisticsHistory()
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT COUNT(*) FROM " + TABLE_HISTORY);
        return (int) s.simpleQueryForLong();
    }

}
