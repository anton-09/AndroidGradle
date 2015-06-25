package ru.home.babylog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class AddActivity extends AppCompatActivity
{
    SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyyMMdd");
    Long clickedDayId;
    Date initialDate = new Date(0);
    int initialWeight = 0;
    String initialEat = "";
    String initialFeed = "";
    String initialComments = "";


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Cursor cursor;

        clickedDayId = getIntent().getLongExtra("clickedId", -1);
        if (clickedDayId != -1) {
            cursor = MyApplication.getDBAdapter().getDataById(clickedDayId);
        } else {
            cursor = MyApplication.getDBAdapter().getLastData();
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            try {
                initialDate = dbDateFormat.parse(cursor.getString(1));
            } catch (ParseException e) {
            }

            initialWeight = cursor.getInt(2);

            if (clickedDayId != -1) {
                initialEat = cursor.getString(3);
                initialFeed = cursor.getString(4);
                initialComments = cursor.getString(5);
            }
            else
            {
                Calendar c = Calendar.getInstance();
                c.setTime(initialDate);
                c.add(Calendar.DATE, 1);
                initialDate = c.getTime();
            }
        }

        prepareIntWheel(R.id.wheelKilo10, initialWeight / 10000);
        prepareIntWheel(R.id.wheelKilo10, initialWeight / 10000);
        prepareIntWheel(R.id.wheelKilo1, (initialWeight - initialWeight / 10000 * 10000) / 1000);
        prepareIntWheel(R.id.wheelGramm100, (initialWeight - initialWeight / 1000 * 1000) / 100);
        prepareIntWheel(R.id.wheelGramm10, (initialWeight - initialWeight / 100 * 100) / 10);
        prepareIntWheel(R.id.wheelGramm1, initialWeight - initialWeight / 10 * 10);
        prepareDateWheel(R.id.wheelDay, R.id.wheelMonth, R.id.wheelYear, initialDate.getTime());

        ((TextView) findViewById(R.id.editEat)).setText(initialEat);
        ((TextView) findViewById(R.id.editFeed)).setText(initialFeed);
        ((TextView) findViewById(R.id.editComments)).setText(initialComments);

        ImageButton confirmDataButton = (ImageButton) findViewById(R.id.buttonConfirm);
        ImageButton cancelDataButton = (ImageButton) findViewById(R.id.buttonCancel);

        if (clickedDayId != -1)
        {
            cancelDataButton.setVisibility(View.VISIBLE);
            findViewById(R.id.wheelDay).setEnabled(false);
            findViewById(R.id.wheelMonth).setEnabled(false);
            findViewById(R.id.wheelYear).setEnabled(false);
        }
        else
        {
            cancelDataButton.setVisibility(View.GONE);
            findViewById(R.id.wheelDay).setEnabled(true);
            findViewById(R.id.wheelMonth).setEnabled(true);
            findViewById(R.id.wheelYear).setEnabled(true);
        }

        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WheelView wheelViewYear = (WheelView) findViewById(R.id.wheelYear);
                WheelView wheelViewMonth = (WheelView) findViewById(R.id.wheelMonth);
                WheelView wheelViewDay = (WheelView) findViewById(R.id.wheelDay);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 10 + wheelViewYear.getCurrentItem());
                calendar.set(Calendar.MONTH, wheelViewMonth.getCurrentItem());
                calendar.set(Calendar.DAY_OF_MONTH, wheelViewDay.getCurrentItem() + 1);

                WheelView wheelViewKilo10 = (WheelView) findViewById(R.id.wheelKilo10);
                WheelView wheelViewKilo1 = (WheelView) findViewById(R.id.wheelKilo1);
                WheelView wheelViewGramm100 = (WheelView) findViewById(R.id.wheelGramm100);
                WheelView wheelViewGramm10 = (WheelView) findViewById(R.id.wheelGramm10);
                WheelView wheelViewGramm1 = (WheelView) findViewById(R.id.wheelGramm1);

                int weight = wheelViewKilo10.getCurrentItem() * 10000 + wheelViewKilo1.getCurrentItem() * 1000 + wheelViewGramm100.getCurrentItem() * 100 + wheelViewGramm10.getCurrentItem() * 10 + wheelViewGramm1.getCurrentItem();

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
                        return;
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
            }
        });

        cancelDataButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder
                        .setMessage(R.string.delete)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                MyApplication.getDBAdapter().deleteData(clickedDayId);

                                setResult(RESULT_OK, new Intent());
                                finish();

                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                            }
                        })
                        .show();
            }
        });
    }

    public void prepareIntWheel(int wheelId, int value)
    {
        WheelView wheelView = (WheelView) findViewById(wheelId);
        wheelView.setViewAdapter(new NumericWheelAdapter(this, 0, 9));
        wheelView.setVisibleItems(2);
        wheelView.setCurrentItem(value);
        wheelView.setCyclic(true);
    }

    public void prepareDateWheel(int wheelIdDay, int wheelIdMonth, int wheelIdYear, long value)
    {
        final WheelView wheelViewDay = (WheelView) findViewById(wheelIdDay);
        final WheelView wheelViewMonth = (WheelView) findViewById(wheelIdMonth);
        final WheelView wheelViewYear = (WheelView) findViewById(wheelIdYear);

        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
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

        // year
        int initialYear = calendar.get(Calendar.YEAR);
        wheelViewYear.setViewAdapter(new NumericWheelAdapter(this, curYear - 10, curYear + 10));
        wheelViewYear.setVisibleItems(2);
        wheelViewYear.setCurrentItem(initialYear + 10 - curYear);
        wheelViewYear.addChangingListener(listener);

        //day
        updateDays(wheelViewYear, wheelViewMonth, wheelViewDay);
        wheelViewDay.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        wheelViewDay.setVisibleItems(2);
    }

    void updateDays(WheelView year, WheelView month, WheelView day)
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