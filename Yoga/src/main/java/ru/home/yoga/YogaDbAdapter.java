package ru.home.yoga;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class YogaDbAdapter
{
    private static final String DATABASE_NAME = "yoga_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SEQUENCE = "sqlite_sequence";

    private static final String TABLE_YOGA = "yoga";
    private static final String YOGA_ID = "_id";
    private static final String YOGA_DATE = "date";
    private static final String YOGA_PRICE = "price";
    private static final String YOGA_PEOPLE = "people";
    private static final String YOGA_TYPE = "type_id";
    private static final String YOGA_DURATION = "duration_id";
    private static final String YOGA_STUDIO = "studio_id";

    public static final String TABLE_TYPE = "type";
    public static final String TYPE_ID = "_id";
    public static final String TYPE_NAME = "typename";

    public static final String TABLE_DURATION = "duration";
    public static final String DURATION_ID = "_id";
    public static final String DURATION_DURATION = "value";

    public static final String TABLE_STUDIO = "studio";
    public static final String STUDIO_ID = "_id";
    public static final String STUDIO_NAME = "name";

    private static final String CREATE_YOGA = "CREATE TABLE " + TABLE_YOGA + " ("
            + YOGA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + YOGA_DATE + " INTEGER, "
            + YOGA_PRICE + " INTEGER, "
            + YOGA_PEOPLE + " INTEGER, "
            + YOGA_TYPE + " INTEGER, "
            + YOGA_DURATION + " INTEGER, "
            + YOGA_STUDIO + " INTEGER); ";

    public static final String CREATE_TYPE = "CREATE TABLE " + TABLE_TYPE + " ("
            + TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TYPE_NAME + " TEXT); ";

    public static final String CREATE_DURATION = "CREATE TABLE " + TABLE_DURATION + " ("
            + DURATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DURATION_DURATION + " DOUBLE); ";

    public static final String CREATE_STUDIO = "CREATE TABLE " + TABLE_STUDIO + " ("
            + STUDIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STUDIO_NAME + " TEXT); ";

    private SQLiteDatabase mDb;

    public YogaDbAdapter()
    {
    }

    public void open() throws SQLException
    {
        DatabaseHelper mDbHelper = new DatabaseHelper();
        mDb = mDbHelper.getWritableDatabase();
    }

    public Cursor getTypes()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_TYPE, new String[]{TYPE_ID, TYPE_NAME}, null, null, null, null, null);
    }

    public Cursor getDurations()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_DURATION, new String[]{DURATION_ID, DURATION_DURATION}, null, null, null, null, null);
    }

    public Cursor getStudios()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_STUDIO, new String[]{STUDIO_ID, STUDIO_NAME}, null, null, null, null, null);
    }


    public Cursor getData()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(
                TABLE_YOGA + ", " + TABLE_TYPE + ", " + TABLE_DURATION + ", " + TABLE_STUDIO,
                new String[]{TABLE_YOGA + "." + YOGA_ID, YOGA_DATE, YOGA_PRICE, YOGA_PEOPLE, YOGA_TYPE, TYPE_NAME, YOGA_DURATION, DURATION_DURATION, YOGA_STUDIO, STUDIO_NAME},
                TABLE_TYPE + "." + TYPE_ID + " = " + YOGA_TYPE + " AND " + TABLE_DURATION + "." + DURATION_ID + " = " + YOGA_DURATION + " AND " + TABLE_STUDIO + "." + STUDIO_ID + " = " + YOGA_STUDIO,
                null,
                null,
                null,
                YOGA_DATE + " DESC"
        );
    }


    public Cursor getDataById(long id)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(
                TABLE_YOGA + ", " + TABLE_TYPE + ", " + TABLE_DURATION + ", " + TABLE_STUDIO,
                new String[]{TABLE_YOGA + "." + YOGA_ID, YOGA_DATE, YOGA_PRICE, YOGA_PEOPLE, YOGA_TYPE, TYPE_NAME, YOGA_DURATION, DURATION_DURATION, YOGA_STUDIO, STUDIO_NAME},
                TABLE_TYPE + "." + TYPE_ID + " = " + YOGA_TYPE + " AND " + TABLE_DURATION + "." + DURATION_ID + " = " + YOGA_DURATION + " AND " + TABLE_STUDIO + "." + STUDIO_ID + " = " + YOGA_STUDIO + " AND " + TABLE_YOGA + "." + YOGA_ID + " = " + id,
                null,
                null,
                null,
                null
        );
    }


    public Cursor getDataByStudioId(long id)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(
                TABLE_YOGA + ", " + TABLE_TYPE + ", " + TABLE_DURATION + ", " + TABLE_STUDIO,
                new String[]{TABLE_YOGA + "." + YOGA_ID, YOGA_DATE, YOGA_PRICE, YOGA_PEOPLE, YOGA_TYPE, TYPE_NAME, YOGA_DURATION, DURATION_DURATION, YOGA_STUDIO, STUDIO_NAME},
                TABLE_TYPE + "." + TYPE_ID + " = " + YOGA_TYPE + " AND " + TABLE_DURATION + "." + DURATION_ID + " = " + YOGA_DURATION + " AND " + TABLE_STUDIO + "." + STUDIO_ID + " = " + YOGA_STUDIO + " AND " + TABLE_YOGA + "." + YOGA_STUDIO + " = " + id,
                null,
                null,
                null,
                YOGA_DATE + " DESC"
        );
    }


    public void addData(String date, int price, int people, int type, int duration, int studio)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(YOGA_DATE, date);
        contentValues.put(YOGA_PRICE, price);
        contentValues.put(YOGA_PEOPLE, people);
        contentValues.put(YOGA_TYPE, type);
        contentValues.put(YOGA_DURATION, duration);
        contentValues.put(YOGA_STUDIO, studio);
        mDb.insert(TABLE_YOGA, null, contentValues);
    }

    public void updateData(long id, String date, int price, int people, int type, int duration, int studio)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(YOGA_DATE, date);
        contentValues.put(YOGA_PRICE, price);
        contentValues.put(YOGA_PEOPLE, people);
        contentValues.put(YOGA_TYPE, type);
        contentValues.put(YOGA_DURATION, duration);
        contentValues.put(YOGA_STUDIO, studio);
        mDb.update(TABLE_YOGA, contentValues, YOGA_ID + " = " + id, null);
    }

    public void deleteData(long id)
    {
        mDb.delete(TABLE_YOGA, YOGA_ID + " = " + id, null);
    }

    public void deleteAllData()
    {
        mDb.delete(TABLE_YOGA, null, null);
        mDb.delete(TABLE_TYPE, null, null);
        mDb.delete(TABLE_DURATION, null, null);
        mDb.delete(TABLE_STUDIO, null, null);
        mDb.delete(TABLE_SEQUENCE, null, null);
    }


    public void addType(String type)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TYPE_NAME, type);
        mDb.insert(TABLE_TYPE, null, contentValues);
    }

    public void addDuration(Double duration)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DURATION_DURATION, duration);
        mDb.insert(TABLE_DURATION, null, contentValues);
    }

    public void addStudio(String studio)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STUDIO_NAME, studio);
        mDb.insert(TABLE_STUDIO, null, contentValues);
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
            db.execSQL(CREATE_TYPE);
            db.execSQL(CREATE_DURATION);
            db.execSQL(CREATE_STUDIO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

    }
}

