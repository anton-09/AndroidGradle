package ru.home.yoga;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import ru.home.yoga.entity.YogaItem;

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
    private static final String YOGA_FIO = "fio";
    private static final String YOGA_PAYTYPE = "pay_type";


    public static final String TABLE_TYPE = "type";
    public static final String TYPE_ID = "_id";
    public static final String TYPE_NAME = "typename";

    public static final String TABLE_DURATION = "duration";
    public static final String DURATION_ID = "_id";
    public static final String DURATION_DURATION = "value";

    public static final String TABLE_STUDIO = "studio";
    public static final String STUDIO_ID = "_id";
    public static final String STUDIO_NAME = "name";
    public static final String STUDIO_GROUP = "is_group";

    private static final String CREATE_YOGA = "CREATE TABLE " + TABLE_YOGA + " ("
            + YOGA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + YOGA_DATE + " INTEGER, "
            + YOGA_PRICE + " INTEGER, "
            + YOGA_PAYTYPE + " TEXT, "
            + YOGA_PEOPLE + " INTEGER, "
            + YOGA_TYPE + " INTEGER, "
            + YOGA_DURATION + " INTEGER, "
            + YOGA_STUDIO + " INTEGER, "
            + YOGA_FIO + " TEXT); ";

    public static final String CREATE_TYPE = "CREATE TABLE " + TABLE_TYPE + " ("
            + TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TYPE_NAME + " TEXT); ";

    public static final String CREATE_DURATION = "CREATE TABLE " + TABLE_DURATION + " ("
            + DURATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DURATION_DURATION + " DOUBLE); ";

    public static final String CREATE_STUDIO = "CREATE TABLE " + TABLE_STUDIO + " ("
            + STUDIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STUDIO_NAME + " TEXT, "
            + STUDIO_GROUP + " INTEGER); ";

    private SQLiteDatabase mDb;

    public YogaDbAdapter()
    {
    }

    public void open() throws SQLException
    {
        DatabaseHelper mDbHelper = new DatabaseHelper();
        mDb = mDbHelper.getWritableDatabase();
    }


    public Cursor getBackupData()
    {
        return getPagedData(LocalDate.now().toString(MyApplication.mDbDateFormat), 0, -1);
    }

    public Cursor getPagedData(String prevDate, long prevId, int pageSize)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(
                TABLE_YOGA + ", " + TABLE_TYPE + ", " + TABLE_DURATION + ", " + TABLE_STUDIO,
                new String[]{TABLE_YOGA + "." + YOGA_ID, YOGA_DATE, YOGA_PRICE, YOGA_PAYTYPE, YOGA_PEOPLE, YOGA_TYPE, TYPE_NAME, YOGA_DURATION, DURATION_DURATION, YOGA_STUDIO, STUDIO_NAME, STUDIO_GROUP, YOGA_FIO},
                TABLE_TYPE + "." + TYPE_ID + " = " + YOGA_TYPE + " AND " + TABLE_DURATION + "." + DURATION_ID + " = " + YOGA_DURATION + " AND " + TABLE_STUDIO + "." + STUDIO_ID + " = " + YOGA_STUDIO + " AND (" + YOGA_DATE + " < '" + prevDate + "'OR " + YOGA_DATE + " = '" + prevDate + "'AND " + TABLE_YOGA + "." + YOGA_ID + " > " + prevId + " )",
                null,
                null,
                null,
                YOGA_DATE + " DESC, " + TABLE_YOGA + "." + YOGA_ID + " ASC LIMIT " + pageSize
        );
    }

    public Cursor getPagedDataByStudioId(String prevDate, long prevId, int studioId, int pageSize)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(
                TABLE_YOGA + ", " + TABLE_TYPE + ", " + TABLE_DURATION + ", " + TABLE_STUDIO,
                new String[]{TABLE_YOGA + "." + YOGA_ID, YOGA_DATE, YOGA_PRICE, YOGA_PAYTYPE, YOGA_PEOPLE, YOGA_TYPE, TYPE_NAME, YOGA_DURATION, DURATION_DURATION, YOGA_STUDIO, STUDIO_NAME, STUDIO_GROUP, YOGA_FIO},
                TABLE_TYPE + "." + TYPE_ID + " = " + YOGA_TYPE + " AND " + TABLE_DURATION + "." + DURATION_ID + " = " + YOGA_DURATION + " AND " + TABLE_STUDIO + "." + STUDIO_ID + " = " + YOGA_STUDIO + " AND " + TABLE_YOGA + "." + YOGA_STUDIO + " = " + studioId + " AND (" + YOGA_DATE + " < '" + prevDate + "'OR " + YOGA_DATE + " = '" + prevDate + "'AND " + TABLE_YOGA + "." + YOGA_ID + " > " + prevId + " )",
                null,
                null,
                null,
                YOGA_DATE + " DESC, " + TABLE_YOGA + "." + YOGA_ID + " ASC LIMIT " + pageSize
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
                new String[]{TABLE_YOGA + "." + YOGA_ID, YOGA_DATE, YOGA_PRICE, YOGA_PAYTYPE, YOGA_PEOPLE, YOGA_TYPE, TYPE_NAME, YOGA_DURATION, DURATION_DURATION, YOGA_STUDIO, STUDIO_NAME, STUDIO_GROUP, YOGA_FIO},
                TABLE_TYPE + "." + TYPE_ID + " = " + YOGA_TYPE + " AND " + TABLE_DURATION + "." + DURATION_ID + " = " + YOGA_DURATION + " AND " + TABLE_STUDIO + "." + STUDIO_ID + " = " + YOGA_STUDIO + " AND " + TABLE_YOGA + "." + YOGA_ID + " = " + id,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getSummary()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(
                TABLE_YOGA + ", " + TABLE_STUDIO,
                new String[]{"SUBSTR(" + YOGA_DATE + ", 1, 6)", STUDIO_NAME, "COUNT(" + YOGA_PRICE + ")", "SUM(" + YOGA_PRICE + ")"},
                TABLE_STUDIO + "." + STUDIO_ID + " = " + YOGA_STUDIO,
                null,
                "SUBSTR(" + YOGA_DATE + ", 1, 6), " + STUDIO_NAME,
                null,
                null);
    }


    public void addData(String date, int price, String payType, int people, int type, int duration, int studio, String fio)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(YOGA_DATE, date);
        contentValues.put(YOGA_PRICE, price);
        contentValues.put(YOGA_PAYTYPE, payType);
        contentValues.put(YOGA_PEOPLE, people);
        contentValues.put(YOGA_TYPE, type);
        contentValues.put(YOGA_DURATION, duration);
        contentValues.put(YOGA_STUDIO, studio);
        contentValues.put(YOGA_FIO, fio);
        mDb.insert(TABLE_YOGA, null, contentValues);
    }

    public void addBulkData(ArrayList<YogaItem> items)
    {
        String sql = "INSERT INTO " + TABLE_YOGA + " (" + YOGA_DATE + ", " + YOGA_PRICE + ", " + YOGA_PAYTYPE + ", " + YOGA_PEOPLE + ", " + YOGA_TYPE + ", " + YOGA_DURATION + ", " + YOGA_STUDIO + ", " + YOGA_FIO + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        mDb.beginTransaction();

        SQLiteStatement stmt = mDb.compileStatement(sql);
        for (int i = 0; i < items.size(); i++)
        {
            stmt.bindString(1, items.get(i).getDate());
            stmt.bindLong(2, items.get(i).getPrice());
            stmt.bindString(3, items.get(i).getPayType());
            stmt.bindLong(4, items.get(i).getPeople());
            stmt.bindLong(5, items.get(i).getType().getId());
            stmt.bindLong(6, items.get(i).getDuration().getId());
            stmt.bindLong(7, items.get(i).getStudio().getId());
            stmt.bindString(8, items.get(i).getFIO());
            stmt.execute();
            stmt.clearBindings();
        }

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }


    public void updateData(long id, String date, int price, String payType, int people, int type, int duration, int studio, String fio)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(YOGA_DATE, date);
        contentValues.put(YOGA_PRICE, price);
        contentValues.put(YOGA_PAYTYPE, payType);
        contentValues.put(YOGA_PEOPLE, people);
        contentValues.put(YOGA_TYPE, type);
        contentValues.put(YOGA_DURATION, duration);
        contentValues.put(YOGA_STUDIO, studio);
        contentValues.put(YOGA_FIO, fio);
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

    public void addStudio(String studio, int group)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STUDIO_NAME, studio);
        contentValues.put(STUDIO_GROUP, group);
        mDb.insert(TABLE_STUDIO, null, contentValues);
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
        return mDb.query(TABLE_STUDIO, new String[]{STUDIO_ID, STUDIO_NAME, STUDIO_GROUP}, null, null, null, null, null);
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

