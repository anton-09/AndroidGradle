package ru.home.fitness.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;
import ru.home.fitness.RecyclerViewClickListener;
import ru.home.fitness.SimpleRecyclerViewDivider;
import ru.home.fitness.adapters.MainRecyclerViewAdapter;
import ru.home.fitness.entities.Action;
import ru.home.fitness.entities.Exercise;
import ru.home.fitness.entities.Muscle;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    private static final int DUMMY_RETURN_CODE = 1;

    private ArrayList<Action> mList;
    private MainRecyclerViewAdapter mMainRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setTitle(MyApplication.viewDateFormat.format(new Date()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleRecyclerViewDivider(this));

        mMainRecyclerViewAdapter = new MainRecyclerViewAdapter(this);
        recyclerView.setAdapter(mMainRecyclerViewAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch (item.getItemId())
        {
            case R.id.menu_day_night:
                MyApplication.toggleTheme();
                recreate();
                return true;

            case R.id.menu_muscle:
                intent = new Intent(this, MuscleActivity.class);
                startActivityForResult(intent, DUMMY_RETURN_CODE);
                return true;

            case R.id.menu_exercise:
                intent = new Intent(this, ExerciseActivity.class);
                startActivityForResult(intent, DUMMY_RETURN_CODE);
                return true;

            case R.id.menu_workout:
                intent = new Intent(this, WorkoutActivity.class);
                startActivityForResult(intent, DUMMY_RETURN_CODE);
                return true;

            case R.id.menu_calendar:
                intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void recyclerViewListClicked(int position)
    {
        if (!mMainRecyclerViewAdapter.isSelected(position))
        {
            mMainRecyclerViewAdapter.toggleSelection(position);

            MyApplication.getDBAdapter().addActionData(MyApplication.dbDateFormat.format(new Date()), mList.get(position).getExercise().getId(), mList.get(position).getComment());
        }
    }

    @Override
    public void recyclerViewListLongClicked(final int position)
    {
        if (mMainRecyclerViewAdapter.isSelected(position))
        {
            mMainRecyclerViewAdapter.toggleSelection(position);

            MyApplication.getDBAdapter().deleteActionData(mList.get(position).getId());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(this)
        {
            @Override
            public Cursor loadInBackground()
            {
                return MyApplication.getDBAdapter().getActionData(MyApplication.dbDateFormat.format(new Date()));
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        Action action;
        mList = new ArrayList<Action>();
        Date date;

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            try { date = MyApplication.dbDateFormat.parse(cursor.getString(1)); }
            catch (Exception e) { date = new Date(0); }

            action = new Action(cursor.getLong(0), date, cursor.getString(2), cursor.getString(3), new Exercise(cursor.getLong(4), cursor.getString(5), new Muscle(cursor.getLong(6), cursor.getString(7))));
            mList.add(action);

            cursor.moveToNext();
        }

        mMainRecyclerViewAdapter.setDataModel(mList);
        mMainRecyclerViewAdapter.notifyDataSetChanged();

        mMainRecyclerViewAdapter.clearSelection();
        for (int i = 0; i < mList.size(); i++)
        {
            if (mList.get(i).getDate().getTime() != 0)
            {
                mMainRecyclerViewAdapter.toggleSelection(i);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        getLoaderManager().getLoader(0).forceLoad();
    }
}
