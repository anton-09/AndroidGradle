package ru.home.housing;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int MENU_DELETE_ID = 1;
    SimpleCursorAdapter simpleCursorAdapter;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

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

        String[] from = new String[] {HousingDbAdapter.HOUSING_DATE, HousingDbAdapter.HOUSING_COLD, HousingDbAdapter.HOUSING_HOT, HousingDbAdapter.HOUSING_ELECTRICITY};
        int[] to = new int[] { R.id.textDate, R.id.textCold, R.id.textHot, R.id.textElectricity };

        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, from, to, 0);

        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder()
        {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex)
            {
                if (columnIndex == 1)
                {
                    ((TextView) view).setText(simpleDateFormat.format(new Date(cursor.getLong(columnIndex))));
                    return true;
                }
                return false;
            }
        });

        setListAdapter(simpleCursorAdapter);

        registerForContextMenu(getListView());

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MENU_DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (item.getItemId() == MENU_DELETE_ID)
        {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            MyApplication.getDBAdapter().deleteData(acmi.id);
            getLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new MyCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        simpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
