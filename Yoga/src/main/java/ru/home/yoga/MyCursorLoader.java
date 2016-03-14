package ru.home.yoga;

import android.content.CursorLoader;
import android.database.Cursor;

class MyCursorLoader extends CursorLoader
{
    int studioId = 0;

    public MyCursorLoader()
    {
        super(MyApplication.getAppContext());
    }

    public MyCursorLoader(int id)
    {
        super(MyApplication.getAppContext());
        studioId = id;
    }

    @Override
    public Cursor loadInBackground()
    {
        if (studioId == 0)
            return MyApplication.getDBAdapter().getData();
        else
            return MyApplication.getDBAdapter().getDataByStudioId(studioId);
    }
}
