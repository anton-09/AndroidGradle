package ru.home.fitness.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;

public class MuscleEditActivity extends AppCompatActivity
{
    private Long mClickedId;
    private String mInitialName = "";

    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.muscle_edit_activity);

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
            cursor = MyApplication.getDBAdapter().getMuscleDataById(mClickedId);
            cursor.moveToFirst();
            mInitialName = cursor.getString(1);
        }

        ((EditText) findViewById(R.id.muscle_name_edit_text)).setText(mInitialName);
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

                EditText muscleNameEditText = (EditText) findViewById(R.id.muscle_name_edit_text);

                if (mClickedId != -1)
                {
                    MyApplication.getDBAdapter().updateMuscleData
                            (
                                    mClickedId,
                                muscleNameEditText.getText().toString()
                            );
                }
                else
                {
                    MyApplication.getDBAdapter().addMuscleData
                            (
                                    muscleNameEditText.getText().toString()
                            );
                }

                setResult(RESULT_OK, new Intent());
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
