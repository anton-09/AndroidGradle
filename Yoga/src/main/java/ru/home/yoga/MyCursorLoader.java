package ru.home.yoga;

import android.content.CursorLoader;
import android.database.Cursor;

class MyCursorLoader extends CursorLoader
{
    int mStudioId = 0;
    String mPrevDate;
    long mPrevId = -1;

    public MyCursorLoader(String prevDate, long prevId)
    {
        super(MyApplication.getAppContext());

        mPrevDate = prevDate;
        mPrevId = prevId;
    }

    public MyCursorLoader(String prevDate, long prevId, int id)
    {
        super(MyApplication.getAppContext());

        mPrevDate = prevDate;
        mPrevId = prevId;
        mStudioId = id;
    }

    public MyCursorLoader()
    {
        super(MyApplication.getAppContext());
    }

    @Override
    public Cursor loadInBackground()
    {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (mStudioId > 0)
            return MyApplication.getDBAdapter().getPagedDataByStudioId(mPrevDate, mPrevId, mStudioId, MyApplication.ITEMS_PER_PAGE);

        if (mPrevId > -1)
            return MyApplication.getDBAdapter().getPagedData(mPrevDate, mPrevId, MyApplication.ITEMS_PER_PAGE);

        return MyApplication.getDBAdapter().getBackupData();
    }
}
