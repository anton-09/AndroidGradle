package ru.home.mediafilerenamer.helper;

import android.os.AsyncTask;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import ru.home.mediafilerenamer.MyApplication;
import ru.home.mediafilerenamer.entities.MediaDir;

public class FileCountHelper
{
    public static void processFile(File file, boolean subFolders, OneFileCallback callback, LocalDate filterDate, boolean allDates)
    {
        new CountMediaFilesAsyncTask(subFolders, callback, filterDate, allDates).execute(file);
    }

    private static class CountMediaFilesAsyncTask extends AsyncTask<File, MediaDir, MediaDir>
    {
        int jpgCount;
        int mp4Count;
        int jpegCount;
        int currentJpgCount = 0;
        int currentMp4Count = 0;
        int currentJpegCount = 0;

        OneFileCallback oneFileCallback;
        boolean processSubFolders;
        LocalDate fileFilterDate;
        boolean takeAllDates;

        private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

        CountMediaFilesAsyncTask(boolean subFolders, OneFileCallback callback, LocalDate filterDate, boolean allDates)
        {
            processSubFolders = subFolders;
            oneFileCallback = callback;
            fileFilterDate = filterDate;
            takeAllDates = allDates;
        }

        @Override
        protected void onProgressUpdate(MediaDir... params)
        {
            oneFileCallback.handleProgress(params[0]);
        }

        @Override
        protected MediaDir doInBackground(File... params)
        {
            //   /config, например, дает null для себя
            if (params[0].listFiles() != null)
            {
                File[] fileList = params[0].listFiles();
                Arrays.sort(fileList);
                for (File aFile : fileList)
                {
                    if (processSubFolders && aFile.isDirectory())
                    {
                        jpgCount = 0;
                        mp4Count = 0;
                        jpegCount = 0;

                        //   /config, например, дает null при обходе в /
                        if (aFile.listFiles() != null)
                        {
                            File[] aFileList = aFile.listFiles();
                            for (File subFile : aFileList)
                            {
                                if (subFile.isFile() && subFile.getName().toLowerCase().endsWith("jpg") && (takeAllDates || fileFilterDate.equals(Instant.ofEpochMilli(subFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                                {
                                    jpgCount++;
                                }
                                else if (subFile.isFile() && subFile.getName().toLowerCase().endsWith("mp4") && (takeAllDates || fileFilterDate.equals(Instant.ofEpochMilli(subFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                                {
                                    mp4Count++;
                                }
                                else if (subFile.isFile() && subFile.getName().toLowerCase().endsWith("jpeg") && (takeAllDates || fileFilterDate.equals(Instant.ofEpochMilli(subFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                                {
                                    jpegCount++;
                                }
                            }
                        }

                        publishProgress(new MediaDir(aFile, String.valueOf(jpgCount), String.valueOf(mp4Count), String.valueOf(jpegCount)));
                    }
                    else
                    {
                        if (aFile.isFile() && aFile.getName().toLowerCase().endsWith("jpg") && (takeAllDates || fileFilterDate.equals(Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                        {
                            currentJpgCount++;

                            // processSubFolders to exclude extra publishing intermediate progress in main screen
                            // needed only in change folder activity
                            if (processSubFolders && MyApplication.isShowMediaFiles())
                            {
                                publishProgress(new MediaDir(aFile, "", "", ""));
                            }
                        }
                        else if (aFile.isFile() && aFile.getName().toLowerCase().endsWith("mp4") && (takeAllDates || fileFilterDate.equals(Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                        {
                            currentMp4Count++;

                            // processSubFolders to exclude extra publishing intermediate progress in main screen
                            // needed only in change folder activity
                            if (processSubFolders && MyApplication.isShowMediaFiles())
                            {
                                publishProgress(new MediaDir(aFile, "", "", ""));
                            }
                        }
                        else if (aFile.isFile() && aFile.getName().toLowerCase().endsWith("jpeg") && (takeAllDates || fileFilterDate.equals(Instant.ofEpochMilli(aFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate())))
                        {
                            currentJpegCount++;

                            // processSubFolders to exclude extra publishing intermediate progress in main screen
                            // needed only in change folder activity
                            if (processSubFolders && MyApplication.isShowMediaFiles())
                            {
                                publishProgress(new MediaDir(aFile, "", "", ""));
                            }
                        }
                    }

                }
            }

            return new MediaDir(params[0], String.valueOf(currentJpgCount), String.valueOf(currentMp4Count), String.valueOf(currentJpegCount));
        }

        @Override
        protected void onPostExecute(MediaDir mediaDir)
        {
            oneFileCallback.handleFinal(mediaDir);
        }

    }
}
