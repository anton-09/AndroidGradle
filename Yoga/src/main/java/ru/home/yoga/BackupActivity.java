package ru.home.yoga;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BackupActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    private final SimpleDateFormat backupDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private BackupRecyclerViewAdapter backupRecyclerViewAdapter;
    private File backupStorage;
    private List<File> mList;

    Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, R.string.backup_no_sd, Toast.LENGTH_LONG).show();
            return;
        }

        backupStorage = new File(Environment.getExternalStorageDirectory(), "/Yoga");
        if (!backupStorage.exists())
        {
            backupStorage.mkdirs();
        }

        initToolbar();
        initRecyclerView();
    }

    private void initToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Workaround to get WHITE back arrow for pre-lollipop devices!!!
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            Drawable backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            backArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }
    }

    private void initRecyclerView()
    {
        File[] filesList = backupStorage.listFiles();
        Arrays.sort(filesList, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        mList = new ArrayList<File>(Arrays.asList(filesList));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleRecyclerViewDivider(this));

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
    public void recyclerViewListClicked(final int position)
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
                            FileInputStream fileInputStream = new FileInputStream(mList.get(position));
                            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "Windows-1251");
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                            String line;
                            String[] item;
                            int itemCount;
                            int typeCount;
                            int durationCount;
                            int studioCount;
                            int i;

                            line = bufferedReader.readLine();
                            item = line.split(";", -1);
                            itemCount = Integer.parseInt(item[0]);
                            typeCount = Integer.parseInt(item[1]);
                            durationCount = Integer.parseInt(item[2]);
                            studioCount = Integer.parseInt(item[3]);

                            MyApplication.getDBAdapter().deleteAllData();

                            for (i = 0; i < typeCount; i++)
                            {
                                line = bufferedReader.readLine();
                                MyApplication.getDBAdapter().addType(line);
                            }

                            for (i = 0; i < durationCount; i++)
                            {
                                line = bufferedReader.readLine();
                                MyApplication.getDBAdapter().addDuration(Double.parseDouble(line));
                            }

                            for (i = 0; i < studioCount; i++)
                            {
                                line = bufferedReader.readLine();
                                MyApplication.getDBAdapter().addStudio(line);
                            }

                            for (i = 0; i < itemCount; i++)
                            {
                                line = bufferedReader.readLine();
                                item = line.split(";", -1);
                                MyApplication.getDBAdapter().addData(item[0], Integer.parseInt(item[1]), Integer.parseInt(item[2]), Integer.parseInt(item[3]), Integer.parseInt(item[4]), Integer.parseInt(item[5]));
                            }

                            bufferedReader.close();


                            setResult(RESULT_OK, new Intent());
                            finish();
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
    public void recyclerViewListLongClicked(final int position)
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
