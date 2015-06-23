package ru.home.pw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.*;

public class DailyDbAdapter {

    private static final String DATABASE_NAME = "pw_db";
    private static final int DATABASE_VERSION = 3;
    
    private static final String TABLE_CHARS = "chars";
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_CHARS_EVENTS = "chars_events";
    private static final String TABLE_STORAGE = "storage";

    private static final String CHARS_ID = "_id";
    private static final String CHARS_NAME = "name";
    private static final String CHARS_PICTURE = "picture";
    private static final String CHARS_LEVEL = "level";

    private static final String EVENTS_ID = "_id";
    private static final String EVENTS_NAME = "name";

    private static final String CHARS_EVENTS_ID = "_id";
    private static final String CHARS_EVENTS_CHAR_ID = "char_id";
    private static final String CHARS_EVENTS_EVENT_ID = "event_id";
    private static final String CHARS_EVENTS_USED = "used";

    private static final String STORAGE_ID = "_id";
    private static final String STORAGE_CHAR_EVENT_ID = "char_event_id";
    private static final String STORAGE_STATUS = "status";
    private static final String STORAGE_DATE = "date";

//    private static final String CREATE_CHARS = "CREATE TABLE " + TABLE_CHARS + " ("
//                        + CHARS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                        + CHARS_PICTURE + " INTEGER, "
//                        + CHARS_LEVEL + " INTEGER, "
//                        + CHARS_NAME + " TEXT NOT NULL);";
//    private static final String CREATE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + " ("
//                        + EVENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                        + EVENTS_NAME + " TEXT NOT NULL);";
//    private static final String CREATE_CHARS_EVENTS = "CREATE TABLE " + TABLE_CHARS_EVENTS + " ("
//                        + CHARS_EVENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                        + CHARS_EVENTS_CHAR_ID + " INTEGER, "
//                        + CHARS_EVENTS_EVENT_ID + " INTEGER, "
//                        + CHARS_EVENTS_USED + " INTEGER);";
//    private static final String CREATE_STORAGE = "CREATE TABLE " + TABLE_STORAGE + " ("
//                        + STORAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                        + STORAGE_CHAR_EVENT_ID + " INTEGER, "
//                        + STORAGE_DATE + " INTEGER, "
//                        + STORAGE_STATUS + " INTEGER);";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;


    private static class DatabaseHelper extends SQLiteOpenHelper {

        String mDbPath;
        Context mContext;

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mDbPath = "/data/data/" + context.getPackageName() + "/databases/";
            mContext = context;
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
                    InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
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

    public DailyDbAdapter(Context ctx)
    {
        this.mCtx = ctx;
    }

    public DailyDbAdapter open() throws SQLException
    {
        mDbHelper = new DatabaseHelper(mCtx);
        try {
            mDbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }


    public long addRecord(Integer char_event_id, Integer status, long date)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STORAGE_CHAR_EVENT_ID, char_event_id);
        contentValues.put(STORAGE_STATUS, status);
        contentValues.put(STORAGE_DATE, date);
        if (!mDb.isOpen()) open();
        return mDb.insert(TABLE_STORAGE, null, contentValues);
    }

//    public long getCountByDateAndMapping(long date, Integer char_event_id)
//    {
//        SQLiteStatement s = mDb.compileStatement("SELECT COUNT(*) FROM " + TABLE_STORAGE + " WHERE " + STORAGE_DATE + " = " + date + " AND " + STORAGE_CHAR_EVENT_ID + " = " + char_event_id);
//        return s.simpleQueryForLong();
//    }

//    public Cursor getUsedMappingCharsEvents()
//    {
//        return mDb.query(TABLE_CHARS_EVENTS, new String[] {CHARS_EVENTS_ID}, CHARS_EVENTS_USED + " = 1", null, null, null, null);
//    }
    
//    public Cursor getUsedMappingCharsEventsByNames(String char_name, String event_name)
//    {
//        String query =  "SELECT ce." + CHARS_EVENTS_ID + " FROM " + TABLE_CHARS_EVENTS + " ce " +
//                        " INNER JOIN " + TABLE_CHARS + " c ON ce." + CHARS_EVENTS_CHAR_ID + " = c." + CHARS_ID +
//                        " INNER JOIN " + TABLE_EVENTS + " e ON ce." + CHARS_EVENTS_EVENT_ID + " = e." + EVENTS_ID +
//                        " WHERE " + CHARS_EVENTS_USED + " = 1 " +
//                        "   AND c." + CHARS_NAME + " = ? "+
//                        "   AND e." + EVENTS_NAME + " = ?";
//        return mDb.rawQuery(query, new String[] {char_name, event_name});
//    }

//    public Cursor getStatusByDateAndNames(long date, String char_name, String event_name) throws SQLException
//    {
//        String query =  "SELECT " + STORAGE_STATUS + " FROM " + TABLE_STORAGE +
//                        " WHERE " + STORAGE_DATE + " = " + date +
//                        "   AND " + STORAGE_CHAR_EVENT_ID + " = " +
//                        "       (SELECT ce." + CHARS_EVENTS_ID + " FROM " + TABLE_CHARS_EVENTS + " ce " +
//                        "       INNER JOIN " + TABLE_CHARS + " c ON ce." + CHARS_EVENTS_CHAR_ID + " = c." + CHARS_ID +
//                        "       INNER JOIN " + TABLE_EVENTS + " e ON ce." + CHARS_EVENTS_EVENT_ID + " = mEvent." + EVENTS_ID +
//                        "       WHERE " + CHARS_EVENTS_USED + " = 1 " +
//                        "           AND c." + CHARS_NAME + " = ? "+
//                        "           AND e." + EVENTS_NAME + " = ?)";
//
//        return mDb.rawQuery(query, new String[] {char_name, event_name});
//    }
    
