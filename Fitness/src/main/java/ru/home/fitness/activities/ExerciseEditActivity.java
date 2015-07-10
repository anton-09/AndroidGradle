package ru.home.fitness.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;
import ru.home.fitness.entities.Muscle;

public class ExerciseEditActivity extends AppCompatActivity
{
    private Long mClickedId;
    private String mInitialName = "";
    private Long mInitialMuscleId = 0L;

    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_edit_activity);

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
        mClickedId = getIntent().getLongExtra("clickedId", -1);
        if (mClickedId != -1)
        {
            cursor = MyApplication.getDBAdapter().getExerciseDataById(mClickedId);
            cursor.moveToFirst();
            mInitialName = cursor.getString(1);
            mInitialMuscleId = cursor.getLong(2);
        }

        ((EditText) findViewById(R.id.exercise_name_edit_text)).setText(mInitialName);


        ArrayList<Muscle> muscleArrayList = new ArrayList<Muscle>();
        muscleArrayList.add(new Muscle(0L, "--------"));

        Cursor muscleCursor = MyApplication.getDBAdapter().getMuscleData();
        muscleCursor.moveToFirst();
        while (!muscleCursor.isAfterLast())
        {
            muscleArrayList.add(new Muscle(muscleCursor.getLong(0), muscleCursor.getString(1)));
            muscleCursor.moveToNext();
        }

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, muscleArrayList);
        Spinner spinner = (Spinner) findViewById(R.id.exercise_muscle_spinner);
        spinner.setAdapter(spinnerAdapter);

        if (mInitialMuscleId == 0)
            spinner.setSelection(0);
        else
        {
            int i = 0;
            while (i < spinner.getCount())
            {
                if (((Muscle) spinner.getItemAtPosition(i)).getId() == mInitialMuscleId)
                {
                    spinner.setSelection(i);
                    break;
                }
                i++;
            }

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

                EditText editText = (EditText) findViewById(R.id.exercise_name_edit_text);
                Spinner spinner = (Spinner) findViewById(R.id.exercise_muscle_spinner);

                if (mClickedId != -1)
                {
                    MyApplication.getDBAdapter().updateExerciseData
                            (
                                    mClickedId,
                                    editText.getText().toString(),
                                    ((Muscle)spinner.getSelectedItem()).getId()
                            );
                }
                else
                {
                    MyApplication.getDBAdapter().addExerciseData
                            (
                                    editText.getText().toString(),
                                    ((Muscle)spinner.getSelectedItem()).getId()
                            );
                }

                setResult(RESULT_OK, new Intent());
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class SpinnerAdapter extends ArrayAdapter<Muscle>
    {

        public SpinnerAdapter(Context context, ArrayList<Muscle> objects)
        {
            super(context, R.layout.spinner_item, objects);
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

            Muscle data = getItem(position);

            ((TextView) convertView.findViewById(R.id.spinner_item_text)).setText(data.getName());

            return convertView;
        }

    }

}
