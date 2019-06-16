package ru.home.mediafilerenamer;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import ru.home.mediafilerenamer.entities.MediaDir;
import ru.home.mediafilerenamer.entities.MediaLog;
import ru.home.mediafilerenamer.helper.FileCountHelper;
import ru.home.mediafilerenamer.helper.OneFileCallback;
import ru.home.mediafilerenamer.recyclerview.LogRecyclerViewAdapter;
import ru.home.mediafilerenamer.recyclerview.SimpleRecyclerViewDivider;

public class MainActivity extends AppCompatActivity implements OneFileCallback
{
    private static final int PERMISSION_REQUEST_SDCARD = 0;
//    public static final int COLOR_ON = Color.rgb(192, 255, 192);
//    public static final int COLOR_OFF = Color.rgb(255, 192, 192);

    MediaDir url;
    ArrayList<MediaLog> fileArrayList = new ArrayList<>();

    TextView textView;
    TextView textViewPhotoJpgCount;
    TextView textViewVideoMp4Count;
    TextView textViewPhotoJpegCount;

    RecyclerView logRecyclerView;
    LogRecyclerViewAdapter logRecyclerViewAdapter;
    ProgressBar progressBar;

    Button datePickerButton;
    LocalDate currentDate;
    ToggleButton selectDateCheckBox;

    Button buttonProcessFiles;

//    ImageView imageJpg;
//    ImageView imageMp4;
//    ImageView imageJpeg;

    boolean isRevertAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view);
        textViewPhotoJpgCount = (TextView) findViewById(R.id.text_view_photo_jpg_count);
        textViewVideoMp4Count = (TextView) findViewById(R.id.text_view_video_mp4_count);
        textViewPhotoJpegCount = (TextView) findViewById(R.id.text_view_photo_jpeg_count);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        datePickerButton = (Button) findViewById(R.id.date_picker_button);
        selectDateCheckBox = (ToggleButton) findViewById(R.id.select_date_check_box);

        buttonProcessFiles = (Button) findViewById(R.id.button_process_files);

//        imageJpg = (ImageView) findViewById(R.id.image_jpg);
//        imageMp4 = (ImageView) findViewById(R.id.image_mp4);
//        imageJpeg = (ImageView) findViewById(R.id.image_jpeg);

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

        currentDate = LocalDate.now();
        selectDateCheckBox.setChecked(MyApplication.isSelectDate());
        datePickerButton.setEnabled(selectDateCheckBox.isChecked());
        datePickerButton.setText(getResources().getString(R.string.select_date_for_jpeg, currentDate));
