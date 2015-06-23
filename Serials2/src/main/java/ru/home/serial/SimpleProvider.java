package ru.home.serial;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class SimpleProvider extends ContentProvider
{
    private static final int SERIALS = 11;
    private static final int SERIAL_BY_ID = 12;
    private static final int SERIAL_FILTER = 13;
    private static final int EPISODES = 21;
    private static final int EPISODE_BY_ID = 22;
    private static final int EPISODES_BY_SERIAL_SEASON = 23;
    private static final int EPISODE_BY_SERIAL_SEASON_NUMBER = 24;


    private static final HashMap<String, String> mProjectionMapSerials;
    private static final HashMap<String, String> mProjectionMapEpisodes;
    private static final UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(MainTable.AUTHORITY, MainTable.TABLE_SERIALS, SERIALS);
        mUriMatcher.addURI(MainTable.AUTHORITY, MainTable.TABLE_SERIALS + "/#", SERIAL_BY_ID);
        mUriMatcher.addURI(MainTable.AUTHORITY, MainTable.TABLE_SERIALS + "/filter/*", SERIAL_FILTER);
        mUriMatcher.addURI(MainTable.AUTHORITY, MainTable.TABLE_EPISODES, EPISODES);
        mUriMatcher.addURI(MainTable.AUTHORITY, MainTable.TABLE_EPISODES + "/#", EPISODE_BY_ID);
        mUriMatcher.addURI(MainTable.AUTHORITY, MainTable.TABLE_EPISODES + "/#/#", EPISODES_BY_SERIAL_SEASON);
        mUriMatcher.addURI(MainTable.AUTHORITY, MainTable.TABLE_EPISODES + "/#/#/#", EPISODE_BY_SERIAL_SEASON_NUMBER);

        mProjectionMapSerials = new HashMap<String, String>();
        mProjectionMapSerials.put(MainTable._ID, MainTable._ID);
        mProjectionMapSerials.put(MainTable.SERIALS_NAME, MainTable.SERIALS_NAME);
        mProjectionMapSerials.put(MainTable.SERIALS_SEASON_COUNT, MainTable.SERIALS_SEASON_COUNT);

        mProjectionMapEpisodes = new HashMap<String, String>();
        mProjectionMapEpisodes.put(MainTable._ID, MainTable._ID);
        mProjectionMapEpisodes.put(MainTable.EPISODES_NAME, MainTable.EPISODES_NAME);
        mProjectionMapEpisodes.put(MainTable.EPISODES_SEASON, MainTable.EPISODES_SEASON);
        mProjectionMapEpisodes.put(MainTable.EPISODES_NUMBER, MainTable.EPISODES_NUMBER);
        mProjectionMapEpisodes.put(MainTable.EPISODES_SERIAL_ID, MainTable.EPISODES_SERIAL_ID);
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new DatabaseHelper(getContext());

        try {
            mOpenHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        List<String> list = uri.getPathSegments();

        switch (mUriMatcher.match(uri))
        {
            case SERIALS:
                qb.setTables(MainTable.TABLE_SERIALS);
                qb.setProjectionMap(mProjectionMapSerials);

                if (TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = MainTable.DEFAULT_SORT_ORDER_SERIALS;
                }

                break;

            case SERIAL_FILTER:
                qb.setTables(MainTable.TABLE_SERIALS);
                qb.setProjectionMap(mProjectionMapSerials);
                qb.appendWhere(MainTable.SERIALS_SEARCH_NAME + " LIKE ?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{"%" + uri.getLastPathSegment().toLowerCase() + "%"});

                if (TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = MainTable.DEFAULT_SORT_ORDER_SERIALS;
                }

                break;

            case SERIAL_BY_ID:
                qb.setTables(MainTable.TABLE_SERIALS);
                qb.setProjectionMap(mProjectionMapSerials);
                qb.appendWhere(MainTable._ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{uri.getLastPathSegment()});

                if (TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = MainTable.DEFAULT_SORT_ORDER_SERIALS;
                }

                break;

            case EPISODES:
                qb.setTables(MainTable.TABLE_EPISODES);
                qb.setProjectionMap(mProjectionMapEpisodes);

                if (TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = MainTable.DEFAULT_SORT_ORDER_EPISODES;
                }

                break;

            case EPISODE_BY_ID:
                qb.setTables(MainTable.TABLE_EPISODES);
                qb.setProjectionMap(mProjectionMapEpisodes);
                qb.appendWhere(MainTable._ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{uri.getLastPathSegment()});

                if (TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = MainTable.DEFAULT_SORT_ORDER_EPISODES;
                }

                break;

            case EPISODES_BY_SERIAL_SEASON:
                qb.setTables(MainTable.TABLE_EPISODES);
                qb.setProjectionMap(mProjectionMapEpisodes);
                qb.appendWhere(MainTable.EPISODES_SERIAL_ID + "=? AND ");
                qb.appendWhere(MainTable.EPISODES_SEASON + "=?");

                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[] {list.get(1), list.get(2)});

                if (TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = MainTable.DEFAULT_SORT_ORDER_EPISODES;
                }

                break;

            case EPISODE_BY_SERIAL_SEASON_NUMBER:
                qb.setTables(MainTable.TABLE_EPISODES);
                qb.setProjectionMap(mProjectionMapEpisodes);
                qb.appendWhere(MainTable.EPISODES_SERIAL_ID + "=? AND ");
                qb.appendWhere(MainTable.EPISODES_SEASON + "=? AND ");
                qb.appendWhere(MainTable.EPISODES_NUMBER + "=?");

                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[] {list.get(1), list.get(2), list.get(3)});

                if (TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = MainTable.DEFAULT_SORT_ORDER_EPISODES;
                }

                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor c = qb.query(db, projection, selection, selectionArgs,
                null /* no group */, null /* no filter */, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri)
    {
        switch (mUriMatcher.match(uri))
        {
            case SERIALS:
            case SERIAL_FILTER:
            case EPISODES:
            case EPISODES_BY_SERIAL_SEASON:
                return MainTable.CONTENT_TYPE;
            case SERIAL_BY_ID:
            case EPISODE_BY_ID:
            case EPISODE_BY_SERIAL_SEASON_NUMBER:
                return MainTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues)
    {
        if (mUriMatcher.match(uri) != SERIALS)
        {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;

        if (initialValues != null)
        {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        if (!values.containsKey(MainTable.SERIALS_NAME))
        {
            values.put(MainTable.SERIALS_NAME, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId = db.insert(MainTable.TABLE_SERIALS, null, values);

        if (rowId > 0)
        {
            Uri noteUri = ContentUris.withAppendedId(MainTable.CONTENT_ID_URI_SERIALS, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalWhere;

        int count;

        switch (mUriMatcher.match(uri))
        {
            case SERIALS:
                count = db.delete(MainTable.TABLE_SERIALS, where, whereArgs);
                break;

            case SERIAL_BY_ID:
                finalWhere = DatabaseUtils.concatenateWhere(MainTable._ID + " = " + ContentUris.parseId(uri), where);
                count = db.delete(MainTable.TABLE_SERIALS, finalWhere, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }


    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String finalWhere;

        switch (mUriMatcher.match(uri))
        {
            case SERIALS:
                count = db.update(MainTable.TABLE_SERIALS, values, where, whereArgs);
                break;

            case SERIAL_BY_ID:
                finalWhere = DatabaseUtils.concatenateWhere(MainTable._ID + " = " + ContentUris.parseId(uri), where);
                count = db.update(MainTable.TABLE_SERIALS, values, finalWhere, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }


    private class DatabaseHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "serial_db";
        private static final int DATABASE_VERSION = 1;

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

        public void createDataBase() throws IOException
        {
            String mDbPath = "/data/data/" + getContext().getPackageName() + "/databases/";
            boolean mDataBaseExist = new File(mDbPath + DATABASE_NAME).exists();
            if(!mDataBaseExist)
            {
                this.getReadableDatabase();
                this.close();
                try
                {
                    InputStream mInput = getContext().getAssets().open(DATABASE_NAME);
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
    }
}