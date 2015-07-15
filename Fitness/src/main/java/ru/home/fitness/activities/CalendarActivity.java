package ru.home.fitness.activities;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;
import ru.home.fitness.entities.Action;

public class CalendarActivity extends AppCompatActivity
{
    HashMap<String, List<Action>> hashMap = new HashMap<String, List<Action>>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

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

        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendar_view);
        materialCalendarView.addDecorator(new OneDayDecorator());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0; i < 42; i++)
        {

        }
    }

    public class OneDayDecorator implements DayViewDecorator
    {

        private CalendarDay currentDay;
        private final Drawable highlightDrawable;

        public OneDayDecorator()
        {
            currentDay = CalendarDay.today();
            highlightDrawable = generateBackgroundDrawable();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day)
        {
            return currentDay.isAfter(day) || currentDay.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view)
        {
            view.setBackgroundDrawable(highlightDrawable);
        }

        private Drawable generateBackgroundDrawable()
        {
            final int color = Color.parseColor("#22FF0000");

            ArcShape arcShape = new ArcShape(30, 215);

            ShapeDrawable drawable = new ShapeDrawable(arcShape);
            drawable.setShaderFactory(new ShapeDrawable.ShaderFactory()
            {
                @Override
                public Shader resize(int width, int height)
                {
                    return new LinearGradient(0, 0, 0, 0, color, color, Shader.TileMode.REPEAT);
                }
            });
            return drawable;
        }
    }
}


