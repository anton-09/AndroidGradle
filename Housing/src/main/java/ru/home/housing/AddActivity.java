package ru.home.housing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class AddActivity extends Activity implements AddFragment.onAddEventListener
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
    }

    @Override
    public void NotifyAddEvent()
    {
        setResult(RESULT_OK, new Intent());
        finish();
    }
}