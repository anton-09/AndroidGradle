package ru.home.techarch.techarch;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;


public class TechArchActivity extends Activity
{
    private static final String ARG_SELECTED_MODE = "selected_mode";
    private static final String ARG_SELECTED_NAVIGATION_DRAWER_ITEM = "selected_navigation_drawer_item";

    private static final String MODE_PHONES = "phones";
    private static final String MODE_CAMERAS = "cameras";

    private ListView mDrawerList;
    private DrawerLayout mDrawer;
    private CustomActionBarDrawerToggle mDrawerToggle;

    CharSequence mTitle;

    public int mCurrentSelectedNavigationDrawerPosition = 0;
    public String mCurrentMode = MODE_PHONES;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("TechArchActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_arch);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerList = (ListView) findViewById(R.id.drawer);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setAdapter(new ArrayAdapter<String>(getActionBar().getThemedContext(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, getAvailableJSONs()));


        mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawer);
        mDrawer.setDrawerListener(mDrawerToggle);

        if (savedInstanceState != null)
        {
            mCurrentMode = savedInstanceState.getString(ARG_SELECTED_MODE);
            mCurrentSelectedNavigationDrawerPosition = savedInstanceState.getInt(ARG_SELECTED_NAVIGATION_DRAWER_ITEM);
        }

        selectItem(mCurrentSelectedNavigationDrawerPosition, savedInstanceState != null);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        Log.d("TechArchActivity", "onPostCreate");

        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Log.d("TechArchActivity", "onConfigurationChanged");

        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void selectItem(int selectedItem, boolean fromSavedInstanceState)
    {
        Log.d("TechArchActivity", "selectItem");

        mCurrentSelectedNavigationDrawerPosition = selectedItem;
        mDrawerList.setItemChecked(selectedItem, true);
        mDrawer.closeDrawer(mDrawerList);

        mTitle = mDrawerList.getItemAtPosition(mDrawerList.getCheckedItemPosition()).toString();
        getActionBar().setTitle(mTitle);

        if (!fromSavedInstanceState)
        {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ContainerFragment.newInstance(mTitle.toString()),"container_fragment")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d("TechArchActivity", "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.tech_arch, menu);

        if (mDrawer != null && mDrawer.isDrawerOpen(mDrawerList))
        {
            getActionBar().setTitle(R.string.app_name);
        }
        else
        {
            getActionBar().setTitle(mTitle);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Log.d("TechArchActivity", "onPrepareOptionsMenu");

        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawer.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.menu_phones).setVisible(!drawerOpen);
        menu.findItem(R.id.menu_cameras).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d("TechArchActivity", "onOptionsItemSelected");

		// The action bar home/up should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        // Handle your other action bar items...
        if (item.getTitle().equals(getString(R.string.phones)))
        {
            mCurrentMode = MODE_PHONES;
            try
            {
                ((ContainerFragment)getFragmentManager().findFragmentByTag("container_fragment")).parseJSON(true);
            } catch (JSONException e) { e.printStackTrace(); }


            return true;
        }
        else if (item.getTitle().equals(getString(R.string.cameras)))
        {
            mCurrentMode = MODE_CAMERAS;
            try
            {
                ((ContainerFragment)getFragmentManager().findFragmentByTag("container_fragment")).parseJSON(true);
            } catch (JSONException e) { e.printStackTrace(); }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d("TechArchActivity", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(ARG_SELECTED_MODE, mCurrentMode);
        outState.putInt(ARG_SELECTED_NAVIGATION_DRAWER_ITEM, mCurrentSelectedNavigationDrawerPosition);
    }


    private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle
    {
        public CustomActionBarDrawerToggle(Activity mActivity, DrawerLayout mDrawerLayout)
        {
            super(
                    mActivity,
                    mDrawerLayout,
                    R.drawable.ic_drawer,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }

        @Override
        public void onDrawerClosed(View view)
        {
            Log.d("CustActBarDrawTog", "onDrawerClosed");
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }

        @Override
        public void onDrawerOpened(View drawerView)
        {
            Log.d("CustActBarDrawTog", "onDrawerOpened");
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectItem(position, false);
        }

    }

    public String [] getAvailableJSONs()
    {
        Log.d("TechArchActivity", "getAvailableJSONs");
        try
        {
            return getAssets().list("json");
        }
        catch (IOException e)
        {
            return new String[]{};
        }
    }

}
