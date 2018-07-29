package ru.home.serial;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.home.serial.api.TheTVDBApi;
import ru.home.serial.api.TheTVDBClient;
import ru.home.serial.entity.LanguageResponse;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();

    TheTVDBApi theTVDBApi;

    TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                testTextView.append("\nWazzup, dude!");
            }
        });

        theTVDBApi = TheTVDBClient.getClient().create(TheTVDBApi.class);

        testTextView = findViewById(R.id.testTextView);


        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                testTextView.append("\nNothing to do here!");
            }
        });

        Button languagesButton = findViewById(R.id.languagesButton);
        languagesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                testTextView.append("\nGetting languages!");
                languages();
            }
        });


    }

    public void languages()
    {
        Call<LanguageResponse> call = theTVDBApi.getLanguages();
        call.enqueue(new Callback<LanguageResponse>()
        {
            @Override
            public void onResponse(Call<LanguageResponse> call, Response<LanguageResponse> response)
            {
                testTextView.append("\n" + response.body().getData().length);
            }

            @Override
            public void onFailure(Call<LanguageResponse> call, Throwable t)
            {
                testTextView.append("\nSHIT HAPPENS!");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
