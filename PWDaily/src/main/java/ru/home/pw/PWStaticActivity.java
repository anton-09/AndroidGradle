package ru.home.pw;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PWStaticActivity extends Activity implements View.OnClickListener {

    private static final int IDM_FIND_UNFINISHED = 101;
    private static final int IDM_SHOW_LEGEND = 102;

    private DataModel mDataModel;
    private DailyDbAdapter mDailyDbAdapter;
    private long mTodayDate;
    private static final DateFormat mDateFormat = new SimpleDateFormat("dd.MM");
    private static final long ONE_DAY = 24 * 60 * 60 * 1000L;

    TextView leftButton, center_button, rightButton;

    Typeface mTypeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		setContentView(R.layout.grid);

        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");

        mTodayDate = getMoscowDate();

        leftButton = (TextView) findViewById(R.id.left_button);
        center_button = (TextView) findViewById(R.id.center_button);
        rightButton = (TextView) findViewById(R.id.right_button);

        leftButton.setOnClickListener(this);
        center_button.setOnClickListener(this);
        rightButton.setOnClickListener(this);


        mDailyDbAdapter = new DailyDbAdapter(this);
        mDailyDbAdapter.open();

        mDataModel = new DataModel(mDailyDbAdapter, this);

        createView();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mDailyDbAdapter.close();
    }

    @Override
    public void onClick(View view) {
        Log.d("PWDaily","Activity::onClick");
        if (view.getTag().equals("left"))
            mTodayDate -= ONE_DAY;

        if (view.getTag().equals("right"))
            mTodayDate += ONE_DAY;
        
        if (view.getTag().equals("center"))
            mTodayDate = getMoscowDate();

        createView();
    }

    private long getMoscowDate()
    {
        Calendar c = Calendar.getInstance();
        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();
        if(z.inDaylightTime(new Date())){
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        c.add(Calendar.MINUTE, (-offsetMins));

        c.add(Calendar.HOUR_OF_DAY, 4);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    private void createView()
    {
        GridView gridView = (GridView) findViewById(R.id.grid_day);

        leftButton.setTypeface(mTypeface);
        center_button.setTypeface(mTypeface);
        rightButton.setTypeface(mTypeface);

        leftButton.setText("<< " + mDateFormat.format(mTodayDate - ONE_DAY));
        center_button.setText(mDateFormat.format(mTodayDate));
        rightButton.setText(mDateFormat.format(mTodayDate + ONE_DAY) + " >>");

        if (mTodayDate == getMoscowDate())
            center_button.setEnabled(false);
        else
            center_button.setEnabled(true);


        mDataModel.setDate(mTodayDate);
        mDataModel.fillCharsFromCursor();
        mDataModel.fillEventsFromCursor();
        mDataModel.fillCharsEventsFromCursor();
        mDataModel.fillStoragesFromCursor();

        GridAdapter gridAdapter = new GridAdapter(this, mDataModel);

        gridView.setNumColumns(mDataModel.mChars.size() + 1);
        gridView.setAdapter(gridAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, IDM_FIND_UNFINISHED, Menu.NONE, R.string.menu_item_find_unfinished);
        menu.add(Menu.NONE, IDM_SHOW_LEGEND, Menu.NONE, R.string.menu_item_show_legend);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
        StringBuilder message = new StringBuilder();
        if (item.getItemId() == IDM_FIND_UNFINISHED)
        {
            Cursor findUnifinishedCursor = mDailyDbAdapter.getStorageUnfinished();
            findUnifinishedCursor.moveToFirst();
            while(!findUnifinishedCursor.isAfterLast())
            {
                message.append(mDateFormat.format(findUnifinishedCursor.getLong(1)));
                message.append(" | ");
                message.append(mDataModel.mCharsEvents.get(findUnifinishedCursor.getInt(0)).mChar.mName);
                message.append(" ");
                message.append(mDataModel.mCharsEvents.get(findUnifinishedCursor.getInt(0)).mEvent.mName);
                message.append("\n");

                findUnifinishedCursor.moveToNext();
            }

            findUnifinishedCursor.close();

            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
            return true;
        }
        if (item.getItemId() == IDM_SHOW_LEGEND)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.listview_container, null);

            DataModel.Char c;
            for(int i = 0; i < mDataModel.mChars.size(); i++)
            {
            	c = mDataModel.mChars.valueAt(i);
                View tempView = inflater.inflate(R.layout.listview_item, null);
                TextView itemName = (TextView) tempView.findViewById(R.id.list_item_name);
                TextView itemLevel = (TextView) tempView.findViewById(R.id.list_item_level);
                ImageView itemImage = (ImageView) tempView.findViewById(R.id.list_item_image);

                int resID = getResources().getIdentifier(c.mPicture, "drawable", getPackageName());
                itemName.setTypeface(mTypeface);
                itemLevel.setTypeface(mTypeface);

                itemName.setText(c.mName);
                itemLevel.setText(c.mLevel.toString());
                itemImage.setBackgroundResource(resID);

                view.addView(tempView);
            }

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setView(view);
            adb.show();

            return true;
        }

        return false;
    }
}