//        imageJpg.setBackgroundColor(MyApplication.isJpgOn() ? COLOR_ON : COLOR_OFF);
//        imageMp4.setBackgroundColor(MyApplication.isMp4On() ? COLOR_ON : COLOR_OFF);
//        imageJpeg.setBackgroundColor(MyApplication.isJpegOn() ? COLOR_ON : COLOR_OFF);


        FileCountHelper.processFile(new File(MyApplication.getInitialFolder()), false, this, currentDate, !selectDateCheckBox.isChecked());
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
                datePickerButton.setEnabled(false);
                selectDateCheckBox.setEnabled(false);
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
//            imageJpg.setBackgroundColor(MyApplication.isJpgOn() ? COLOR_ON : COLOR_OFF);
//            imageMp4.setBackgroundColor(MyApplication.isMp4On() ? COLOR_ON : COLOR_OFF);
//            imageJpeg.setBackgroundColor(MyApplication.isJpegOn() ? COLOR_ON : COLOR_OFF);
            FileCountHelper.processFile(new File(MyApplication.getInitialFolder()), false, this, currentDate, !selectDateCheckBox.isChecked());
        }
        else // from change folder activity
        {
            url = data.getParcelableExtra("url");
            textView.setText(url.getUrl().getAbsolutePath());
            textViewPhotoJpgCount.setText(url.getPhotoJpgCount());
            textViewVideoMp4Count.setText(url.getVideoMp4Count());
            textViewPhotoJpegCount.setText(url.getPhotoJpegCount());
        }
    }

    @Override
    public void handleProgress(MediaDir result)
    {
    }

    @Override
    public void handleFinal(MediaDir result)
    {
        url = result;
        textView.setText(result.getUrl().getAbsolutePath());
        textViewPhotoJpgCount.setText(result.getPhotoJpgCount());
        textViewVideoMp4Count.setText(result.getVideoMp4Count());
        textViewPhotoJpegCount.setText(result.getPhotoJpegCount());
    }

    public void chooseAnotherFolder(View view)
    {
        Intent intent = new Intent(this, ChooseDirActivity.class);
        startActivityForResult(intent, 1);
    }

    public void doRenameFiles(View view)
    {
        buttonProcessFiles.setEnabled(false);
        datePickerButton.setEnabled(false);
        selectDateCheckBox.setEnabled(false);
        new RenameAsyncTask().execute();
    }

    public void doSelectDate(View view)
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                currentDate = LocalDate.of(year, month + 1, day);
                datePickerButton.setText(getResources().getString(R.string.select_date_for_jpeg, currentDate));
                FileCountHelper.processFile(new File(MyApplication.getInitialFolder()), false, MainActivity.this, currentDate, !selectDateCheckBox.isChecked());
            }
        }, currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());

        datePickerDialog.show();
    }

    public void selectDateCheckBoxClicked(View view)
    {
        MyApplication.setSelectDate(selectDateCheckBox.isChecked());
        datePickerButton.setEnabled(selectDateCheckBox.isChecked());
        FileCountHelper.processFile(new File(MyApplication.getInitialFolder()), false, this, currentDate, !selectDateCheckBox.isChecked());
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        MyApplication.getSharedPreferences().edit()
                .putBoolean("mSelectDate", MyApplication.isSelectDate())
                .apply();
    }

    private class RenameAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern(MyApplication.getRenameMask());
        File newFile;
        int processedFiles;

        @Override
        protected void onPreExecute()
        {
            fileArrayList.clear();
            logRecyclerViewAdapter.notifyDataSetChanged();
            progressBar.setProgress(0);
            processedFiles = 0;
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
                if (aFile.getName().toLowerCase().endsWith("jpg") && MyApplication.isJpgOn() && (!MyApplication.isSelectDate() || currentDate.equals(Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                {
                    newFile = new File(url.getUrl() + "/" + Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).format(simpleDateFormat) + ".jpg");
                    if (!newFile.exists())
                    {
                        if (aFile.renameTo(newFile))
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_OK, MediaLog.DIR_FORWARD);
                            processedFiles++;
                        }
                        else
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);
                        }

                        fileArrayList.add(mediaLog);
                    }
                    else if (MyApplication.isVerboseLog())
                    {
                        if (aFile.getName().equals(newFile.getName()))
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_EQUAL, MediaLog.DIR_FORWARD);
                        }
                        else
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);
                        }

                        fileArrayList.add(mediaLog);
                    }
                }
                else if (aFile.getName().toLowerCase().endsWith("mp4") && MyApplication.isMp4On() && (!MyApplication.isSelectDate() || currentDate.equals(Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                {
                    newFile = new File(url.getUrl() + "/" + Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).format(simpleDateFormat) + ".mp4");
                    if (!newFile.exists())
                    {
                        if (aFile.renameTo(newFile))
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_OK, MediaLog.DIR_FORWARD);
                            processedFiles++;
                        }
                        else
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);
                        }

                        fileArrayList.add(mediaLog);
                    }
                    else if (MyApplication.isVerboseLog())
                    {
                        if (aFile.getName().equals(newFile.getName()))
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_EQUAL, MediaLog.DIR_FORWARD);
                        }
                        else
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);
                        }

                        fileArrayList.add(mediaLog);
                    }
                }
                else if (aFile.getName().toLowerCase().endsWith("jpeg") && MyApplication.isJpegOn() && (!MyApplication.isSelectDate() || currentDate.equals(Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                {
                    newFile = new File(url.getUrl() + "/" + Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).format(simpleDateFormat) + ".jpeg");
                    if (!newFile.exists())
                    {
                        if (aFile.renameTo(newFile))
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_OK, MediaLog.DIR_FORWARD);
                            processedFiles++;
                        }
                        else
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);
                        }

                        fileArrayList.add(mediaLog);
                    }
                    else if (MyApplication.isVerboseLog())
                    {
                        if (aFile.getName().equals(newFile.getName()))
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_EQUAL, MediaLog.DIR_FORWARD);
                        }
                        else
                        {
                            mediaLog = new MediaLog(aFile, newFile, MediaLog.STATUS_CANCEL, MediaLog.DIR_FORWARD);
                        }

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
            buttonProcessFiles.setText(getResources().getString(R.string.processed_files, processedFiles));
            datePickerButton.setEnabled(selectDateCheckBox.isChecked());
            selectDateCheckBox.setEnabled(true);
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
                {
                    mediaLog.setDir(MediaLog.DIR_BACKWARD);
                }

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
            buttonProcessFiles.setText(R.string.process_files);
            datePickerButton.setEnabled(selectDateCheckBox.isChecked());
            selectDateCheckBox.setEnabled(true);
            progressBar.setProgress(0);
        }
    }
}
