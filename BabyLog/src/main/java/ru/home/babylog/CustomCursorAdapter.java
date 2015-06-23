package ru.home.babylog;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;


public class CustomCursorAdapter extends CursorAdapter
{
    private LayoutInflater mInflater;

    public CustomCursorAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return mInflater.inflate(R.layout.main_list_item, parent, false);
    }

}