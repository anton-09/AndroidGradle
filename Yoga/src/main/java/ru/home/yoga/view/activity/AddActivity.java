package ru.home.yoga.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import ru.home.yoga.MyApplication;
import ru.home.yoga.R;
import ru.home.yoga.model.EntityGeneric;
import ru.home.yoga.model.Place;
import ru.home.yoga.model.PracticeDuration;
import ru.home.yoga.model.Studio;
import ru.home.yoga.model.Type;


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
    Spinner spinnerPlace;
    EditText editTextComment;

    RadioButton radioButtonMinus;
    RadioButton radioButtonOk;
    RadioButton radioButtonPlus;


    LocalDate initialDate = new LocalDate();
    long clickedDayId;
    int initialPrice = 0;
    String initialPayType = "=";
    int initialPeople = 0;
    int initialType = 0;
    int initialDuration = 0;
    int initialStudio = 0;
    int initialPlace = 0;
    String initialComment = "";


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
        spinnerPlace = (Spinner) findViewById(R.id.spinner_place);
        editTextComment = (EditText) findViewById(R.id.edit_text_comment);
        radioButtonMinus = (RadioButton) findViewById(R.id.button_pay_type_minus);
        radioButtonOk = (RadioButton) findViewById(R.id.button_pay_type_ok);
        radioButtonPlus = (RadioButton) findViewById(R.id.button_pay_type_plus);


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
                setResult(0);
                onBackPressed();
            }
        });
    }

    public void getInitValues()
    {
        Cursor cursor;
        ArrayList arrayList;
        ArrayAdapter<EntityGeneric> arrayAdapter;

        initialStudio = getIntent().getIntExtra("studioId", 0);
        clickedDayId = getIntent().getLongExtra("clickedId", -1);
        if (clickedDayId != -1)
        {
            cursor = MyApplication.getDBAdapter().getDataById(clickedDayId);

            cursor.moveToFirst();
            if (!cursor.isAfterLast())
            {
                initialDate = MyApplication.mDbDateFormat.parseLocalDate(cursor.getString(1));
                initialPrice = cursor.getInt(2);
                initialPayType = cursor.getString(3);
                initialPeople = cursor.getInt(4);
                initialType = cursor.getInt(5);
                initialDuration = cursor.getInt(7);
                initialStudio = cursor.getInt(9);
                initialPlace = cursor.getInt(13);
                initialComment = cursor.getString(16);
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
        cursor = MyApplication.getDBAdapter().getStudiosChrono();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(new Studio(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3)));
            cursor.moveToNext();
        }
        cursor.close();
        arrayAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        spinnerStudio.setAdapter(arrayAdapter);


        arrayList = new ArrayList<>();
        cursor = MyApplication.getDBAdapter().getPlaces();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(new Place(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        arrayAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        spinnerPlace.setAdapter(arrayAdapter);
    }

    private void initComponents()
    {
        buttonDate.setText(initialDate.toString(MyApplication.mViewFullDateFormat));
        buttonDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AddActivity.this, DatePickerActivity.class);
                intent.putExtra("date", buttonDate.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_SET_DATE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (initialPayType.equals("-"))
        {
            radioButtonMinus.setChecked(true);
        }
        if (initialPayType.equals("="))
        {
            radioButtonOk.setChecked(true);
        }
        if (initialPayType.equals("+"))
        {
            radioButtonPlus.setChecked(true);
        }

        editTextPrice.setText(String.valueOf(initialPrice));
        editTextComment.setText(initialComment);


        seekBarPeople.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                textViewPeople.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
        seekBarPeople.setProgress(1);
        seekBarPeople.setProgress(initialPeople);


        spinnerType.setSelection(((MySpinnerAdapter) spinnerType.getAdapter()).getPositionById(initialType));
        spinnerDuration.setSelection(((MySpinnerAdapter) spinnerDuration.getAdapter()).getPositionById(initialDuration));
        spinnerStudio.setSelection(((MySpinnerAdapter) spinnerStudio.getAdapter()).getPositionById(initialStudio));
        spinnerStudio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (((Studio) adapterView.getItemAtPosition(i)).isGroup())
                    findViewById(R.id.seekbar_people).setEnabled(true);
                else
                    findViewById(R.id.seekbar_people).setEnabled(false);
            }

            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
        spinnerPlace.setSelection(((MySpinnerAdapter) spinnerPlace.getAdapter()).getPositionById(initialPlace));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        buttonDate.setText(data.getStringExtra("date"));
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

                initialDate = MyApplication.mViewFullDateFormat.parseLocalDate(buttonDate.getText().toString());
                initialPrice = Integer.parseInt(editTextPrice.getText().toString());
                initialPeople = seekBarPeople.getProgress();
                initialType = ((Type) spinnerType.getSelectedItem()).getEntityId();
                initialDuration = ((PracticeDuration) spinnerDuration.getSelectedItem()).getEntityId();
                initialStudio = ((Studio) spinnerStudio.getSelectedItem()).getEntityId();
                initialPlace = ((Place) spinnerPlace.getSelectedItem()).getEntityId();
                initialComment = editTextComment.getText().toString();

                if (radioButtonMinus.isChecked())
                {
                    initialPayType = "-";
                }
                if (radioButtonOk.isChecked())
                {
                    initialPayType = "=";
                }
                if (radioButtonPlus.isChecked())
                {
                    initialPayType = "+";
                }

                if (!((Studio) spinnerStudio.getSelectedItem()).isGroup())
                {
                    initialPeople = 1;
                }

                if (clickedDayId != -1)
                {
                    MyApplication.getDBAdapter().updateData(
                            clickedDayId,
                            initialDate.toString(MyApplication.mDbDateFormat),
                            initialPrice,
                            initialPayType,
                            initialPeople,
                            initialType,
                            initialDuration,
                            initialStudio,
                            initialPlace,
                            initialComment
                    );
                }
                else
                {
                    MyApplication.getDBAdapter().addData(
                            initialDate.toString(MyApplication.mDbDateFormat),
                            initialPrice,
                            initialPayType,
                            initialPeople,
                            initialType,
                            initialDuration,
                            initialStudio,
                            initialPlace,
                            initialComment
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

        MySpinnerAdapter(Context context, int textViewResourceId, ArrayList<EntityGeneric> objects)
        {
            super(context, textViewResourceId, objects);

            list = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
        {
            return getView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.spinner_item, null);
            }

            EntityGeneric data = getItem(position);

            ((TextView) convertView.findViewById(R.id.spinner_text)).setText(data.getEntityValue());

            return convertView;
        }

        int getPositionById(Integer id)
        {
            for (EntityGeneric listItem : list)
            {
                if (listItem.getEntityId().intValue() == id.intValue())
                {
                    return getPosition(listItem);
                }
            }

            return 0;
        }
    }

}