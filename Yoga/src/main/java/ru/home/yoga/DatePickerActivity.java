package ru.home.yoga;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.joda.time.LocalDate;


public class DatePickerActivity extends AppCompatActivity
{
    Toolbar toolbar;

    Intent exitIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker_activity);

        initToolbar();

        LocalDate dateDefault = MyApplication.mViewFullDateFormat.parseLocalDate(getIntent().getStringExtra("date"));

        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.material_calendar_view);
        materialCalendarView.setSelectedDate(dateDefault.toDate());
        materialCalendarView.setCurrentDate(dateDefault.toDate());
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener()
        {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected)
            {
                exitIntent.putExtra("date", LocalDate.fromDateFields(date.getDate()).toString(MyApplication.mViewFullDateFormat));
            }
        });

        exitIntent = new Intent();
        exitIntent.putExtra("date", dateDefault.toString(MyApplication.mViewFullDateFormat));
        setResult(0, exitIntent);
    }

    private void initToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
