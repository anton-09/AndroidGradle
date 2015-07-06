package ru.home.fitness;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FitnessDbAdapter
{
    private static final String TABLE_FITNESS = "fitness";
    private static final String FITNESS_ID = "_id";
    private static final String FITNESS_DATE = "date";
    private static final String FITNESS_COMMENTS = "comments";
    private static final String DATABASE_NAME = "fitness_db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_FITNESS = "CREATE TABLE " + TABLE_FITNESS + " ("
            + FITNESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FITNESS_DATE + " INTEGER, "
            + FITNESS_COMMENTS + " TEXT);";

    private SQLiteDatabase mDb;

    public FitnessDbAdapter()
    {
    }

    public void open() throws SQLException
    {
        DatabaseHelper mDbHelper = new DatabaseHelper();
        mDb = mDbHelper.getWritableDatabase();
    }

//    public Cursor getData()
//    {
//        if (!mDb.isOpen())
//        {
//            open();
//        }
//        return mDb.query(TABLE_FITNESS, new String[]{FITNESS_ID, FITNESS_DATE, FITNESS_WEIGHT, FITNESS_EAT, FITNESS_FEED, FITNESS_COMMENTS}, null, null, null, null, FITNESS_DATE + " DESC");
//    }


//    public Cursor getDataById(long id)
//    {
//        if (!mDb.isOpen())
//        {
//            open();
//        }
//        return mDb.query(TABLE_FITNESS, new String[]{FITNESS_ID, FITNESS_DATE, FITNESS_WEIGHT, FITNESS_EAT, FITNESS_FEED, FITNESS_COMMENTS}, FITNESS_ID + " = " + id, null, null, null, null);
//    }

//    public Cursor getDataByDate(String date)
//    {
//        if (!mDb.isOpen())
//        {
//            open();
//        }
//        return mDb.query(TABLE_FITNESS, new String[]{FITNESS_ID, FITNESS_DATE, FITNESS_WEIGHT, FITNESS_EAT, FITNESS_FEED, FITNESS_COMMENTS}, FITNESS_DATE + " = " + date, null, null, null, null);
//    }
//
//    public void addData(String date, int weight, String eat, String feed, String comments)
//    {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(FITNESS_DATE, date);
//        contentValues.put(FITNESS_WEIGHT, weight);
//        contentValues.put(FITNESS_EAT, eat);
//        contentValues.put(FITNESS_FEED, feed);
//        contentValues.put(FITNESS_COMMENTS, comments);
//        mDb.insert(TABLE_FITNESS, null, contentValues);
//    }
//
//    public void updateData(long id, String date, int weight, String eat, String feed, String comments)
//    {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(FITNESS_DATE, date);
//        contentValues.put(FITNESS_WEIGHT, weight);
//        contentValues.put(FITNESS_EAT, eat);
//        contentValues.put(FITNESS_FEED, feed);
//        contentValues.put(FITNESS_COMMENTS, comments);
//        mDb.update(TABLE_FITNESS, contentValues, FITNESS_ID + " = " + id, null);
//    }
//
//    public void deleteData(long id)
//    {
//        mDb.delete(TABLE_FITNESS, FITNESS_ID + " = " + id, null);
//    }
//
//    public void deleteAllData()
//    {
//        mDb.delete(TABLE_FITNESS, null, null);
//    }

    private class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper()
        {
            super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_FITNESS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

    }
}

