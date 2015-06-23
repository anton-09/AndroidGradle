package ru.home.vertel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class SelectPackActivity extends Activity
{
    private ImagePack mImagePack;
    private GridView gridView;
    
    private static final int IDM_CLEAR = 101;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        mImagePack = getIntent().getParcelableExtra("ImagePack");


        gridView = (GridView) findViewById(R.id.grid_pack);
        GridAdapter gridAdapter = new GridAdapter(mImagePack);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                View borderView = v.findViewById(R.id.cell_border);
                if (borderView.getVisibility() == View.VISIBLE)
                {
                    borderView.setVisibility(View.INVISIBLE);
                    mImagePack.mCheckedResURIs.remove(mImagePack.mResURIs.get(position));
                }
                else
                {
                    borderView.setVisibility(View.VISIBLE);
                    mImagePack.mCheckedResURIs.add(mImagePack.mResURIs.get(position));
                }

                fillTextViews();
            }
        });

        Button buttonOk = (Button) findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mImagePack.mCheckedResURIs.size() > 0)
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("ImagePack", mImagePack);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                else
                {
                    Toast.makeText(SelectPackActivity.this, R.string.empty_skin, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        fillTextViews();

        Button showStats = (Button) findViewById(R.id.show_stat);
        showStats.setOnClickListener(new View.OnClickListener()
        {
            @Override
			public void onClick(View v)
            {
                int color;
                int runs, wins;
                float percent, chance;
                View tempView;
                TextView itemsTextView, runsTextView, winsTextView, percentTextView, chanceTextView;

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View linearLayout = inflater.inflate(R.layout.listview_container, null);
                LinearLayout view = (LinearLayout) linearLayout.findViewById(R.id.listview_container);

                // Header
                tempView = inflater.inflate(R.layout.listview_header, null);
                view.addView(tempView);

                Cursor cursor = MyApplication.getDBAdapter().getData();
                cursor.moveToFirst();

                // List
                while(!cursor.isAfterLast())
                {
                    tempView = inflater.inflate(R.layout.listview_item, null);
                    itemsTextView = (TextView) tempView.findViewById(R.id.list_item_items);
                    runsTextView = (TextView) tempView.findViewById(R.id.list_item_runs);
                    winsTextView = (TextView) tempView.findViewById(R.id.list_item_wins);
                    percentTextView = (TextView) tempView.findViewById(R.id.list_item_percent);
                    chanceTextView = (TextView) tempView.findViewById(R.id.list_item_chance);

                    runs = cursor.getInt(1);
                    wins = cursor.getInt(2);
                    percent = (runs > 0) ? wins * 100.0f / runs : 0;
                    chance = (cursor.getInt(0) == 1) ? 100.0f : 100.0f / (cursor.getInt(0)*cursor.getInt(0)-1);

                    if (runs!=0)
                    {
                        color = getResources().getColor(R.color.stat_active_text_color);
                        itemsTextView.setTextColor(color);
                        runsTextView.setTextColor(color);
                        winsTextView.setTextColor(color);
                        percentTextView.setTextColor(color);
                        chanceTextView.setTextColor(color);
                    }

                    itemsTextView.setText(cursor.getString(0));
                    runsTextView.setText("" + runs);
                    winsTextView.setText("" + wins);
                    percentTextView.setText(String.format("%.2f%%", percent));
                    chanceTextView.setText(String.format("%.2f%%", chance));

                    view.addView(tempView);
                    cursor.moveToNext();
                }

                AlertDialog.Builder adb = new AlertDialog.Builder(SelectPackActivity.this);
                adb.setView(linearLayout);
                adb.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, IDM_CLEAR, 0, R.string.menu_clear);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == IDM_CLEAR)
        {
            MyApplication.getDBAdapter().reset();
            return true;
        }
        return false;
    }
    
    public void fillTextViews()
    {
        TextView chosenView = (TextView) findViewById(R.id.string_chosen);
        chosenView.setText(String.format("%s: %d", getResources().getString(R.string.chosen), mImagePack.mCheckedResURIs.size()));

        TextView percentView = (TextView) findViewById(R.id.string_percent);
        percentView.setText(String.format("%s: %.2f%%", getResources().getString(R.string.chance), mImagePack.getWinPercent()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            MyApplication.zoomIn();
            GridAdapter gridAdapter = new GridAdapter(mImagePack);
            gridView.setAdapter(gridAdapter);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            MyApplication.zoomOut();
            GridAdapter gridAdapter = new GridAdapter(mImagePack);
            gridView.setAdapter(gridAdapter);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
