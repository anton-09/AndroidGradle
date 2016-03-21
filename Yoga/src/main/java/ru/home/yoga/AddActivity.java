package ru.home.yoga;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import ru.home.yoga.entity.EntityGeneric;
import ru.home.yoga.entity.PracticeDuration;
import ru.home.yoga.entity.Studio;
import ru.home.yoga.entity.Type;


public class AddActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE_SET_DATE = 1;

    Toolbar toolbar;
    Button buttonDate;
    EditText editTextPrice;
    SeekBar seekBarPeople;
    TextView textViewPeople;
    Spinner spinnerType;
    Spinner spinnerDuration;
    Spinner spinnerStudio;

    Date initialDate = new Date();
    long clickedDayId;
    int initialPrice = 0;
    int initialPeople = 0;
    int initialType = 0;
    int initialDuration = 0;
    int initialStudio = 0;



    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        buttonDate = (Button) findViewById(R.id.button_calendar);
        editTextPrice = (EditText) findViewById(R.id.edit_text_price);
        seekBarPeople = (SeekBar) findViewById(R.id.seekbar_people);
        textViewPeople = (TextView) findViewById(R.id.text_view_people);
        spinnerType = (Spinner) findViewById(R.id.spinner_type);
        spinnerDuration = (Spinner) findViewById(R.id.spinner_duration);
        spinnerStudio = (Spinner) findViewById(R.id.spinner_studio);

        initToolbar();
        getInitValues();
        initComponents();
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
            Drawable backArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            backArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }
    }

    public void getInitValues()
    {
        Cursor cursor;
        ArrayList arrayList;
        ArrayAdapter<EntityGeneric> arrayAdapter;


        clickedDayId = getIntent().getLongExtra("clickedId", -1);
        if (clickedDayId != -1)
        {
            cursor = MyApplication.getDBAdapter().getDataById(clickedDayId);

            cursor.moveToFirst();
            if (!cursor.isAfterLast())
            {
                try
                {
                    initialDate = MyApplication.mDbDateFormat.parse(cursor.getString(1));
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


        arrayList = new ArrayList<>();
        cursor = MyApplication.getDBAdapter().getTypes();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(new Type(cursor.getInt(0), cursor.getString(1)));
            cursor.moveToNext();
        }

        cursor.close();
        arrayAdapter = new MySpinnerAdapter(this, 0, arrayList);
        spinnerType.setAdapter(arrayAdapter);


        arrayList = new ArrayList<>();
        cursor = MyApplication.getDBAdapter().getDurations();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(new PracticeDuration(cursor.getInt(0), cursor.getDouble(1)));
            cursor.moveToNext();
        }
        cursor.close();
        arrayAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        spinnerDuration.setAdapter(arrayAdapter);


        arrayList = new ArrayList<>();
        cursor = MyApplication.getDBAdapter().getStudios();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(new Studio(cursor.getInt(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();
        arrayAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        spinnerStudio.setAdapter(arrayAdapter);
    }

    private void initComponents()
    {
        buttonDate.setText(MyApplication.mViewFullDateFormat.format(initialDate));
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, DatePickerActivity.class);
                try {
                    intent.putExtra("date", MyApplication.mViewFullDateFormat.parse(buttonDate.getText().toString()).getTime());
                } catch (ParseException ignored) {
                }
                startActivityForResult(intent, REQUEST_CODE_SET_DATE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        editTextPrice.setText(String.valueOf(initialPrice));


        seekBarPeople.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewPeople.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarPeople.setProgress(1);
        seekBarPeople.setProgress(initialPeople);


        spinnerType.setSelection(((MySpinnerAdapter) spinnerType.getAdapter()).getPositionById(initialType));
        spinnerDuration.setSelection(((MySpinnerAdapter) spinnerDuration.getAdapter()).getPositionById(initialDuration));
        spinnerStudio.setSelection(((MySpinnerAdapter) spinnerStudio.getAdapter()).getPositionById(initialStudio));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        buttonDate.setText(MyApplication.mViewFullDateFormat.format(new Date(data.getLongExtra("date", -1))));
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

                try
                {
                    initialDate = MyApplication.mViewFullDateFormat.parse(buttonDate.getText().toString());
                }
                catch (ParseException ignored)
                {
                }
                initialPrice = Integer.parseInt(editTextPrice.getText().toString());
                initialPeople = seekBarPeople.getProgress();
                initialType = ((Type) spinnerType.getSelectedItem()).getId();
                initialDuration = ((PracticeDuration) spinnerDuration.getSelectedItem()).getId();
                initialStudio = ((Studio) spinnerStudio.getSelectedItem()).getId();

                if (clickedDayId != -1)
                {
                    MyApplication.getDBAdapter().updateData(
                            clickedDayId,
                            MyApplication.mDbDateFormat.format(initialDate),
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
                            MyApplication.mDbDateFormat.format(initialDate),
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
                convertView = getLayoutInflater().inflate(R.layout.spinner_item, null);
            }

            EntityGeneric data = getItem(position);

            ((TextView) convertView.findViewById(R.id.spinner_text)).setText(data.getEntityValue());

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