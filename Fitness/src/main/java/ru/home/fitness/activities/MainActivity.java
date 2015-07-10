package ru.home.fitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch (item.getItemId())
        {
            case R.id.menu_day_night:
                MyApplication.toggleTheme();
                recreate();
                return true;

            case R.id.menu_muscle:
                intent = new Intent(this, MuscleActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_exercise:
                intent = new Intent(this, ExerciseActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_workout:
                intent = new Intent(this, WorkoutActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
