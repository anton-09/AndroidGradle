package ru.home.yoga.model.db;

import android.content.CursorLoader;
import android.database.Cursor;

import ru.home.yoga.MyApplication;

public class MyCursorLoader extends CursorLoader
{
    private int mStudioId = 0;
    private String mPrevDate;
    private long mPrevId = -1;

    public MyCursorLoader(String prevDate, long prevId, int studioId)
    {
        super(MyApplication.getAppContext());

        mPrevDate = prevDate;
        mPrevId = prevId;
        mStudioId = studioId;
    }

    public MyCursorLoader()
    {
        super(MyApplication.getAppContext());
    }

    @Override
    public Cursor loadInBackground()
    {
        try
        {
            Thread.sleep(50);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        if (mStudioId > 0)
        {
            return MyApplication.getDBAdapter().getPagedDataByStudioId(mPrevDate, mPrevId, mStudioId, MyApplication.ITEMS_PER_PAGE);
        }

        if (mPrevId > -1)
        {
            return MyApplication.getDBAdapter().getPagedData(mPrevDate, mPrevId, MyApplication.ITEMS_PER_PAGE);
        }

        return MyApplication.getDBAdapter().getBackupData();
    }
}
