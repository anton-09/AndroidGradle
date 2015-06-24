package ru.home.babylog;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>
{
    SimpleDateFormat backupDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    BackupListItemAdapter arrayAdapter;
    File backupStorage;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_backup);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, R.string.backup_no_sd, Toast.LENGTH_LONG).show();
            return;
        }

        backupStorage = new File(android.os.Environment.getExternalStorageDirectory(), "/BabyLog");

        if (!backupStorage.exists())
            backupStorage.mkdirs();

        arrayAdapter = new BackupListItemAdapter(this, backupStorage.list());
        ((ListView)findViewById(R.id.listview_backup)).setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.backup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_add:
                getLoaderManager().initLoader(0, null, this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new MainListFragment.MyCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        File fileName = new File(backupStorage, backupDateFormat.format(new Date()) + ".csv");

        try
        {
            FileWriter f = new FileWriter(fileName);

            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                String output = cursor.getString(1) + ";" + cursor.getInt(2) + ";" + cursor.getString(3) + ";" + cursor.getString(4) + ";" + cursor.getString(5) + "\n";
                f.write(output);
                cursor.moveToNext();
            }

            f.flush();
            f.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this, R.string.backup_write_error, Toast.LENGTH_LONG).show();
            fileName.delete();
        }

        arrayAdapter = new BackupListItemAdapter(this, backupStorage.list());
        ((ListView)findViewById(R.id.listview_backup)).setAdapter(arrayAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    private class BackupListItemAdapter extends ArrayAdapter<String>
    {

        public BackupListItemAdapter(Context context, String[] items)
        {
            super(context, R.layout.backup_list_item, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.backup_list_item, null);
            }

            ((TextView) convertView.findViewById(R.id.listItemBackupDate)).setText(getItem(position));

            return convertView;
        }
    }
}
