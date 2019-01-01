package ru.home.yoga;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import ru.home.yoga.entity.PracticeDuration;
import ru.home.yoga.entity.Studio;
import ru.home.yoga.entity.Type;
import ru.home.yoga.entity.YogaItem;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener
{
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_ADD_DATA = 1;
    private static final int REQUEST_CODE_BACKUP = 2;
    private static final int REQUEST_CODE_SUMMARY = 3;

    private MainRecyclerViewAdapter mainRecyclerViewAdapter;

    private ArrayList<YogaItem> mList = new ArrayList<YogaItem>();

    LinearLayoutManager mLinearLayoutManager;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String mInitDate = LocalDate.now().toString(MyApplication.mDbDateFormat);
    long mInitId = 0;
    boolean mIsLoading = false;
    boolean mIsLastPage = false;
    int mTotalItemCount;
    int mLastVisibleItemPosition;
    int mVisibleThreshold = 5;
    int mSelectedStudioId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_coordinator_layout_activity);

        initToolbar();
        initNavigationView();
        initRecyclerView();

        Bundle bundle = new Bundle();
        bundle.putInt("StudioId", mSelectedStudioId);
        bundle.putString("PrevDate", mInitDate);
        bundle.putLong("PrevId", mInitId);
        getLoaderManager().initLoader(0, bundle, MainActivity.this);
    }

    private void initToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavigationView()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_view_open, R.string.navigation_view_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();

        Cursor studioCursor = MyApplication.getDBAdapter().getStudios();
        studioCursor.moveToFirst();
        while (!studioCursor.isAfterLast())
        {
            menu.add(Menu.NONE, studioCursor.getInt(0), Menu.NONE, studioCursor.getString(1));
            studioCursor.moveToNext();
        }
        studioCursor.close();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                drawerLayout.closeDrawers();

                toolbar.setTitle(item.getTitle());

                mSelectedStudioId = item.getItemId();

                Bundle bundle = new Bundle();
                bundle.putInt("StudioId", mSelectedStudioId);
                bundle.putString("PrevDate", mInitDate);
                bundle.putLong("PrevId", mInitId);
                getLoaderManager().restartLoader(0, bundle, MainActivity.this);

                return true;
            }
        });

    }

    private void initRecyclerView()
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleRecyclerViewDivider(this));

        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(this, this);
        recyclerView.setAdapter(mainRecyclerViewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                mTotalItemCount = mLinearLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();

                if (!mIsLoading && !mIsLastPage &&
                        ((mLastVisibleItemPosition + mVisibleThreshold) >= mTotalItemCount))
                //                        mTotalItemCount >= MyApplication.ITEMS_PER_PAGE
                {
                    Log.i(TAG, "initRecyclerView LastVisible = " + mLastVisibleItemPosition + ", Total = " + mTotalItemCount);
                    loadMoreItems();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentAdd = new Intent(MainActivity.this, AddActivity.class);
                intentAdd.putExtra("studioId", mSelectedStudioId);
                startActivityForResult(intentAdd, REQUEST_CODE_ADD_DATA);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_summary:
                Intent intentSummary = new Intent(this, SummaryActivity.class);
                startActivityForResult(intentSummary, REQUEST_CODE_SUMMARY);
                return true;

            case R.id.menu_backup:
                Intent intentBackup = new Intent(this, BackupActivity.class);
                startActivityForResult(intentBackup, REQUEST_CODE_BACKUP);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_CANCELED)
        {
            return;
        }

        // Fill Navigation Drawer with studios
        if (requestCode == REQUEST_CODE_BACKUP)
        {
            initNavigationView();
        }

        Bundle bundle = new Bundle();
        bundle.putInt("StudioId", mSelectedStudioId);
        bundle.putString("PrevDate", mInitDate);
        bundle.putLong("PrevId", mInitId);
        getLoaderManager().restartLoader(0, bundle, MainActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        Log.i(TAG, "onCreateLoader id = " + i + ", prevDate = " + bundle.getString("PrevDate") + ", prevId = " + bundle.getLong("PrevId") + ", studioId = " + bundle.getInt("StudioId"));
        return new MyCursorLoader(bundle.getString("PrevDate"), bundle.getLong("PrevId"), bundle.getInt("StudioId"));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.i(TAG, "onLoadFinished loaderId = " + loader.getId());

        mIsLastPage = data.getCount() < MyApplication.ITEMS_PER_PAGE;

        if (mIsLoading)
        {
            Log.i(TAG, "onLoadFinished set mIsLoading FALSE");
            mIsLoading = false;
            mList.remove(mList.size() - 1);
            mainRecyclerViewAdapter.notifyItemRemoved(mList.size());
        }
        else
        {
            mLinearLayoutManager.scrollToPosition(0);
            mList.clear();
            mainRecyclerViewAdapter.setDataModel(mList);
            mainRecyclerViewAdapter.notifyDataSetChanged();
        }

        YogaItem yogaItem;

        data.moveToFirst();
        while (!data.isAfterLast())
        {
            yogaItem = new YogaItem(
                    data.getLong(0),
                    MyApplication.mDbDateFormat.parseLocalDate(data.getString(1)).toString(MyApplication.mViewShortDateFormat),
                    data.getInt(2),
                    data.getString(3),
                    data.getInt(4),
                    new Type(data.getInt(5), data.getString(6)),
                    new PracticeDuration(data.getInt(7), data.getDouble(8)),
                    new Studio(data.getInt(9), data.getString(10), data.getInt(11)),
                    data.getString(12));

            mList.add(yogaItem);
            mainRecyclerViewAdapter.notifyItemInserted(mList.size());
            data.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    @Override
    public void recyclerViewListClicked(int position)
    {
        Intent intentEdit = new Intent(this, AddActivity.class);
        intentEdit.putExtra("clickedId", mList.get(position).getId());
        startActivityForResult(intentEdit, REQUEST_CODE_ADD_DATA);
    }

    @Override
    public void recyclerViewListLongClicked(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(getString(R.string.delete))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        MyApplication.getDBAdapter().deleteData(mList.get(position).getId());
                        mList.remove(position);
                        mainRecyclerViewAdapter.notifyItemRemoved(position);
                        mTotalItemCount = mLinearLayoutManager.getItemCount();
                        mLastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                        Log.i(TAG, "onDelete LastVisible = " + mLastVisibleItemPosition + ", Total = " + mTotalItemCount + ", mIsLoading = " + mIsLoading + ", mIsLastPage = " + mIsLastPage);
                        if (!mIsLoading && !mIsLastPage && ((mLastVisibleItemPosition + mVisibleThreshold) >= mTotalItemCount))
                        {
                            loadMoreItems();
                        }

                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                    }
                })
                .show();
    }


    public void loadMoreItems()
    {
        mIsLoading = true;

        Log.i(TAG, "loadMoreItems mIsLoading = " + mIsLoading);

        mList.add(null);
        mainRecyclerViewAdapter.notifyItemInserted(mList.size() - 1);

        Bundle bundle = new Bundle();
        bundle.putString("PrevDate", MyApplication.mViewShortDateFormat.parseLocalDate(mList.get((mList.size() - 2)).getDate()).toString(MyApplication.mDbDateFormat));
        bundle.putLong("PrevId", mList.get(mList.size() - 2).getId());
        bundle.putInt("StudioId", mSelectedStudioId);
        getLoaderManager().restartLoader(0, bundle, MainActivity.this);
    }
}
