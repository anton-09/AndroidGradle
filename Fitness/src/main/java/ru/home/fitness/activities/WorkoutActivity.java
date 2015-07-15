package ru.home.fitness.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;
import ru.home.fitness.RecyclerViewClickListener;
import ru.home.fitness.SimpleRecyclerViewDivider;
import ru.home.fitness.adapters.WorkoutRecyclerViewAdapter;
import ru.home.fitness.entities.Exercise;
import ru.home.fitness.entities.Muscle;
import ru.home.fitness.entities.Workout;

public class WorkoutActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    private static final int DUMMY_RETURN_CODE = 1;


    private ArrayList<Workout> mList;
    private WorkoutRecyclerViewAdapter mWorkoutRecyclerViewAdapter;

    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleRecyclerViewDivider(this));

        mWorkoutRecyclerViewAdapter = new WorkoutRecyclerViewAdapter(this);
        recyclerView.setAdapter(mWorkoutRecyclerViewAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(WorkoutActivity.this, WorkoutEditActivity.class);
                startActivityForResult(intent, DUMMY_RETURN_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void recyclerViewListClicked(int position)
    {
        Intent intent = new Intent(this, WorkoutEditActivity.class);
        intent.putExtra("clickedId", mList.get(position).getId());
        startActivityForResult(intent, DUMMY_RETURN_CODE);
    }

    @Override
    public void recyclerViewListLongClicked(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(getString(R.string.delete) + mList.get(position).getName() + "?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        MyApplication.getDBAdapter().deleteWorkoutData(mList.get(position).getId());
                        mList.remove(position);
                        mWorkoutRecyclerViewAdapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        getLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(this)
        {
            @Override
            public Cursor loadInBackground()
            {
                return MyApplication.getDBAdapter().getWorkoutData();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        Workout workout = null;
        Exercise exercise;
        Date startDate;
        Date endDate;
        Long oldId = -1L;
        mList = new ArrayList<Workout>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            if (oldId != cursor.getLong(0))
            {
                oldId = cursor.getLong(0);

                try { startDate = MyApplication.dbDateFormat.parse(cursor.getString(2)); }
                catch (ParseException e) { startDate = new Date(0); }

                try { endDate = MyApplication.dbDateFormat.parse(cursor.getString(3)); }
                catch (ParseException e) { endDate = new Date(0); }

                workout = new Workout(cursor.getLong(0), cursor.getString(1), startDate, endDate);
                mList.add(workout);
            }

            exercise = new Exercise(cursor.getLong(4), cursor.getString(5), new Muscle(cursor.getLong(6), cursor.getString(7)));
            workout.addExercise(exercise);

            cursor.moveToNext();
        }

        mWorkoutRecyclerViewAdapter.setDataModel(mList);
        mWorkoutRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }
}
