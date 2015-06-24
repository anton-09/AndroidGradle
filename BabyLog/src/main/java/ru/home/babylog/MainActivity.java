package ru.home.babylog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity
{
    private static final int REQUEST_CODE_ADD_DATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
            case R.id.menu_add:
                Intent intentAdd = new Intent(this, AddActivity.class);
                startActivityForResult(intentAdd, REQUEST_CODE_ADD_DATA);
                return true;

            case R.id.menu_backup:
                Intent intentBackup = new Intent(this, BackupActivity.class);
                startActivity(intentBackup);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        getFragmentManager().findFragmentById(R.id.mainlist_fragment).getLoaderManager().getLoader(0).forceLoad();
    }
}
