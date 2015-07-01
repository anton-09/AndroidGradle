package ru.home.babylog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BabyLogDbAdapter
{
    private static final String TABLE_BABYLOG = "babylog";
    private static final String BABYLOG_ID = "_id";
    private static final String BABYLOG_DATE = "date";
    private static final String BABYLOG_WEIGHT = "weight";
    private static final String BABYLOG_EAT = "eat";
    private static final String BABYLOG_FEED = "feed";
    private static final String BABYLOG_COMMENTS = "comments";
    private static final String DATABASE_NAME = "babylog_db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_BABYLOG = "CREATE TABLE " + TABLE_BABYLOG + " ("
            + BABYLOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BABYLOG_DATE + " INTEGER, "
            + BABYLOG_WEIGHT + " INTEGER, "
            + BABYLOG_EAT + " TEXT, "
            + BABYLOG_FEED + " TEXT, "
            + BABYLOG_COMMENTS + " TEXT);";

    private SQLiteDatabase mDb;

    public BabyLogDbAdapter()
    {
    }

    public void open() throws SQLException
    {
        DatabaseHelper mDbHelper = new DatabaseHelper();
        mDb = mDbHelper.getWritableDatabase();
    }

    public Cursor getData()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_BABYLOG, new String[]{BABYLOG_ID, BABYLOG_DATE, BABYLOG_WEIGHT, BABYLOG_EAT, BABYLOG_FEED, BABYLOG_COMMENTS}, null, null, null, null, BABYLOG_DATE + " DESC");
    }

    public Cursor getLastData()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_BABYLOG, new String[]{BABYLOG_ID, BABYLOG_DATE, BABYLOG_WEIGHT, BABYLOG_EAT, BABYLOG_FEED, BABYLOG_COMMENTS}, BABYLOG_DATE + " = (SELECT MAX(" + BABYLOG_DATE + ") FROM " + TABLE_BABYLOG + ")", null, null, null, null);
    }

    public Cursor getDataById(long id)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_BABYLOG, new String[]{BABYLOG_ID, BABYLOG_DATE, BABYLOG_WEIGHT, BABYLOG_EAT, BABYLOG_FEED, BABYLOG_COMMENTS}, BABYLOG_ID + " = " + id, null, null, null, null);
    }

    public Cursor getDataByDate(String date)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_BABYLOG, new String[]{BABYLOG_ID, BABYLOG_DATE, BABYLOG_WEIGHT, BABYLOG_EAT, BABYLOG_FEED, BABYLOG_COMMENTS}, BABYLOG_DATE + " = " + date, null, null, null, null);
    }

    public void addData(String date, int weight, String eat, String feed, String comments)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BABYLOG_DATE, date);
        contentValues.put(BABYLOG_WEIGHT, weight);
        contentValues.put(BABYLOG_EAT, eat);
        contentValues.put(BABYLOG_FEED, feed);
        contentValues.put(BABYLOG_COMMENTS, comments);
        mDb.insert(TABLE_BABYLOG, null, contentValues);
    }

    public void updateData(long id, String date, int weight, String eat, String feed, String comments)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BABYLOG_DATE, date);
        contentValues.put(BABYLOG_WEIGHT, weight);
        contentValues.put(BABYLOG_EAT, eat);
        contentValues.put(BABYLOG_FEED, feed);
        contentValues.put(BABYLOG_COMMENTS, comments);
        mDb.update(TABLE_BABYLOG, contentValues, BABYLOG_ID + " = " + id, null);
    }

    public void deleteData(long id)
    {
        mDb.delete(TABLE_BABYLOG, BABYLOG_ID + " = " + id, null);
    }

    public void deleteAllData()
    {
        mDb.delete(TABLE_BABYLOG, null, null);
    }

    private class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper()
        {
            super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_BABYLOG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

    }
}

