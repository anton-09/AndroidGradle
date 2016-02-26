package ru.home.yoga;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class YogaDbAdapter
{
    private static final String TABLE_YOGA = "yoga";
    private static final String YOGA_ID = "_id";
    private static final String YOGA_DATE = "date";
    private static final String YOGA_WEIGHT = "weight";
    private static final String YOGA_EAT = "eat";
    private static final String YOGA_FEED = "feed";
    private static final String YOGA_COMMENTS = "comments";
    private static final String DATABASE_NAME = "yoga_db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_YOGA = "CREATE TABLE " + TABLE_YOGA + " ("
            + YOGA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + YOGA_DATE + " INTEGER, "
            + YOGA_WEIGHT + " INTEGER, "
            + YOGA_EAT + " TEXT, "
            + YOGA_FEED + " TEXT, "
            + YOGA_COMMENTS + " TEXT);";

    private SQLiteDatabase mDb;

    public YogaDbAdapter()
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
        return mDb.query(TABLE_YOGA, new String[]{YOGA_ID, YOGA_DATE, YOGA_WEIGHT, YOGA_EAT, YOGA_FEED, YOGA_COMMENTS}, null, null, null, null, YOGA_DATE + " DESC");
    }

    public Cursor getLastData()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_YOGA, new String[]{YOGA_ID, YOGA_DATE, YOGA_WEIGHT, YOGA_EAT, YOGA_FEED, YOGA_COMMENTS}, YOGA_DATE + " = (SELECT MAX(" + YOGA_DATE + ") FROM " + TABLE_YOGA + ")", null, null, null, null);
    }

    public Cursor getDataById(long id)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_YOGA, new String[]{YOGA_ID, YOGA_DATE, YOGA_WEIGHT, YOGA_EAT, YOGA_FEED, YOGA_COMMENTS}, YOGA_ID + " = " + id, null, null, null, null);
    }

    public Cursor getDataByDate(String date)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_YOGA, new String[]{YOGA_ID, YOGA_DATE, YOGA_WEIGHT, YOGA_EAT, YOGA_FEED, YOGA_COMMENTS}, YOGA_DATE + " = " + date, null, null, null, null);
    }

    public void addData(String date, int weight, String eat, String feed, String comments)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(YOGA_DATE, date);
        contentValues.put(YOGA_WEIGHT, weight);
        contentValues.put(YOGA_EAT, eat);
        contentValues.put(YOGA_FEED, feed);
        contentValues.put(YOGA_COMMENTS, comments);
        mDb.insert(TABLE_YOGA, null, contentValues);
    }

    public void updateData(long id, String date, int weight, String eat, String feed, String comments)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(YOGA_DATE, date);
        contentValues.put(YOGA_WEIGHT, weight);
        contentValues.put(YOGA_EAT, eat);
        contentValues.put(YOGA_FEED, feed);
        contentValues.put(YOGA_COMMENTS, comments);
        mDb.update(TABLE_YOGA, contentValues, YOGA_ID + " = " + id, null);
    }

    public void deleteData(long id)
    {
        mDb.delete(TABLE_YOGA, YOGA_ID + " = " + id, null);
    }

    public void deleteAllData()
    {
        mDb.delete(TABLE_YOGA, null, null);
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
            db.execSQL(CREATE_YOGA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

    }
}

