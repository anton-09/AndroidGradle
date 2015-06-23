package ru.home.serial;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class EpisodeActivity extends Activity
{
    static Long serialId;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        serialId = getIntent().getLongExtra("serial_id", -1);

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        Uri baseUri;
        baseUri = Uri.withAppendedPath(MainTable.CONTENT_URI_SERIALS, Long.toString(serialId));

        final String[] PROJECTION1 = new String[]
                {
                        MainTable.SERIALS_SEASON_COUNT,
                };
        Cursor cursor = getContentResolver().query(baseUri, PROJECTION1, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            int seasonCount = cursor.getInt(0);

            for (int i=1; i<=seasonCount; i++)
            {
                actionBar.addTab(actionBar.newTab()
                        .setText("season " + i)
                        .setTabListener(new TabListener<EpisodeLoaderListFragment>(EpisodeActivity.this, Integer.toString(i), EpisodeLoaderListFragment.class)));
            }

            cursor.close();
        }

        if (savedInstanceState != null)
        {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener
    {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(Activity activity, String tag, Class<T> clz)
        {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args)
        {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached())
            {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft)
        {
            if (mFragment == null)
            {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft)
        {
            if (mFragment != null)
            {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft)
        {
        }
    }

    public static class EpisodeLoaderListFragment extends ListFragment
            implements LoaderManager.LoaderCallbacks<Cursor>
    {
        SimpleCursorAdapter mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            setEmptyText("No episodes!");
            setHasOptionsMenu(true);

            mAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_2, null,
                    new String[]{MainTable.EPISODES_NAME, MainTable.EPISODES_NUMBER},
                    new int[]{android.R.id.text1, android.R.id.text2}, 0);

            setListAdapter(mAdapter);

            setListShown(false);

            getLoaderManager().initLoader(0, null, this);
        }

        @Override public void onListItemClick(ListView l, View v, int position, long id)
        {
        }

        static final String[] PROJECTION = new String[]
                {
                        MainTable._ID,
                        MainTable.EPISODES_NAME,
                        MainTable.EPISODES_SEASON,
                        MainTable.EPISODES_NUMBER,
                        MainTable.EPISODES_SERIAL_ID,
                };

        public Loader<Cursor> onCreateLoader(int id, Bundle args)
        {
            Uri baseUri;
            baseUri = Uri.withAppendedPath(MainTable.CONTENT_ID_URI_EPISODES, Long.toString(serialId));
            baseUri = Uri.withAppendedPath(baseUri, getTag());

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
