package ru.home.fitness.adapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ru.home.fitness.MyApplication;

public class FitnessDbAdapter
{
    private static final String DATABASE_NAME = "fitness_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MUSCLE = "muscle";
    private static final String MUSCLE_ID = "_id";
    private static final String MUSCLE_NAME = "name";
    private static final String CREATE_MUSCLE = "CREATE TABLE " + TABLE_MUSCLE + " ("
            + MUSCLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MUSCLE_NAME + " TEXT);";

    private static final String TABLE_EXERCISE = "exercise";
    private static final String EXERCISE_ID = "_id";
    private static final String EXERCISE_NAME = "name";
    private static final String EXERCISE_MUSCLE_ID = "muscle_id";
    private static final String CREATE_EXERCISE = "CREATE TABLE " + TABLE_EXERCISE + " ("
            + EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EXERCISE_NAME + " TEXT, "
            + EXERCISE_MUSCLE_ID + " INTEGER);";

    private static final String TABLE_WORKOUT = "workout";
    private static final String WORKOUT_ID = "_id";
    private static final String WORKOUT_NAME = "name";
    private static final String WORKOUT_START_DATE = "start_date";
    private static final String WORKOUT_END_DATE = "end_date";
    private static final String CREATE_WORKOUT = "CREATE TABLE " + TABLE_WORKOUT + " ("
            + WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WORKOUT_NAME + " TEXT, "
            + WORKOUT_START_DATE + " INTEGER, "
            + WORKOUT_END_DATE + " INTEGER);";

    private static final String TABLE_WORKOUT_EXERCISE_CROSS = "workout_exercise_cross";
    private static final String WORKOUT_EXERCISE_CROSS_ID = "_id";
    private static final String WORKOUT_EXERCISE_CROSS_WORKOUT_ID = "workout_id";
    private static final String WORKOUT_EXERCISE_CROSS_EXERCISE_ID = "exercise_id";
    private static final String CREATE_WORKOUT_EXERCISE_CROSS = "CREATE TABLE " + TABLE_WORKOUT_EXERCISE_CROSS + " ("
            + WORKOUT_EXERCISE_CROSS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WORKOUT_EXERCISE_CROSS_WORKOUT_ID + " INTEGER, "
            + WORKOUT_EXERCISE_CROSS_EXERCISE_ID + " INTEGER);";

    private static final String TABLE_ACTION = "action";
    private static final String ACTION_ID = "_id";
    private static final String ACTION_DATE = "date";
    private static final String ACTION_EXERCISE = "exercise_id";
    private static final String ACTION_COMMENT = "comment";
    private static final String CREATE_ACTION = "CREATE TABLE " + TABLE_ACTION + " ("
            + ACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACTION_DATE + " INTEGER, "
            + ACTION_EXERCISE + " INTEGER, "
            + ACTION_COMMENT + " TEXT);";

    private SQLiteDatabase mDb;

    public FitnessDbAdapter()
    {
    }

    public void open() throws SQLException
    {
        DatabaseHelper mDbHelper = new DatabaseHelper();
        mDb = mDbHelper.getWritableDatabase();
    }





