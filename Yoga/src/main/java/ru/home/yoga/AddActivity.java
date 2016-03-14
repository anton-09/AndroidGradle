package ru.home.yoga;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.home.yoga.entity.EntityGeneric;
import ru.home.yoga.entity.PracticeDuration;
import ru.home.yoga.entity.Studio;
import ru.home.yoga.entity.Type;


public class AddActivity extends AppCompatActivity
{
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyyMMdd");
    private Long clickedDayId;
    private Date initialDate = new Date();
    private Integer initialPrice = 0;
    private Integer initialPeople = 0;
    private Integer initialType = 0;
    private Integer initialDuration = 0;
    private Integer initialStudio = 0;

    Toolbar toolbar;

    MaterialCalendarView materialCalendarView;
    EditText editTextPrice;
    EditText editTextPeople;
    Spinner spinnerType;
    Spinner spinnerDuration;
    Spinner spinnerStudio;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        initToolbar();

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendar_view);
        editTextPrice = (EditText) findViewById(R.id.edit_text_price);
        editTextPeople = (EditText) findViewById(R.id.edit_text_people);
        spinnerType = (Spinner) findViewById(R.id.spinner_type);
        spinnerDuration = (Spinner) findViewById(R.id.spinner_duration);
        spinnerStudio = (Spinner) findViewById(R.id.spinner_studio);

        Cursor cursor;

        clickedDayId = getIntent().getLongExtra("clickedId", -1);
        if (clickedDayId != -1)
        {
            cursor = MyApplication.getDBAdapter().getDataById(clickedDayId);

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

                initialPrice = cursor.getInt(2);
                initialPeople = cursor.getInt(3);
                initialType = cursor.getInt(4);
                initialDuration = cursor.getInt(6);
                initialStudio = cursor.getInt(8);
            }
            cursor.close();
        }

        editTextPrice.setText(String.valueOf(initialPrice));
        editTextPeople.setText(String.valueOf(initialPeople));

        getSpinnerData();

        spinnerType.setSelection(((MySpinnerAdapter) spinnerType.getAdapter()).getPositionById(initialType));
        spinnerDuration.setSelection(((MySpinnerAdapter) spinnerDuration.getAdapter()).getPositionById(initialDuration));
        spinnerStudio.setSelection(((MySpinnerAdapter) spinnerStudio.getAdapter()).getPositionById(initialStudio));


        materialCalendarView.setSelectedDate(initialDate);
        materialCalendarView.setCalendarDisplayMode(CalendarMode.WEEKS);
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

        // Workaround to get WHITE back arrow for pre-lollipop devices!!!
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            Drawable backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            backArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }
    }

    private void getSpinnerData()
    {
        Cursor cursor;
        Type type;
        PracticeDuration duration;
        Studio studio;
        ArrayList arrayList;
        ArrayAdapter<EntityGeneric> arrayAdapter;

        arrayList = new ArrayList<Type>();
        cursor = MyApplication.getDBAdapter().getTypes();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            type = new Type(cursor.getInt(0), cursor.getString(1));
            arrayList.add(type);
            cursor.moveToNext();
        }

        cursor.close();
        arrayAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(arrayAdapter);


        arrayList = new ArrayList<PracticeDuration>();
        cursor = MyApplication.getDBAdapter().getDurations();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            duration = new PracticeDuration(cursor.getInt(0), cursor.getDouble(1));
            arrayList.add(duration);
            cursor.moveToNext();
        }
        cursor.close();
        arrayAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(arrayAdapter);


        arrayList = new ArrayList<Studio>();
        cursor = MyApplication.getDBAdapter().getStudios();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            studio = new Studio(cursor.getInt(0), cursor.getString(1));
            arrayList.add(studio);
            cursor.moveToNext();
        }
        cursor.close();
        arrayAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudio.setAdapter(arrayAdapter);
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

                initialDate = materialCalendarView.getSelectedDate().getDate();
                initialPrice = Integer.parseInt(editTextPrice.getText().toString());
                initialPeople = Integer.parseInt(editTextPeople.getText().toString());
                initialType = ((Type) spinnerType.getSelectedItem()).getId();
                initialDuration = ((PracticeDuration) spinnerDuration.getSelectedItem()).getId();
                initialStudio = ((Studio) spinnerStudio.getSelectedItem()).getId();

                if (clickedDayId != -1)
                {
                    MyApplication.getDBAdapter().updateData(
                            clickedDayId,
                            dbDateFormat.format(initialDate),
                            initialPrice,
                            initialPeople,
                            initialType,
                            initialDuration,
                            initialStudio
                    );
                }
                else
                {
                    MyApplication.getDBAdapter().addData(
                            dbDateFormat.format(initialDate),
                            initialPrice,
                            initialPeople,
                            initialType,
                            initialDuration,
                            initialStudio
                    );
                }

                setResult(RESULT_OK, new Intent());
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MySpinnerAdapter extends ArrayAdapter<EntityGeneric>
    {
        ArrayList<EntityGeneric> list;

        public MySpinnerAdapter(Context context, int textViewResourceId, ArrayList<EntityGeneric> objects)
        {
            super(context, textViewResourceId, objects);
            list = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            return getView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_spinner_item, null);
            }

            EntityGeneric data = getItem(position);

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(data.getEntityValue());

            return convertView;
        }

        public int getPositionById(Integer id)
        {
            for (EntityGeneric listItem : list)
            {
                if (listItem.getEntityId().intValue() == id.intValue())
                    return getPosition(listItem);
            }

            return 0;
        }
    }

}