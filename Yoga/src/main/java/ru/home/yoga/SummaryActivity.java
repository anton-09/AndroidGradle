package ru.home.yoga;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import ru.home.yoga.entity.SummaryItem;

public class SummaryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    Toolbar toolbar;
    ViewPager viewPager;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_activity);

        initToolbar();

        initTabs();

        getLoaderManager().initLoader(0, null, SummaryActivity.this);
    }

    private void initToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });
    }

    private void initTabs()
    {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabPagerFragmentAdapter tabPagerFragmentAdapter = new TabPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerFragmentAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(this)
        {
            public Cursor loadInBackground()
            {
                return MyApplication.getDBAdapter().getSummary();
            }
        };
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        TreeMap<String, ArrayList<SummaryItem>> summaryItemTreeMap = new TreeMap<String, ArrayList<SummaryItem>>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            String key = cursor.getString(0x0);
            ArrayList<SummaryItem> list = summaryItemTreeMap.get(key);
            if (list == null)
            {
                ArrayList<SummaryItem> value = new ArrayList<>();
                value.add(new SummaryItem(cursor.getString(1), cursor.getInt(2), cursor.getInt(3)));
                summaryItemTreeMap.put(key, value);
            }
            else
            {
                list.add(new SummaryItem(cursor.getString(1), cursor.getInt(2), cursor.getInt(3)));
            }
            cursor.moveToNext();
        }
        for (Map.Entry<String, ArrayList<SummaryItem>> entry : summaryItemTreeMap.entrySet())
        {
            ((TabPagerFragmentAdapter) viewPager.getAdapter()).addTab((String) entry.getKey(), (ArrayList) entry.getValue());
        }
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setCurrentItem(viewPager.getAdapter().getCount());
    }

    public void onLoaderReset(Loader<Cursor> loader)
    {
    }
}
