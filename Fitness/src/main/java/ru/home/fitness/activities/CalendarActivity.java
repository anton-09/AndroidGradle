package ru.home.fitness.activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;

public class CalendarActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object>
{
    static MaterialCalendarView materialCalendarView;
    static HashMap<CalendarDay, Float> hashMap = new HashMap<CalendarDay, Float>();
    static Calendar calendar = Calendar.getInstance();

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

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendar_view);
        materialCalendarView.addDecorator(new OneDayDecorator());
        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener()
        {
            @Override
            public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay)
            {
                Log.e("CalendarActivity", "onMonthChanged calendar");
                calendar.set(Calendar.MONTH, calendarDay.getMonth());
                getLoaderManager().getLoader(0).forceLoad();
            }
        });

        calendar.setTime(materialCalendarView.getCurrentDate().getDate());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e("CalendarActivity", "onResume activity");
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.e("CalendarActivity", "onPause activity");
        getLoaderManager().getLoader(0).cancelLoad();
    }

    @Override
    public Loader<Object> onCreateLoader(int i, Bundle bundle)
    {
        Log.e("CalendarActivity", "onCreateLoader");
        MyTaskLoader myTaskLoader = new MyTaskLoader(this);
        myTaskLoader.forceLoad();
        return myTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object cursor)
    {
        Log.e("CalendarActivity", "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Object> loader)
    {
        Log.e("CalendarActivity", "onLoaderReset");
    }

    public static class MyTaskLoader extends AsyncTaskLoader<Object>
    {
        WeakReference<CalendarActivity> mActivity;

        public MyTaskLoader(CalendarActivity activity)
        {
            super(activity);
            Log.e("CalendarActivity", "MyTaskLoader constructor");
            mActivity = new WeakReference<CalendarActivity>(activity);
        }

        @Override
        public Object loadInBackground()
        {
            int doneTasks;
            int unDoneTasks;

            hashMap.clear();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int daysCountInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            Log.e("CalendarActivity", "" + daysCountInMonth);
            Log.e("CalendarActivity", "" + calendar.get(Calendar.MONTH));

            for (int i = 0; i < daysCountInMonth; i++)
            {
                doneTasks = 0;
                unDoneTasks = 0;

                if (this.isLoadInBackgroundCanceled())
                    return null;

                Cursor dayDataCursor = MyApplication.getDBAdapter().getActionData(MyApplication.dbDateFormat.format(calendar.getTime()));
                dayDataCursor.moveToFirst();

                while (!dayDataCursor.isAfterLast())
                {
                    try
                    {
                        MyApplication.dbDateFormat.parse(dayDataCursor.getString(1));
                        doneTasks++;
                    }
                    catch (Exception e)
                    {
                        unDoneTasks++;
                    }

                    dayDataCursor.moveToNext();
                }

                if (doneTasks > 0)
                {
                    hashMap.put(CalendarDay.from(calendar), doneTasks * 1.0f / (doneTasks + unDoneTasks));
                }

                Log.e("CalendarActivity", "i = " + i + " Day = " + CalendarDay.from(calendar) + " Done = " + doneTasks + " Undone = " + unDoneTasks);

                mActivity.get().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        materialCalendarView.invalidateDecorators();
                    }
                });

                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            return null;
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
            return hashMap.containsKey(day);
            //return currentDay.isAfter(day) || currentDay.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view)
        {
            view.setBackgroundDrawable(highlightDrawable);
        }

        private Drawable generateBackgroundDrawable()
        {
            final int color = Color.parseColor("#22FF0000");

            ArcShape arcShape = new ArcShape(0, 360);

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


