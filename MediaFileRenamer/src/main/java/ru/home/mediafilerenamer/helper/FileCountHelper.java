package ru.home.mediafilerenamer.helper;

import android.os.AsyncTask;

import java.io.File;
import java.util.Arrays;

import ru.home.mediafilerenamer.MyApplication;
import ru.home.mediafilerenamer.entities.MediaDir;

public class FileCountHelper
{
    public static void processFile(File file, boolean subFolders, OneFileCallback callback)
    {
        new CountMediaFilesAsyncTask(subFolders, callback).execute(file);
    }

    private static class CountMediaFilesAsyncTask extends AsyncTask<File, MediaDir, MediaDir>
    {
        int jpgCount;
        int mp4Count;
        int currentJPGCount = 0;
        int currentMP4Count = 0;

        OneFileCallback oneFileCallback;
        boolean processSubFolders;

        CountMediaFilesAsyncTask(boolean subFolders, OneFileCallback callback)
        {
            processSubFolders = subFolders;
            oneFileCallback = callback;
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
            if (params[0].listFiles() !=  null)
            {
                File[] fileList = params[0].listFiles();
                Arrays.sort(fileList);
                for (File aFile : fileList)
                {
                    if (processSubFolders && aFile.isDirectory())
                    {
                        jpgCount = 0;
                        mp4Count = 0;

                        //   /config, например, дает null при обходе в /
                        if (aFile.listFiles() != null)
                        {
                            File[] aFileList = aFile.listFiles();
                            for (File subFile : aFileList)
                            {
                                if (subFile.isFile() && subFile.getName().toLowerCase().endsWith("jpg"))
                                    jpgCount++;
                                else if (subFile.isFile() && subFile.getName().toLowerCase().endsWith("mp4"))
                                    mp4Count++;
                            }
                        }

                        publishProgress(new MediaDir(aFile, String.valueOf(jpgCount), String.valueOf(mp4Count)));
                    }
                    else
                    {
                        if (aFile.isFile() && aFile.getName().toLowerCase().endsWith("jpg"))
                        {
                            currentJPGCount++;

                            // processSubFolders to exclude extra publishing intermediate progress in main screen
                            // needed only in change folder activity
                            if (processSubFolders && MyApplication.isShowMediaFiles())
                            {
                                publishProgress(new MediaDir(aFile, "", ""));
                            }
                        }
                        else if (aFile.isFile() && aFile.getName().toLowerCase().endsWith("mp4"))
                        {
                            currentMP4Count++;

                            // processSubFolders to exclude extra publishing intermediate progress in main screen
                            // needed only in change folder activity
                            if (processSubFolders && MyApplication.isShowMediaFiles())
                            {
                                publishProgress(new MediaDir(aFile, "", ""));
                            }
                        }
                    }

                }
            }

            return new MediaDir(params[0], String.valueOf(currentJPGCount), String.valueOf(currentMP4Count));
        }

        @Override
        protected void onPostExecute(MediaDir mediaDir)
        {
            oneFileCallback.handleFinal(mediaDir);
        }

    }
}
