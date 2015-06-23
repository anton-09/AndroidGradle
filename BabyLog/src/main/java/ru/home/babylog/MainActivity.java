package ru.home.babylog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MainActivity extends Activity
{
    private static final int REQUEST_CODE_ADD_DATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.main);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            try
            {
                new File(sdDir + "/test.file").createNewFile();
            } catch (IOException e)
            {
                Toast.makeText(this, "Проблема с записью файла на SD-карту!", Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(this, "Внимание! SD-карта не доступна, бэкап не будет работать!", Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(this, AddActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_DATA);
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
