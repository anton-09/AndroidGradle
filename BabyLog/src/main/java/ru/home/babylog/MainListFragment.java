package ru.home.babylog;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int REQUEST_CODE_ADD_DATA = 1;

    EntityAdapter entityAdapter;
    SimpleDateFormat viewDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyyMMdd");

    ArrayList<ActivityItem> mList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.list_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        entityAdapter = new EntityAdapter(getActivity());
        setListAdapter(entityAdapter);

        registerForContextMenu(getListView());

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        Intent intent = new Intent(getActivity(), AddActivity.class);
        intent.putExtra("clickedId", ((ActivityItem)(((ListView) v).getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position))).mId);
        startActivityForResult(intent, REQUEST_CODE_ADD_DATA);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new MyCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        ActivityItem activityItem;
        Date date;
        mList = new ArrayList<ActivityItem>();

        data.moveToFirst();
        while(!data.isAfterLast())
        {
            try
            {
                date = dbDateFormat.parse(data.getString(1));
            }
            catch (ParseException e)
            {
                date = new Date(0);
            }

            activityItem = new ActivityItem(data.getLong(0), viewDateFormat.format(date), data.getInt(2), data.getString(3), data.getString(4), data.getString(5));
            mList.add(activityItem);
            data.moveToNext();
        }

        entityAdapter.setDataModel(mList);
        entityAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        getFragmentManager().findFragmentById(R.id.mainlist_fragment).getLoaderManager().getLoader(0).forceLoad();
    }

    static class MyCursorLoader extends CursorLoader
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
}
