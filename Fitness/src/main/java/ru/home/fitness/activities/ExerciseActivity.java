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

import java.util.ArrayList;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;
import ru.home.fitness.RecyclerViewClickListener;
import ru.home.fitness.SimpleRecyclerViewDivider;
import ru.home.fitness.adapters.ExerciseRecyclerViewAdapter;
import ru.home.fitness.entities.Exercise;
import ru.home.fitness.entities.Muscle;

public class ExerciseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    private static final int DUMMY_RETURN_CODE = 1;

    private ArrayList<Exercise> mList;
    private ExerciseRecyclerViewAdapter mExerciseRecyclerViewAdapter;

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

        mExerciseRecyclerViewAdapter = new ExerciseRecyclerViewAdapter(this);
        recyclerView.setAdapter(mExerciseRecyclerViewAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ExerciseActivity.this, ExerciseEditActivity.class);
                startActivityForResult(intent, DUMMY_RETURN_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void recyclerViewListClicked(int position)
    {
        Intent intent = new Intent(this, ExerciseEditActivity.class);
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
                        MyApplication.getDBAdapter().deleteExerciseData(mList.get(position).getId());
                        mList.remove(position);
                        mExerciseRecyclerViewAdapter.notifyItemRemoved(position);
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
                return MyApplication.getDBAdapter().getExerciseData();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        Exercise exercise;
        mList = new ArrayList<Exercise>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            exercise = new Exercise(cursor.getLong(0), cursor.getString(1), new Muscle(cursor.getLong(2), cursor.getString(3)));
            mList.add(exercise);
            cursor.moveToNext();
        }

        mExerciseRecyclerViewAdapter.setDataModel(mList);
        mExerciseRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }
}
