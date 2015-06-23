package ru.home.vertel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class VertelDbAdapter
{
    private static final String DATABASE_NAME = "vertel_db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_VERTEL = "vertel";
    private static final String TABLE_SETTINGS = "settings";

    public static final String VERTEL_ID = "_id";
    public static final String VERTEL_ITEMS = "items";
    public static final String VERTEL_RUNS = "runs";
    public static final String VERTEL_WINS = "wins";

    public static final String SETTINGS_ID = "_id";
    public static final String SETTINGS_KEY = "key";
    public static final String SETTINGS_VALUE = "value";

    private static final String CREATE_VERTEL = "CREATE TABLE " + TABLE_VERTEL + " ("
                        + VERTEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + VERTEL_ITEMS + " INTEGER, "
                        + VERTEL_RUNS + " INTEGER, "
                        + VERTEL_WINS + " INTEGER);";

    private static final String CREATE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS + " ("
            + SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SETTINGS_KEY + " TEXT, "
            + SETTINGS_VALUE + " TEXT);";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper()
        {
            super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_VERTEL);
            db.execSQL(CREATE_SETTINGS);

            ContentValues contentValues = new ContentValues();
            for (int i=1;i<31;i++)
            {
                contentValues.clear();
                contentValues.put(VERTEL_ITEMS, i);
                contentValues.put(VERTEL_RUNS, 0);
                contentValues.put(VERTEL_WINS, 0);
                db.insert(TABLE_VERTEL, null, contentValues);
            }

            contentValues.clear();
            contentValues.put(SETTINGS_KEY, "skin_path");
            contentValues.put(SETTINGS_VALUE, "/AHTOH/Vertel");
            db.insert(TABLE_SETTINGS, null, contentValues);

            contentValues.clear();
            contentValues.put(SETTINGS_KEY, "crop_factor");
            contentValues.put(SETTINGS_VALUE, "6");
            db.insert(TABLE_SETTINGS, null, contentValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

    }

    public VertelDbAdapter()
    {
    }

    public VertelDbAdapter open() throws SQLException
    {
        mDbHelper = new DatabaseHelper();
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }


    public void win(Integer items)
    {
        String query =  "UPDATE " + TABLE_VERTEL +
                        " SET " + VERTEL_RUNS + " = " + VERTEL_RUNS + "+1, " + VERTEL_WINS + " = " + VERTEL_WINS + "+1 " +
                        " WHERE " + VERTEL_ITEMS + " = " + items;

        if (!mDb.isOpen()) open();
        mDb.execSQL(query);
    }

    public void lose(Integer items)
    {
        String query =  "UPDATE " + TABLE_VERTEL +
                        " SET " + VERTEL_RUNS + " = " + VERTEL_RUNS + "+1 " +
                        " WHERE " + VERTEL_ITEMS + " = " + items;

        if (!mDb.isOpen()) open();
        mDb.execSQL(query);
    }

    public void reset()
    {
        String query =  "UPDATE " + TABLE_VERTEL +
                        " SET " + VERTEL_RUNS + " = 0, " + VERTEL_WINS + " = 0 ";

        if (!mDb.isOpen()) open();
        mDb.execSQL(query);
    }

    public void reset(Integer items)
    {
        String query =  "UPDATE " + TABLE_VERTEL +
                " SET " + VERTEL_RUNS + " = 0, " + VERTEL_WINS + " = 0 " +
                " WHERE " + VERTEL_ITEMS + " = " + items;

        if (!mDb.isOpen()) open();
        mDb.execSQL(query);
    }

    public Cursor getData()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_VERTEL, new String[] { VERTEL_ITEMS, VERTEL_RUNS, VERTEL_WINS }, null, null, null, null, null);
    }

    public Cursor getData(Integer items)
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_VERTEL, new String[] { VERTEL_RUNS, VERTEL_WINS }, VERTEL_ITEMS + " = " + items, null, null, null, null);
    }

    public String getSkinPath()
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT " + SETTINGS_VALUE + " FROM " + TABLE_SETTINGS + " WHERE " + SETTINGS_KEY + " = 'skin_path'");
        return s.simpleQueryForString();
    }
    
    public void updateSkinPath(String skinPath)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_VALUE, skinPath);

        if (!mDb.isOpen()) open();
        mDb.update(TABLE_SETTINGS, contentValues, SETTINGS_KEY + " = 'skin_path'", null);
    }

    public int getCropFactor()
    {
        if (!mDb.isOpen()) open();
        SQLiteStatement s = mDb.compileStatement("SELECT " + SETTINGS_VALUE + " FROM " + TABLE_SETTINGS + " WHERE " + SETTINGS_KEY + " = 'crop_factor'");
        return (int) s.simpleQueryForLong();
    }

    public void updateCropFactor(int cropFactor)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_VALUE, cropFactor);

        if (!mDb.isOpen()) open();
        mDb.update(TABLE_SETTINGS, contentValues, SETTINGS_KEY + " = 'crop_factor'", null);
    }
}
