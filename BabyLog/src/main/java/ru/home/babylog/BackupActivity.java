package ru.home.babylog;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BackupActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    SimpleDateFormat backupDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    File backupStorage;
    List<String> dataList;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, R.string.backup_no_sd, Toast.LENGTH_LONG).show();
            return;
        }

        backupStorage = new File(android.os.Environment.getExternalStorageDirectory(), "/BabyLog");

        if (!backupStorage.exists())
            backupStorage.mkdirs();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewBackup);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        dataList = new ArrayList<String>(Arrays.asList(backupStorage.list()));
        adapter = new RecyclerViewAdapter(dataList, this);
        recyclerView.setAdapter(adapter);
    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case R.id.menu_add:
//                getLoaderManager().initLoader(0, null, this);
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new MainListFragment.MyCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        String shortFileName =  backupDateFormat.format(new Date()) + ".csv";
        File fileName = new File(backupStorage, shortFileName);

        try
        {
            FileWriter fileWriter = new FileWriter(fileName);

            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                String output = cursor.getString(1) + ";" + cursor.getInt(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4) + ";" + cursor.getString(5) + "\n";
                fileWriter.write(output);
                cursor.moveToNext();
            }

            fileWriter.flush();
            fileWriter.close();

            dataList.add(shortFileName);
            adapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            Toast.makeText(this, R.string.backup_write_error, Toast.LENGTH_LONG).show();
            fileName.delete();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

}
