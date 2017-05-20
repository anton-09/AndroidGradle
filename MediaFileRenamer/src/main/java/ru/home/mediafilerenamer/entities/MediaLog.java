package ru.home.mediafilerenamer.entities;

import java.io.File;

public class MediaLog
{
    public static final int STATUS_OK = 0;
    public static final int STATUS_EQUAL = 1;
    public static final int STATUS_CANCEL = 2;

    public static final int DIR_FORWARD = 0;
    public static final int DIR_BACKWARD = 1;

    private File mFileOld;
    private File mFileNew;
    private int mStatus;
    private int mDir;

    public MediaLog(File fileOld, File fileNew, int status, int dir)
    {
        mFileOld = fileOld;
        mFileNew = fileNew;
        mStatus = status;
        mDir = dir;
    }

    public File getFileOld()
    {
        return mFileOld;
    }

    public File getFileNew()
    {
        return mFileNew;
    }

    public String getFileOldName()
    {
        return (mFileOld != null) ? mFileOld.getName() : "";
    }

    public String getFileNewName()
    {
        return (mFileNew != null) ? mFileNew.getName() : "";
    }

    public int getStatus()
    {
        return mStatus;
    }

    public int getDir()
    {
        return mDir;
    }

    public void setDir(int dir)
    {
        mDir = dir;
    }

}
