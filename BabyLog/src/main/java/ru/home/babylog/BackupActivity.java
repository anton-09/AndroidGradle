package ru.home.babylog;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BackupActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    SimpleDateFormat backupDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    BackupRecyclerViewAdapter backupRecyclerViewAdapter;
    RecyclerView recyclerView;
    File backupStorage;
    List<File> mList;

    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);

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

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, R.string.backup_no_sd, Toast.LENGTH_LONG).show();
            return;
        }

        backupStorage = new File(android.os.Environment.getExternalStorageDirectory(), "/BabyLog");

        if (!backupStorage.exists())
        {
            backupStorage.mkdirs();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewBackup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleRecyclerViewDivider(this));

        File[] filesList = backupStorage.listFiles();
        Arrays.sort(filesList, new Comparator<File>()
        {
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        mList = new ArrayList<File>(Arrays.asList(filesList));


        backupRecyclerViewAdapter = new BackupRecyclerViewAdapter(mList, this, this);
        recyclerView.setAdapter(backupRecyclerViewAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getLoaderManager().initLoader(0, null, BackupActivity.this);
            }
        });

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new MyCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        String shortFileName = backupDateFormat.format(new Date()) + ".csv";
        File fileName = new File(backupStorage, shortFileName);

        try
        {
            FileWriter fileWriter = new FileWriter(fileName);

            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                String output = cursor.getString(1) + ";" + cursor.getInt(2) + ";" + cursor.getString(3).replace("\n", " ") + ";" + cursor.getString(4).replace("\n", " ") + ";" + cursor.getString(5).replace("\n", " ") + "\n";
                fileWriter.write(output);
                cursor.moveToNext();
            }

            fileWriter.flush();
            fileWriter.close();

            mList.add(fileName);
            backupRecyclerViewAdapter.notifyDataSetChanged();
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

    @Override
    public void recyclerViewListClicked(View v, final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(getString(R.string.restore_backup) + mList.get(position) + "?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        try
                        {
                            FileReader fileReader = new FileReader(mList.get(position));
                            BufferedReader bufferedReader = new BufferedReader(fileReader);

                            MyApplication.getDBAdapter().deleteAllData();

                            String line = "";
                            String[] item;
                            while ((line = bufferedReader.readLine()) != null)
                            {
                                item = line.split(";", -1);
                                MyApplication.getDBAdapter().addData(item[0], Integer.parseInt(item[1]), item[2], item[3], item[4]);
                            }

                            setResult(RESULT_OK, new Intent());
                            finish();
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
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
    public void recyclerViewListLongClicked(View v, final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(getString(R.string.delete_backup) + mList.get(position) + "?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        mList.get(position).delete();
                        mList.remove(position);
                        backupRecyclerViewAdapter.notifyItemRemoved(position);
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
