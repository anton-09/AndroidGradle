package ru.home.mediafilerenamer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ListIterator;

import ru.home.mediafilerenamer.entities.MediaDir;
import ru.home.mediafilerenamer.entities.MediaLog;
import ru.home.mediafilerenamer.helper.FileCountHelper;
import ru.home.mediafilerenamer.helper.OneFileCallback;
import ru.home.mediafilerenamer.recyclerview.LogRecyclerViewAdapter;
import ru.home.mediafilerenamer.recyclerview.SimpleRecyclerViewDivider;

public class MainActivity extends AppCompatActivity implements OneFileCallback
{
    private static final int PERMISSION_REQUEST_SDCARD = 0;

    MediaDir url;
    ArrayList<MediaLog> fileArrayList = new ArrayList<>();

    TextView textView;
    TextView textViewPhotoCount;
    TextView textViewVideoCount;

    RecyclerView logRecyclerView;
    LogRecyclerViewAdapter logRecyclerViewAdapter;
    ProgressBar progressBar;

    Button buttonProcessFiles;

    boolean isRevertAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view);
        textViewPhotoCount = (TextView) findViewById(R.id.text_view_photo_count);
        textViewVideoCount = (TextView) findViewById(R.id.text_view_video_count);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        buttonProcessFiles = (Button) findViewById(R.id.button_process_files);

        logRecyclerView = (RecyclerView) findViewById(R.id.log_recycler_view);
        logRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        logRecyclerView.setItemAnimator(new DefaultItemAnimator());
        logRecyclerView.addItemDecoration(new SimpleRecyclerViewDivider(this));

        logRecyclerViewAdapter = new LogRecyclerViewAdapter();
        logRecyclerViewAdapter.setDataModel(fileArrayList);
        logRecyclerView.setAdapter(logRecyclerViewAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                Toast.makeText(this, "SD Card access is required", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "Permission is not available. Requesting SD Card permission", Toast.LENGTH_SHORT).show();

            // Request the permission. The result will be received in onRequestPermissionResult()
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_SDCARD);
        }
        else
        {
            // Permission is already available
            Toast.makeText(this, "SD Card permission is available", Toast.LENGTH_SHORT).show();
        }

        FileCountHelper.processFile(new File(MyApplication.getInitialFolder()), false, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSION_REQUEST_SDCARD)
        {
            // Request for permission.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Permission has been granted
                Toast.makeText(this, "SD Card permission was granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Permission request was denied.
                Toast.makeText(this, "SD Card permission request was denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_revert).setVisible(isRevertAvailable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_setting:
                Intent intentSummary = new Intent(this, SettingsActivity.class);
                startActivityForResult(intentSummary, 1);
                return true;

            case R.id.menu_revert:
                buttonProcessFiles.setEnabled(false);
                isRevertAvailable = false;
                invalidateOptionsMenu();
                new RevertAsyncTask().execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data == null) // from settings activity
        {
            FileCountHelper.processFile(new File(MyApplication.getInitialFolder()), false, this);
        }
        else // from change folder activity
        {
            url = data.getParcelableExtra("url");
            textView.setText(url.getUrl().getAbsolutePath());
            textViewPhotoCount.setText(url.getPhotoCount());
            textViewVideoCount.setText(url.getVideoCount());
        }
    }

    @Override
    public void handleProgress(MediaDir result)
    {
        int fuck = 0;
    }

    @Override
    public void handleFinal(MediaDir result)
    {
        url = result;
        textView.setText(result.getUrl().getAbsolutePath());
        textViewPhotoCount.setText(result.getPhotoCount());
        textViewVideoCount.setText(result.getVideoCount());
    }

    public void chooseAnotherFolder(View view)
    {
        Intent intent = new Intent(this, ChooseDirActivity.class);
        startActivityForResult(intent, 1);
    }

    public void doRenameFiles(View view)
    {
        buttonProcessFiles.setEnabled(false);
        new RenameAsyncTask().execute();
    }







    private class RenameAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MyApplication.getRenameMask());
        File newFile;

        @Override
        protected void onPreExecute()
        {
            fileArrayList.clear();
            logRecyclerViewAdapter.notifyDataSetChanged();
            progressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... params)
        {
            progressBar.setProgress(params[0]);
            logRecyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            int counter = 0;
            MediaLog mediaLog;

            File[] listFiles = url.getUrl().listFiles();
            Arrays.sort(listFiles);
            for (File aFile : listFiles)
            {
                if (aFile.getName().toLowerCase().endsWith("jpg") && MyApplication.isJPGon())
                {
                    newFile = new File(url.getUrl() + "/" + simpleDateFormat.format(new Date(aFile.lastModified())) + ".jpg");
                    if (!newFile.exists())
                    {
                        if (aFile.renameTo(newFile))
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_OK, MediaLog.DIR_FORWARD);
                        else
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);

                        fileArrayList.add(mediaLog);
                    }
                    else if (MyApplication.isVerboseLog())
                    {
                        if (aFile.getName().equals(newFile.getName()))
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_EQUAL, MediaLog.DIR_FORWARD);
                        else
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);

                        fileArrayList.add(mediaLog);
                    }
                }
                else if (aFile.getName().toLowerCase().endsWith("mp4") && MyApplication.isMP4on())
                {
                    newFile = new File(url.getUrl() + "/" + simpleDateFormat.format(new Date(aFile.lastModified())) + ".mp4");
                    if (!newFile.exists())
                    {
                        if (aFile.renameTo(newFile))
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_OK, MediaLog.DIR_FORWARD);
                        else
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);

                        fileArrayList.add(mediaLog);
                    }
                    else if (MyApplication.isVerboseLog())
                    {
                        if (aFile.getName().equals(newFile.getName()))
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_EQUAL, MediaLog.DIR_FORWARD);
                        else
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);

                        fileArrayList.add(mediaLog);
                    }
                }

                counter++;
                publishProgress(counter * 100 / listFiles.length);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            buttonProcessFiles.setEnabled(true);
            progressBar.setProgress(100);
            isRevertAvailable = true;
            invalidateOptionsMenu();
        }
    }




    private class RevertAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            int counter = fileArrayList.size();
            int initialSize = fileArrayList.size();

            for (MediaLog mediaLog : fileArrayList)
            {
                if (mediaLog.getStatus() == MediaLog.STATUS_OK)
                {
                    if (mediaLog.getFileNew().renameTo(mediaLog.getFileOld()))
                    {
                        mediaLog.setDir(MediaLog.DIR_BACKWARD);
                    }
                }
                else
                    mediaLog.setDir(MediaLog.DIR_BACKWARD);

                counter--;
                publishProgress(counter * 100 / initialSize);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... params)
        {
            progressBar.setProgress(params[0]);
            logRecyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            buttonProcessFiles.setEnabled(true);
            progressBar.setProgress(0);
        }
    }
}
