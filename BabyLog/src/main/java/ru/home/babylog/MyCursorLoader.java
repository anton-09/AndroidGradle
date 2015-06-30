package ru.home.babylog;

import android.content.CursorLoader;
import android.database.Cursor;

class MyCursorLoader extends CursorLoader
{
    public MyCursorLoader()
    {
        super(MyApplication.getAppContext());
    }

    @Override
    public Cursor loadInBackground()
    {
        return MyApplication.getDBAdapter().getData();
    }
}
