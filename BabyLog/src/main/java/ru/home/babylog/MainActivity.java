package ru.home.babylog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE_ADD_DATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                startActivityForResult(intentBackup, REQUEST_CODE_ADD_DATA);
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