    public Cursor getMuscleData()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_MUSCLE, new String[]{MUSCLE_ID, MUSCLE_NAME}, null, null, null, null, null);
    }

    public Cursor getMuscleDataById(long id)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_MUSCLE, new String[]{MUSCLE_ID, MUSCLE_NAME}, MUSCLE_ID + " = " + id, null, null, null, null);
    }

    public void addMuscleData(String name)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MUSCLE_NAME, name);
        mDb.insert(TABLE_MUSCLE, null, contentValues);
    }

    public void updateMuscleData(long id, String name)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MUSCLE_NAME, name);
        mDb.update(TABLE_MUSCLE, contentValues, MUSCLE_ID + " = " + id, null);
    }

    public void deleteMuscleData(long id)
    {
        mDb.delete(TABLE_MUSCLE, MUSCLE_ID + " = " + id, null);
    }






    public Cursor getExerciseData()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_EXERCISE + " e LEFT JOIN " + TABLE_MUSCLE + " m ON e." + EXERCISE_MUSCLE_ID + " = m." + MUSCLE_ID, new String[]{"e." + EXERCISE_ID, "e." + EXERCISE_NAME, "m." + MUSCLE_ID, "m." + MUSCLE_NAME}, null, null, null, null, null);
    }

    public Cursor getExerciseDataById(long id)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_EXERCISE + " e LEFT JOIN " + TABLE_MUSCLE + " m ON e." + EXERCISE_MUSCLE_ID + " = m." + MUSCLE_ID, new String[]{"e." + EXERCISE_ID, "e." + EXERCISE_NAME, "m." + MUSCLE_ID, "m." + MUSCLE_NAME}, "e." + EXERCISE_ID + " = " + id, null, null, null, null);
    }

    public void addExerciseData(String name, long id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXERCISE_NAME, name);
        contentValues.put(EXERCISE_MUSCLE_ID, id);
        mDb.insert(TABLE_EXERCISE, null, contentValues);
    }

    public void updateExerciseData(long id, String name, long id2)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXERCISE_NAME, name);
        contentValues.put(EXERCISE_MUSCLE_ID, id2);
        mDb.update(TABLE_EXERCISE, contentValues, EXERCISE_ID + " = " + id, null);
    }

    public void deleteExerciseData(long id)
    {
        mDb.delete(TABLE_EXERCISE, EXERCISE_ID + " = " + id, null);
    }







    public Cursor getWorkoutData()
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_WORKOUT + " w JOIN " + TABLE_WORKOUT_EXERCISE_CROSS + " c ON w." + WORKOUT_ID + " = c." + WORKOUT_EXERCISE_CROSS_WORKOUT_ID + " JOIN " + TABLE_EXERCISE + " e ON c." + WORKOUT_EXERCISE_CROSS_EXERCISE_ID + " = e." + EXERCISE_ID + " LEFT JOIN " + TABLE_MUSCLE + " m ON e." + EXERCISE_MUSCLE_ID + " = m." + MUSCLE_ID, new String[]{"w." + WORKOUT_ID, "w." + WORKOUT_NAME, "w." + WORKOUT_START_DATE, "w." + WORKOUT_END_DATE, "e." + EXERCISE_ID, "e." + EXERCISE_NAME, "m." + MUSCLE_ID, "m." + MUSCLE_NAME}, null, null, null, null, "w." + WORKOUT_ID + " ASC");
    }

    public Cursor getWorkoutDataById(long id)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_WORKOUT + " w JOIN " + TABLE_WORKOUT_EXERCISE_CROSS + " c ON w." + WORKOUT_ID + " = c." + WORKOUT_EXERCISE_CROSS_WORKOUT_ID + " JOIN " + TABLE_EXERCISE + " e ON c." + WORKOUT_EXERCISE_CROSS_EXERCISE_ID + " = e." + EXERCISE_ID + " LEFT JOIN " + TABLE_MUSCLE + " m ON e." + EXERCISE_MUSCLE_ID + " = m." + MUSCLE_ID, new String[]{"w." + WORKOUT_ID, "w." + WORKOUT_NAME, "w." + WORKOUT_START_DATE, "w." + WORKOUT_END_DATE, "e." + EXERCISE_ID, "e." + EXERCISE_NAME, "m." + MUSCLE_ID, "m." + MUSCLE_NAME}, "w." + WORKOUT_ID + " = " + id, null, null, null, "w." + WORKOUT_ID + " ASC");
    }

    public void addWorkoutData(String name, String startDate, String endDate, ArrayList<Long> exercisesId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORKOUT_NAME, name);
        contentValues.put(WORKOUT_START_DATE, startDate);
        contentValues.put(WORKOUT_END_DATE, endDate);
        Long id = mDb.insert(TABLE_WORKOUT, null, contentValues);

        for (Long exerciseId : exercisesId)
        {
            contentValues = new ContentValues();
            contentValues.put(WORKOUT_EXERCISE_CROSS_EXERCISE_ID, exerciseId);
            contentValues.put(WORKOUT_EXERCISE_CROSS_WORKOUT_ID, id);
            mDb.insert(TABLE_WORKOUT_EXERCISE_CROSS, null, contentValues);
        }
    }

    public void updateWorkoutData(long id, String name, String startDate, String endDate, ArrayList<Long> exercisesId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORKOUT_NAME, name);
        contentValues.put(WORKOUT_START_DATE, startDate);
        contentValues.put(WORKOUT_END_DATE, endDate);
        mDb.update(TABLE_WORKOUT, contentValues, WORKOUT_ID + " = " + id, null);

        mDb.delete(TABLE_WORKOUT_EXERCISE_CROSS, WORKOUT_EXERCISE_CROSS_WORKOUT_ID + " = " + id, null);
        for (Long exerciseId : exercisesId)
        {
            contentValues = new ContentValues();
            contentValues.put(WORKOUT_EXERCISE_CROSS_EXERCISE_ID, exerciseId);
            contentValues.put(WORKOUT_EXERCISE_CROSS_WORKOUT_ID, id);
            mDb.insert(TABLE_WORKOUT_EXERCISE_CROSS, null, contentValues);
        }
    }

    public void deleteWorkoutData(long id)
    {
        mDb.delete(TABLE_WORKOUT, WORKOUT_ID + " = " + id, null);
        mDb.delete(TABLE_WORKOUT_EXERCISE_CROSS, WORKOUT_EXERCISE_CROSS_WORKOUT_ID + " = " + id, null);
    }






    public Cursor getActionData(String date)
    {
        if (!mDb.isOpen())
        {
            open();
        }
        return mDb.query(TABLE_WORKOUT + " w JOIN " + TABLE_WORKOUT_EXERCISE_CROSS + " c ON w." + WORKOUT_ID + " = c." + WORKOUT_EXERCISE_CROSS_WORKOUT_ID + " JOIN " + TABLE_EXERCISE + " e ON c." + WORKOUT_EXERCISE_CROSS_EXERCISE_ID + " = e." + EXERCISE_ID + " LEFT JOIN " + TABLE_MUSCLE + " m ON e." + EXERCISE_MUSCLE_ID + " = m." + MUSCLE_ID + " LEFT JOIN " + TABLE_ACTION + " a ON e." + EXERCISE_ID + " = a." + ACTION_EXERCISE, new String[]{"a." + ACTION_ID, "CASE WHEN a." + ACTION_DATE + " = '" + date + "' THEN a." + ACTION_DATE + " ELSE '' END", "a." + ACTION_COMMENT, "w." + WORKOUT_NAME, "e." + EXERCISE_ID, "e." + EXERCISE_NAME, "m." + MUSCLE_ID, "m." + MUSCLE_NAME}, "(a." + ACTION_DATE + " = '" + date + "' OR a." + ACTION_DATE + " is null OR a." + ACTION_DATE + " != '" + date + "' AND NOT EXISTS (SELECT * FROM " + TABLE_ACTION + " aaa WHERE aaa." + ACTION_DATE + " = '" + date + "' AND aaa." + ACTION_EXERCISE + " = e." + EXERCISE_ID + ")) AND '" + date + "' BETWEEN CASE WHEN w." + WORKOUT_START_DATE + " = '' THEN '1970-01-01' ELSE w." + WORKOUT_START_DATE + " END AND CASE WHEN w." + WORKOUT_END_DATE + " = '' THEN '9999-12-31' ELSE w." + WORKOUT_END_DATE + " END", null, null, null, "w." + WORKOUT_ID + " ASC");
    }

    public void addActionData(String date, long exerciseId, String comment)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACTION_DATE, date);
        contentValues.put(ACTION_EXERCISE, exerciseId);
        contentValues.put(ACTION_COMMENT, comment);
        mDb.insert(TABLE_ACTION, null, contentValues);
    }

    public void updateActionData(long id, String date, long exerciseId, String comment)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACTION_DATE, date);
        contentValues.put(ACTION_EXERCISE, exerciseId);
        contentValues.put(ACTION_COMMENT, comment);
        mDb.update(TABLE_ACTION, contentValues, ACTION_ID + " = " + id, null);
    }

    public void deleteActionData(long id)
    {
        mDb.delete(TABLE_ACTION, ACTION_ID + " = " + id, null);
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
            db.execSQL(CREATE_MUSCLE);
            db.execSQL(CREATE_EXERCISE);
            db.execSQL(CREATE_WORKOUT);
            db.execSQL(CREATE_WORKOUT_EXERCISE_CROSS);
            db.execSQL(CREATE_ACTION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

    }
}