    public void updateRecord(long date, String char_name, String event_name, Integer status)
    {
        String query =  "UPDATE " + TABLE_STORAGE + " SET " + STORAGE_STATUS + " = " + status +
                        " WHERE " + STORAGE_DATE + " = " + date +
                        "   AND " + STORAGE_CHAR_EVENT_ID + " = " +
                        "       (SELECT ce." + CHARS_EVENTS_ID + " FROM " + TABLE_CHARS_EVENTS + " ce " +
                        "       INNER JOIN " + TABLE_CHARS + " c ON ce." + CHARS_EVENTS_CHAR_ID + " = c." + CHARS_ID +
                        "       INNER JOIN " + TABLE_EVENTS + " e ON ce." + CHARS_EVENTS_EVENT_ID + " = e." + EVENTS_ID +
                        "       WHERE " + CHARS_EVENTS_USED + " = 1 " +
                        "           AND c." + CHARS_NAME + " = ? "+
                        "           AND e." + EVENTS_NAME + " = ?)";
        if (!mDb.isOpen()) open();
        mDb.execSQL(query, new String[] {char_name, event_name});
    }

    public int updateUsedMappingById(Integer char_id, String event, int used)
    {
        if (!mDb.isOpen()) open();
        Cursor c = mDb.query(TABLE_EVENTS, new String[] {EVENTS_ID}, EVENTS_NAME + " = '" + event + "'", null, null, null, null);
        c.moveToFirst();
        int event_id = c.getInt(0);
        c.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHARS_EVENTS_USED, used);
        return mDb.update(TABLE_CHARS_EVENTS, contentValues, CHARS_EVENTS_CHAR_ID + " = " + char_id + " AND "+ CHARS_EVENTS_EVENT_ID + " = " + event_id, null);
    }

    public Cursor getChars()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_CHARS, new String[] {CHARS_ID, CHARS_NAME, CHARS_PICTURE, CHARS_LEVEL}, null, null, null, null, null);
    }

    public Cursor getEvents()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_EVENTS, new String[] {EVENTS_ID, EVENTS_NAME}, null, null, null, null, null);
    }

    public Cursor getCharsEvents()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_CHARS_EVENTS, new String[] {CHARS_EVENTS_ID, CHARS_EVENTS_CHAR_ID, CHARS_EVENTS_EVENT_ID, CHARS_EVENTS_USED}, null, null, null, null, null);
    }

    public Cursor getStorage(long date)
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_STORAGE, new String[] {STORAGE_ID, STORAGE_CHAR_EVENT_ID, STORAGE_DATE, STORAGE_STATUS}, STORAGE_DATE + " = " + date, null, null, null, null);
    }

    public Cursor getStorageUnfinished()
    {
        if (!mDb.isOpen()) open();
        return mDb.query(TABLE_STORAGE, new String[] {STORAGE_CHAR_EVENT_ID, STORAGE_DATE}, STORAGE_STATUS + " = 1", null, null, null, null);
    }

    public void updateLevelById(Integer char_id)
    {
        String query =  "UPDATE " + TABLE_CHARS + " SET " + CHARS_LEVEL + " = " + CHARS_LEVEL + " + 1" +
                " WHERE " + CHARS_ID + " = " + char_id;
        if (!mDb.isOpen()) open();
        mDb.execSQL(query);
    }
}
