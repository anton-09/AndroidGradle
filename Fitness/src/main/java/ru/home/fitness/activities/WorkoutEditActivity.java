package ru.home.fitness.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;
import ru.home.fitness.entities.Exercise;
import ru.home.fitness.entities.Muscle;

public class WorkoutEditActivity extends AppCompatActivity
{
    private Long mClickedId;
    private String mInitialName = "";
    private String mInitialStartDate = "";
    private String mInitialEndDate = "";
    private List<Long> mInitialExerciseList;

    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_edit_activity);

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

        mInitialExerciseList = new ArrayList<Long>();
        Cursor cursor;
        mClickedId = getIntent().getLongExtra("clickedId", -1);
        if (mClickedId != -1)
        {
            cursor = MyApplication.getDBAdapter().getWorkoutDataById(mClickedId);
            cursor.moveToFirst();
            mInitialName = cursor.getString(1);
            try { mInitialStartDate = MyApplication.viewDateFormat.format(MyApplication.dbDateFormat.parse(cursor.getString(2))); }
            catch (ParseException e) { }
            try { mInitialEndDate = MyApplication.viewDateFormat.format(MyApplication.dbDateFormat.parse(cursor.getString(3))); }
            catch (ParseException e) { }


            while (!cursor.isAfterLast())
            {
                mInitialExerciseList.add(cursor.getLong(4));
                cursor.moveToNext();
            }
        }

        ((EditText) findViewById(R.id.workout_name_edit_text)).setText(mInitialName);

        final Button startDateButton = ((Button) findViewById(R.id.workout_start_date_button));
        startDateButton.setText(mInitialStartDate);
        startDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final MaterialCalendarView materialCalendarView = (MaterialCalendarView) getLayoutInflater().inflate(R.layout.calendar, null);
                try
                {
                    materialCalendarView.setSelectedDate(MyApplication.viewDateFormat.parse(startDateButton.getText().toString()));
                }
                catch (ParseException e)
                {
                    materialCalendarView.setSelectedDate(new Date());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutEditActivity.this);
                builder
                        .setView(materialCalendarView)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                startDateButton.setText(MyApplication.viewDateFormat.format(materialCalendarView.getSelectedDate().getDate()));
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                            }
                        })
                        .setNeutralButton(R.string.reset, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                startDateButton.setText("");
                            }
                        })
                        .show();
            }
        });

        final Button endDateButton = ((Button) findViewById(R.id.workout_end_date_button));
        endDateButton.setText(mInitialEndDate);
        endDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final MaterialCalendarView materialCalendarView = (MaterialCalendarView) getLayoutInflater().inflate(R.layout.calendar, null);
                try
                {
                    materialCalendarView.setSelectedDate(MyApplication.viewDateFormat.parse(endDateButton.getText().toString()));
                }
                catch (ParseException e)
                {
                    materialCalendarView.setSelectedDate(new Date());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutEditActivity.this);
                builder
                        .setView(materialCalendarView)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                endDateButton.setText(MyApplication.viewDateFormat.format(materialCalendarView.getSelectedDate().getDate()));
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                            }
                        })
                        .setNeutralButton(R.string.reset, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                endDateButton.setText("");
                            }
                        })
                        .show();
            }
        });

        ArrayList<Exercise> exerciseArrayList = new ArrayList<Exercise>();
        Cursor exerciseCursor = MyApplication.getDBAdapter().getExerciseData();
        exerciseCursor.moveToFirst();
        while (!exerciseCursor.isAfterLast())
        {
            exerciseArrayList.add(new Exercise(exerciseCursor.getLong(0), exerciseCursor.getString(1), new Muscle(exerciseCursor.getLong(2), exerciseCursor.getString(3))));
            exerciseCursor.moveToNext();
        }

        ListViewAdapter listViewAdapter = new ListViewAdapter(this, exerciseArrayList);
        ListView listView = (ListView) findViewById(R.id.workout_list_view);
        listView.setAdapter(listViewAdapter);


        for (int i = 0; i < listView.getCount(); i++)
        {
            if (mInitialExerciseList.contains(((Exercise)listView.getItemAtPosition(i)).getId()))
                listView.setItemChecked(i, true);
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

                EditText editText = (EditText) findViewById(R.id.workout_name_edit_text);
                Button startDateButton = (Button) findViewById(R.id.workout_start_date_button);
                Button endDateButton = (Button) findViewById(R.id.workout_end_date_button);
                ListView listView = (ListView) findViewById(R.id.workout_list_view);
                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
                ArrayList<Long> checkedIds = new ArrayList<Long>();

                for(int i = 0; i< checkedItemPositions.size();i++)
                {
                    if(checkedItemPositions.valueAt(i))
                    {
                        checkedIds.add(((Exercise)listView.getAdapter().getItem(checkedItemPositions.keyAt(i))).getId());
                    }
                }

                String startDateString = "";
                String endDateString = "";

                try { startDateString = MyApplication.dbDateFormat.format(MyApplication.viewDateFormat.parse(startDateButton.getText().toString())); }
                catch (ParseException e) { }

                try { endDateString = MyApplication.dbDateFormat.format(MyApplication.viewDateFormat.parse(endDateButton.getText().toString())); }
                catch (ParseException e) { }


                if (mClickedId != -1)
                {
                    MyApplication.getDBAdapter().updateWorkoutData
                            (
                                    mClickedId,
                                    editText.getText().toString(),
                                    startDateString,
                                    endDateString,
                                    checkedIds
                            );
                }
                else
                {
                    MyApplication.getDBAdapter().addWorkoutData
                            (
                                    editText.getText().toString(),
                                    startDateString,
                                    endDateString,
                                    checkedIds
                            );
                }

                setResult(RESULT_OK, new Intent());
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class ListViewAdapter extends ArrayAdapter<Exercise>
    {

        public ListViewAdapter(Context context, ArrayList<Exercise> objects)
        {
            super(context, R.layout.list_view_item, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.list_view_item, null);
            }

            Exercise data = getItem(position);

            ((TextView) convertView.findViewById(R.id.exercise_name_text_view)).setText(data.getName());
            ((TextView) convertView.findViewById(R.id.muscle_name_text_view)).setText(data.getMuscle().getName());

            return convertView;
        }

    }

}
