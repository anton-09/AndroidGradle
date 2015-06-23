package ru.home.housing;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HousingDbAdapter
{
    private static final String DATABASE_NAME = "housing_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HOUSING = "housing";

    public static final String HOUSING_ID = "_id";
    public static final String HOUSING_DATE = "date";
    public static final String HOUSING_COLD  = "cold";
    public static final String HOUSING_HOT  = "hot";
    public static final String HOUSING_ELECTRICITY  = "electricity";

    private static final String CREATE_HOUSING = "CREATE TABLE " + TABLE_HOUSING + " ("
            + HOUSING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HOUSING_DATE + " INTEGER, "
            + HOUSING_COLD + " INTEGER, "
            + HOUSING_HOT + " INTEGER, "
            + HOUSING_ELECTRICITY + " INTEGER);";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper()
        {
            super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_HOUSING);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

    }

    public HousingDbAdapter()
    {
    }

    public HousingDbAdapter open() throws SQLException
    {
        mDbHelper = new DatabaseHelper();
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public Cursor getData()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_HOUSING, new String[] { HOUSING_ID, HOUSING_DATE, HOUSING_COLD, HOUSING_HOT, HOUSING_ELECTRICITY }, null, null, null, null, HOUSING_DATE + " ASC");
    }

    public Cursor getLastData()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_HOUSING, new String[] { HOUSING_ID, HOUSING_DATE, HOUSING_COLD, HOUSING_HOT, HOUSING_ELECTRICITY }, HOUSING_DATE + " = (SELECT MAX(" + HOUSING_DATE + ") FROM " + TABLE_HOUSING + ")", null, null, null, null);
    }


    public void addData(long date, int cold, int hot, int electricity)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOUSING_DATE, date);
        contentValues.put(HOUSING_COLD, cold);
        contentValues.put(HOUSING_HOT, hot);
        contentValues.put(HOUSING_ELECTRICITY, electricity);
        mDb.insert(TABLE_HOUSING, null, contentValues);
    }

    public void deleteData(long id)
    {
        mDb.delete(TABLE_HOUSING, HOUSING_ID + " = " + id, null);
    }
}

