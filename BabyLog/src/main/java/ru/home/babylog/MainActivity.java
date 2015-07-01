package ru.home.babylog;

import android.app.LoaderManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    private static final int REQUEST_CODE_ADD_DATA = 1;

    private MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private final SimpleDateFormat viewDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyyMMdd");

    private ArrayList<ActivityItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleRecyclerViewDivider(this));

        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(this);
        recyclerView.setAdapter(mainRecyclerViewAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentAdd = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intentAdd, REQUEST_CODE_ADD_DATA);
            }
        });

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
        switch (item.getItemId())
        {
            case R.id.menu_day_night:
                MyApplication.toggleTheme();
                recreate();
                return true;

            case R.id.menu_backup:
                Intent intentBackup = new Intent(this, BackupActivity.class);
                startActivityForResult(intentBackup, REQUEST_CODE_ADD_DATA);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        return new MyCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        ActivityItem activityItem;
        Date date;
        mList = new ArrayList<ActivityItem>();

        data.moveToFirst();
        while (!data.isAfterLast())
        {
            try
            {
                date = dbDateFormat.parse(data.getString(1));
            }
            catch (ParseException e)
            {
                date = new Date(0);
            }

            activityItem = new ActivityItem(data.getLong(0), viewDateFormat.format(date), data.getInt(2), data.getString(3), data.getString(4), data.getString(5));
            mList.add(activityItem);
            data.moveToNext();
        }

        mainRecyclerViewAdapter.setDataModel(mList);
        mainRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    @Override
    public void recyclerViewListClicked(int position)
    {
        Intent intentEdit = new Intent(this, AddActivity.class);
        intentEdit.putExtra("clickedId", mList.get(position).mId);
        startActivityForResult(intentEdit, REQUEST_CODE_ADD_DATA);
    }

    @Override
    public void recyclerViewListLongClicked(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(getString(R.string.delete_day) + mList.get(position).mDate + "?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        MyApplication.getDBAdapter().deleteData(mList.get(position).mId);
                        mList.remove(position);
                        mainRecyclerViewAdapter.notifyItemRemoved(position);
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
}
