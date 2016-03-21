package ru.home.yoga;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

        Date dateDefault = new Date(getIntent().getLongExtra("date", -1));

        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.material_calendar_view);
        materialCalendarView.setSelectedDate(dateDefault);
        materialCalendarView.setCurrentDate(dateDefault);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                exitIntent.putExtra("date", date.getDate().getTime());
            }
        });

        exitIntent = new Intent();
        exitIntent.putExtra("date", dateDefault.getTime());
        setResult(0, exitIntent);
    }

    private void initToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Workaround to get WHITE back arrow for pre-lollipop devices!!!
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            Drawable backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            backArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
