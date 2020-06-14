package ru.home.yoga.view.activity;

import android.Manifest;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.joda.time.LocalDateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ru.home.yoga.MyApplication;
import ru.home.yoga.R;
import ru.home.yoga.model.YogaItem;
import ru.home.yoga.model.db.MyCursorLoader;
import ru.home.yoga.view.SimpleRecyclerViewDivider;
import ru.home.yoga.view.adapter.BackupRecyclerViewAdapter;
import ru.home.yoga.view.RecyclerViewClickListener;

public class BackupActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    Toolbar toolbar;
    BackupRecyclerViewAdapter backupRecyclerViewAdapter;
    //TODO
    ProgressDialog mProgressDialog;

    View containerForSnackBar;

    File backupStorage;
    List<File> mList;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        containerForSnackBar = findViewById(R.id.activity_container);

        initToolbar();

        initStorage();
    }

    private void initToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
    }

    private void initRecyclerView()
    {
        //TODO
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

        File[] filesList = backupStorage.listFiles();
        Arrays.sort(filesList, new Comparator<File>()
        {
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        mList = new ArrayList<>(Arrays.asList(filesList));

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
                mProgressDialog = new ProgressDialog(BackupActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setTitle(getString(R.string.progress_dialog_load_data_from_db));
                mProgressDialog.show();

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
        //TODO
        new AsyncTask<Cursor, Integer, File>()
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();

                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setTitle(getString(R.string.progress_dialog_save_data_to_sd));
            }

            @Override
            protected File doInBackground(Cursor... cursor)
            {
                String shortFileName = LocalDateTime.now().toString(MyApplication.mBackupDateFormat);
                File fileName = new File(backupStorage, shortFileName);

                try
                {

                    int i = 0;
                    int iCount = cursor[0].getCount();

                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "Windows-1251");
                    PrintWriter printWriter = new PrintWriter(outputStreamWriter);

                    cursor[0].moveToFirst();

                    printWriter.println(iCount);
                    while (!cursor[0].isAfterLast())
                    {
                        publishProgress(++i * 100 / iCount);
                        printWriter.println(cursor[0].getString(1) + ";" + cursor[0].getString(2) + ";" + cursor[0].getString(3) + ";" + cursor[0].getString(4) + ";" + cursor[0].getString(5) + ";" + cursor[0].getString(7) + ";" + cursor[0].getString(9) + ";" + cursor[0].getString(13) + ";" + cursor[0].getString(16));
                        cursor[0].moveToNext();
                    }


                    Cursor tempCursor;

                    tempCursor = MyApplication.getDBAdapter().getTypes();
                    printWriter.println(tempCursor.getCount());
                    tempCursor.moveToFirst();
                    while (!tempCursor.isAfterLast())
                    {
                        printWriter.println(tempCursor.getString(1));
                        tempCursor.moveToNext();

                    }

                    tempCursor = MyApplication.getDBAdapter().getDurations();
                    printWriter.println(tempCursor.getCount());
                    tempCursor.moveToFirst();
                    while (!tempCursor.isAfterLast())
                    {
                        printWriter.println(tempCursor.getString(1));
                        tempCursor.moveToNext();
                    }

                    tempCursor = MyApplication.getDBAdapter().getStudios();
                    printWriter.println(tempCursor.getCount());
                    tempCursor.moveToFirst();
                    while (!tempCursor.isAfterLast())
                    {
                        printWriter.println(tempCursor.getString(1) + ";" + tempCursor.getString(2) + ";" + tempCursor.getString(3));
                        tempCursor.moveToNext();
                    }

                    tempCursor = MyApplication.getDBAdapter().getPlaces();
                    printWriter.println(tempCursor.getCount());
                    tempCursor.moveToFirst();
                    while (!tempCursor.isAfterLast())
                    {
                        printWriter.println(tempCursor.getString(1) + ";" + tempCursor.getString(2));
                        tempCursor.moveToNext();
                    }

                    tempCursor.close();
                    printWriter.close();
                } catch (Exception e)
                {
                    Toast.makeText(BackupActivity.this, R.string.backup_write_error, Toast.LENGTH_LONG).show();
                    fileName.delete();
                }

                return fileName;
            }

            @Override
            protected void onProgressUpdate(Integer... values)
            {
                super.onProgressUpdate(values);

                mProgressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(File filename)
            {
                mProgressDialog.dismiss();

                mList.add(filename);
                backupRecyclerViewAdapter.notifyDataSetChanged();
            }
        }.execute(cursor);
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
                        new AsyncTask<Void, Void, Void>()
                        {
                            @Override
                            protected void onPreExecute()
                            {
                                super.onPreExecute();

                                mProgressDialog = new ProgressDialog(BackupActivity.this);
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                mProgressDialog.setIndeterminate(true);
                                mProgressDialog.setTitle(getString(R.string.progress_dialog_load_data_from_sd));
                                mProgressDialog.show();
                            }

                            @Override
                            protected Void doInBackground(Void... nothing)
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
                                    int placeCount;
                                    int i;
                                    ArrayList<YogaItem> yogaItemArrayList = new ArrayList<>();

                                    MyApplication.getDBAdapter().deleteAllData();

                                    // Bulk insert is 10 times faster than 1-by-1, because it is done in 1 transaction
                                    line = bufferedReader.readLine();
                                    itemCount = Integer.parseInt(line);
                                    for (i = 0; i < itemCount; i++)
                                    {
                                        line = bufferedReader.readLine();
                                        item = line.split(";", -1);
                                        yogaItemArrayList.add(new YogaItem(item[0], Integer.parseInt(item[1]), item[2], Integer.parseInt(item[3]), Integer.parseInt(item[4]), Integer.parseInt(item[5]), Integer.parseInt(item[6]), Integer.parseInt(item[7]), item[8]));
                                        //MyApplication.getDBAdapter().addData(item[0], Integer.parseInt(item[1]), Integer.parseInt(item[2]), Integer.parseInt(item[3]), Integer.parseInt(item[4]), Integer.parseInt(item[5]));
                                    }
                                    MyApplication.getDBAdapter().addBulkData(yogaItemArrayList);

                                    line = bufferedReader.readLine();
                                    typeCount = Integer.parseInt(line);
                                    for (i = 0; i < typeCount; i++)
                                    {
                                        line = bufferedReader.readLine();
                                        MyApplication.getDBAdapter().addType(line);
                                    }

                                    line = bufferedReader.readLine();
                                    durationCount = Integer.parseInt(line);
                                    for (i = 0; i < durationCount; i++)
                                    {
                                        line = bufferedReader.readLine();
                                        MyApplication.getDBAdapter().addDuration(Double.parseDouble(line));
                                    }

                                    line = bufferedReader.readLine();
                                    studioCount = Integer.parseInt(line);
                                    for (i = 0; i < studioCount; i++)
                                    {
                                        line = bufferedReader.readLine();
                                        MyApplication.getDBAdapter().addStudio(line.split(";")[0], Integer.parseInt(line.split(";")[1]), line.split(";")[2]);
                                    }

                                    line = bufferedReader.readLine();
                                    placeCount = Integer.parseInt(line);
                                    for (i = 0; i < placeCount; i++)
                                    {
                                        line = bufferedReader.readLine();
                                        MyApplication.getDBAdapter().addPlace(line.split(";")[0], line.split(";")[1]);
                                    }

                                    bufferedReader.close();
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void nothing)
                            {
                                mProgressDialog.dismiss();

                                setResult(RESULT_OK, new Intent());
                                finish();
                            }
                        }.execute();
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


    // ============================
    // === New permission model ===
    // ============================

    public static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 1;


    private void initStorage()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            // Permission is already available
            initRecyclerView();
        }
        else
        {
            // Permission is missing and must be requested.
            requestWriteStoragePermission();
            //requestReadStoragePermission();
        }
    }

    private void requestWriteStoragePermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(containerForSnackBar, R.string.write_storage_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.yes, new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // Request the permission
                    ActivityCompat.requestPermissions(BackupActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE_REQUEST_CODE);
                }
            }).show();

        }
        else
        {
            Snackbar.make(containerForSnackBar, R.string.write_storage_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_WRITE_STORAGE_REQUEST_CODE)
        {
            // Request for write storage permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Permission has been granted
                initRecyclerView();
            }
            else
            {
            }
        }
    }
}
