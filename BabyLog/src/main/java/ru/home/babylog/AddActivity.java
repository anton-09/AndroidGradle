package ru.home.babylog;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class AddActivity extends AppCompatActivity
{
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyyMMdd");
    private Long clickedDayId;
    private Date initialDate = new Date(0);
    private int initialWeight = 0;
    private String initialEat = "";
    private String initialFeed = "";
    private String initialComments = "";


    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        Cursor cursor;

        clickedDayId = getIntent().getLongExtra("clickedId", -1);
        if (clickedDayId != -1)
        {
            cursor = MyApplication.getDBAdapter().getDataById(clickedDayId);
        }
        else
        {
            cursor = MyApplication.getDBAdapter().getLastData();
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast())
        {
            try
            {
                initialDate = dbDateFormat.parse(cursor.getString(1));
            }
            catch (ParseException ignored)
            {
            }

            initialWeight = cursor.getInt(2);

            if (clickedDayId != -1)
            {
                initialEat = cursor.getString(3);
                initialFeed = cursor.getString(4);
            }
            else
            {
                Calendar c = Calendar.getInstance();
                c.setTime(initialDate);
                c.add(Calendar.DATE, 1);
                initialDate = c.getTime();
            }

            initialComments = cursor.getString(5);
        }

        prepareIntWheel(R.id.wheelKilo10, initialWeight / 10000);
        prepareIntWheel(R.id.wheelKilo10, initialWeight / 10000);
        prepareIntWheel(R.id.wheelKilo1, (initialWeight - initialWeight / 10000 * 10000) / 1000);
        prepareIntWheel(R.id.wheelGram100, (initialWeight - initialWeight / 1000 * 1000) / 100);
        prepareIntWheel(R.id.wheelGram10, (initialWeight - initialWeight / 100 * 100) / 10);
        prepareIntWheel(R.id.wheelGram1, initialWeight - initialWeight / 10 * 10);
        prepareDateWheel(initialDate.getTime());

        ((EditText) findViewById(R.id.editEat)).setText(initialEat);
        ((EditText) findViewById(R.id.editFeed)).setText(initialFeed);
        ((EditText) findViewById(R.id.editComments)).setText(initialComments);

        if (clickedDayId != -1)
        {
            findViewById(R.id.wheelDay).setEnabled(false);
            findViewById(R.id.wheelMonth).setEnabled(false);
            findViewById(R.id.wheelYear).setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_confirm:
                WheelView wheelViewYear = (WheelView) findViewById(R.id.wheelYear);
                WheelView wheelViewMonth = (WheelView) findViewById(R.id.wheelMonth);
                WheelView wheelViewDay = (WheelView) findViewById(R.id.wheelDay);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 10 + wheelViewYear.getCurrentItem());
                calendar.set(Calendar.MONTH, wheelViewMonth.getCurrentItem());
                calendar.set(Calendar.DAY_OF_MONTH, wheelViewDay.getCurrentItem() + 1);

                WheelView wheelViewKilo10 = (WheelView) findViewById(R.id.wheelKilo10);
                WheelView wheelViewKilo1 = (WheelView) findViewById(R.id.wheelKilo1);
                WheelView wheelViewGram100 = (WheelView) findViewById(R.id.wheelGram100);
                WheelView wheelViewGram10 = (WheelView) findViewById(R.id.wheelGram10);
                WheelView wheelViewGram1 = (WheelView) findViewById(R.id.wheelGram1);

                int weight = wheelViewKilo10.getCurrentItem() * 10000 + wheelViewKilo1.getCurrentItem() * 1000 + wheelViewGram100.getCurrentItem() * 100 + wheelViewGram10.getCurrentItem() * 10 + wheelViewGram1.getCurrentItem();

                EditText eatEditText = (EditText) findViewById(R.id.editEat);
                EditText feedEditText = (EditText) findViewById(R.id.editFeed);
                EditText commentsText = (EditText) findViewById(R.id.editComments);

                if (clickedDayId != -1)
                {
                    MyApplication.getDBAdapter().updateData(
                            clickedDayId,
                            dbDateFormat.format(calendar.getTime()),
                            weight,
                            eatEditText.getText().toString(),
                            feedEditText.getText().toString(),
                            commentsText.getText().toString());
                }
                else
                {
                    if (MyApplication.getDBAdapter().getDataByDate(dbDateFormat.format(calendar.getTime())).getCount() > 0)
                    {
                        Toast.makeText(AddActivity.this, "Запись в такой датой уже существует", Toast.LENGTH_LONG).show();
                        return true;
                    }

                    MyApplication.getDBAdapter().addData(
                            dbDateFormat.format(calendar.getTime()),
                            weight,
                            eatEditText.getText().toString(),
                            feedEditText.getText().toString(),
                            commentsText.getText().toString());
                }

                setResult(RESULT_OK, new Intent());
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void prepareIntWheel(int wheelId, int value)
    {
        WheelView wheelView = (WheelView) findViewById(wheelId);
        wheelView.setViewAdapter(new NumericWheelAdapter(this, 0, 9));
        wheelView.setVisibleItems(2);
        wheelView.setCurrentItem(value);
        wheelView.setCyclic(true);
        ((AbstractWheelTextAdapter)wheelView.getViewAdapter()).setTextSize(20);
        wheelView.setLightTheme(MyApplication.getCurrentTheme() == R.style.AppTheme_Light);
    }

    private void prepareDateWheel(long value)
    {
        final WheelView wheelViewDay = (WheelView) findViewById(R.id.wheelDay);
        final WheelView wheelViewMonth = (WheelView) findViewById(R.id.wheelMonth);
        final WheelView wheelViewYear = (WheelView) findViewById(R.id.wheelYear);

        OnWheelChangedListener listener = new OnWheelChangedListener()
        {
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                updateDays(wheelViewYear, wheelViewMonth, wheelViewDay);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(value);
        // month
        int initialMonth = calendar.get(Calendar.MONTH);
        String months[] = getResources().getStringArray(R.array.months);
        wheelViewMonth.setViewAdapter(new ArrayWheelAdapter<String>(this, months));
        wheelViewMonth.setVisibleItems(2);
        wheelViewMonth.setCurrentItem(initialMonth);
        wheelViewMonth.addChangingListener(listener);
        ((AbstractWheelTextAdapter)wheelViewMonth.getViewAdapter()).setTextSize(20);
        wheelViewMonth.setLightTheme(MyApplication.getCurrentTheme() == R.style.AppTheme_Light);


        // year
        int initialYear = calendar.get(Calendar.YEAR);
        wheelViewYear.setViewAdapter(new NumericWheelAdapter(this, curYear - 10, curYear + 10));
        wheelViewYear.setVisibleItems(2);
        wheelViewYear.setCurrentItem(initialYear + 10 - curYear);
        wheelViewYear.addChangingListener(listener);
        ((AbstractWheelTextAdapter)wheelViewYear.getViewAdapter()).setTextSize(20);
        wheelViewYear.setLightTheme(MyApplication.getCurrentTheme() == R.style.AppTheme_Light);

        //day
        updateDays(wheelViewYear, wheelViewMonth, wheelViewDay);
        wheelViewDay.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        wheelViewDay.setVisibleItems(2);
        ((AbstractWheelTextAdapter)wheelViewDay.getViewAdapter()).setTextSize(20);
        wheelViewDay.setLightTheme(MyApplication.getCurrentTheme() == R.style.AppTheme_Light);
    }

    private void updateDays(WheelView year, WheelView month, WheelView day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new NumericWheelAdapter(this, 1, maxDays));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

}