package ru.home.yoga;

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
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.home.yoga.entity.PracticeDuration;
import ru.home.yoga.entity.Studio;
import ru.home.yoga.entity.Type;
import ru.home.yoga.entity.YogaItem;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    private static final int REQUEST_CODE_ADD_DATA = 1;

    private MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private final SimpleDateFormat viewDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyyMMdd");

    private ArrayList<YogaItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);
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
        YogaItem yogaItem;
        Date date;
        mList = new ArrayList<YogaItem>();

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

            yogaItem = new YogaItem(
                    data.getLong(0),
                    viewDateFormat.format(date),
                    data.getInt(2),
                    data.getInt(3),
                    new Type(data.getInt(4), data.getString(5)),
                    new PracticeDuration(data.getInt(6), data.getDouble(7)),
                    new Studio(data.getInt(8), data.getString(9)));

            mList.add(yogaItem);
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
        intentEdit.putExtra("clickedId", mList.get(position).getId());
        startActivityForResult(intentEdit, REQUEST_CODE_ADD_DATA);
    }

    @Override
    public void recyclerViewListLongClicked(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(getString(R.string.delete))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        MyApplication.getDBAdapter().deleteData(mList.get(position).getId());
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
