package ru.home.housing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements AddFragment.onAddEventListener
{
    private static final int REQUEST_CODE_ADD_DATA = 1;
    private boolean mDualFragments = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.main);

        AddFragment frag = (AddFragment) getFragmentManager().findFragmentById(R.id.add_fragment);
        if (frag != null) mDualFragments = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (mDualFragments)
        {
            menu.removeItem(R.id.menu_add);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_add:
                Intent intent = new Intent(this, AddActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_DATA);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void NotifyAddEvent()
    {
        getFragmentManager().findFragmentById(R.id.mainlist_fragment).getLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        getFragmentManager().findFragmentById(R.id.mainlist_fragment).getLoaderManager().getLoader(0).forceLoad();
    }
}
