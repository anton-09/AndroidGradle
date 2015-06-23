package ru.home.serial;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;

public class SerialActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null)
        {
            SerialLoaderListFragment list = new SerialLoaderListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    public static class SerialLoaderListFragment extends ListFragment
            implements OnQueryTextListener, OnCloseListener, LoaderManager.LoaderCallbacks<Cursor>
    {

        SimpleCursorAdapter mAdapter;
        String mCurFilter;
        SearchView mSearchView;

        @Override public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            setEmptyText("No serials!");
            setHasOptionsMenu(true);

            mAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_2, null,
                    new String[] { MainTable.SERIALS_NAME, MainTable.SERIALS_SEASON_COUNT },
                    new int[] { android.R.id.text1, android.R.id.text2 }, 0);

            setListAdapter(mAdapter);

            setListShown(false);

            getLoaderManager().initLoader(0, null, this);
        }

        public static class MySearchView extends SearchView
        {
            public MySearchView(Context context)
            {
                super(context);
            }

            // The normal SearchView doesn't clear its search text when
            // collapsed, so we will do this for it.
            @Override
            public void onActionViewCollapsed()
            {
                setQuery("", false);
                super.onActionViewCollapsed();
            }
        }

        @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            MenuItem item = menu.add("Search");
            item.setIcon(android.R.drawable.ic_menu_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            mSearchView = new MySearchView(getActivity());
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnCloseListener(this);
            mSearchView.setIconifiedByDefault(true);
            item.setActionView(mSearchView);
        }

        @Override public boolean onQueryTextChange(String newText)
        {
            // Called when the action bar search text has changed.  Update
            // the search filter, and restart the loader to do a new query
            // with this filter.
            String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
            // Don't do anything if the filter hasn't actually changed.
            // Prevents restarting the loader when restoring state.
            if (mCurFilter == null && newFilter == null)
            {
                return true;
            }
            if (mCurFilter != null && mCurFilter.equals(newFilter))
            {
                return true;
            }
            mCurFilter = newFilter;
            getLoaderManager().restartLoader(0, null, this);
            return true;
        }

        @Override public boolean onQueryTextSubmit(String query)
        {
            // Don't care about this.
            return true;
        }

        @Override
        public boolean onClose()
        {
            if (!TextUtils.isEmpty(mSearchView.getQuery())) {
                mSearchView.setQuery(null, true);
            }
            return true;
        }



        @Override public void onListItemClick(ListView l, View v, int position, long id)
        {
            Intent intent = new Intent(getActivity(), EpisodeActivity.class);
            intent.putExtra("serial_id", id);
            startActivityForResult(intent, 0);
        }

        static final String[] PROJECTION = new String[]
                {
                        MainTable._ID,
                        MainTable.SERIALS_NAME,
                        MainTable.SERIALS_SEASON_COUNT,
                };

        public Loader<Cursor> onCreateLoader(int id, Bundle args)
        {
            Uri baseUri;
            if (mCurFilter != null) {
                baseUri = Uri.withAppendedPath(MainTable.CONTENT_URI_SERIAL_FILTER, Uri.encode(mCurFilter));
            } else {
                baseUri = MainTable.CONTENT_URI_SERIALS;
            }

            return new CursorLoader(getActivity(), baseUri, PROJECTION, null, null, null);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor data)
        {
            mAdapter.swapCursor(data);

            if (isResumed())
            {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        public void onLoaderReset(Loader<Cursor> loader)
        {
            mAdapter.swapCursor(null);
        }
    }
}
