package ru.home.serial;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;


public class SerialEditActivity extends SherlockFragmentActivity
{
    private static final int IDM_CANCEL = 101;

    private EditText mNameText;
    private EditText mAltNameText;
    private EditText mColumnNumber;
    private CheckBox mIsEpisodeWithName;
    private CheckBox mIsActive;
    private Spinner mReleaseStudioNameText;
    private Spinner mTranslateStudioNameText;

    private Long mClickedSerialId;

    private boolean mSave;

    @Override
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.serial_edit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mNameText = (EditText) findViewById(R.id.edit_serial_name);
        mAltNameText = (EditText) findViewById(R.id.edit_serial_alt_name);
        mColumnNumber = (EditText) findViewById(R.id.edit_serial_column_number);
        mIsEpisodeWithName = (CheckBox) findViewById(R.id.edit_serial_is_episode_with_name);
        mIsActive = (CheckBox) findViewById(R.id.edit_serial_is_active);
        mReleaseStudioNameText = (Spinner) findViewById(R.id.edit_serial_release_studio_name);
        mTranslateStudioNameText = (Spinner) findViewById(R.id.edit_serial_translate_studio_name);


        ArrayList<StudioGeneric> releaserArrayList = new ArrayList<StudioGeneric>();
        StudioGeneric releaser = new StudioGeneric(getResources().getString(R.string.empty_date), null);
        releaserArrayList.add(releaser);

        Cursor cursor = MyApplication.getDBAdapter().getReleasers();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            releaser = new StudioGeneric(cursor.getString(1), cursor.getString(2));
            releaserArrayList.add(releaser);
            cursor.moveToNext();
        }
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, releaserArrayList);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        mReleaseStudioNameText.setAdapter(spinnerAdapter);

        ArrayList<StudioGeneric> translatorArrayList = new ArrayList<StudioGeneric>();
        StudioGeneric translator = new StudioGeneric(getResources().getString(R.string.empty_date), null);
        translatorArrayList.add(translator);

        cursor = MyApplication.getDBAdapter().getTranslators();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            translator = new StudioGeneric(cursor.getString(1), cursor.getString(2));
            translatorArrayList.add(translator);
            cursor.moveToNext();
        }
        spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, translatorArrayList);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        mTranslateStudioNameText.setAdapter(spinnerAdapter);



        mClickedSerialId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID);
        if (mClickedSerialId == null) {
            mClickedSerialId = getIntent().getLongExtra(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, -1);
        }
        Log.e("FUCK", "SerialEditActivity.onCreate(): Serial=" + mClickedSerialId);

        mSave = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(Menu.NONE, IDM_CANCEL, Menu.NONE, R.string.cancel)
                .setIcon(R.drawable.ab_cancel)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case IDM_CANCEL:
                mSave = false;
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateFields()
    {
        String name, altname;
        int release, translate;
        Log.e("FUCK", "SerialEditActivity.populateFields(): Serial=" + mClickedSerialId);
        if (mClickedSerialId != -1)
        {
            Cursor serialDetails = MyApplication.getDBAdapter().getSerialDetails(mClickedSerialId);
            serialDetails.moveToFirst();
            name = serialDetails.getString(1);
            altname = serialDetails.getString(2);
            release = serialDetails.isNull(7) ? 0 : serialDetails.getInt(7);
            translate = serialDetails.isNull(8) ? 0 : serialDetails.getInt(8);

            mNameText.setText(name);
            mAltNameText.setText(altname);
            mColumnNumber.setText(serialDetails.getString(5));
            mIsEpisodeWithName.setChecked(serialDetails.getInt(4) == 1);
            mIsActive.setChecked(serialDetails.getInt(6) == 1);
            mReleaseStudioNameText.setSelection(release);
            mTranslateStudioNameText.setSelection(translate);
            serialDetails.close();

        }
        else
        {
            name = getResources().getString(R.string.new_serial);
            altname = "";
            mNameText.setText("");
            mAltNameText.setText("");
            mColumnNumber.setText("5");
            mIsEpisodeWithName.setChecked(false);
            mIsActive.setChecked(true);
            mReleaseStudioNameText.setSelection(0, false);
            mTranslateStudioNameText.setSelection(0, false);
        }

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(name);
        if (!altname.isEmpty())
            bar.setSubtitle(altname);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.e("FUCK", "SerialEditActivity.onSaveInstanceState(): Serial=" + mClickedSerialId);
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, mClickedSerialId);
    }

    @Override
    protected void onPause()
    {
        Log.e("FUCK", "SerialEditActivity.onPause(): Serial=" + mClickedSerialId);
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume()
    {
        Log.e("FUCK", "SerialEditActivity.onResume(): Serial=" + mClickedSerialId);
        super.onResume();
        populateFields();
    }

    private void saveState()
    {
        Log.e("FUCK", "SerialEditActivity.saveState(): Serial=" + mClickedSerialId);
        if (mSave)
        {
            String nameTextValue = mNameText.getText().toString();
            String altNameTextValue = mAltNameText.getText().toString();
            int columnNumberTextValue = Integer.parseInt(mColumnNumber.getText().toString());
            int releaseStudioNameTextValue = mReleaseStudioNameText.getSelectedItemPosition();
            int translateStudioNameTextValue = mTranslateStudioNameText.getSelectedItemPosition();

            if (mClickedSerialId != -1)
            {
                MyApplication.getDBAdapter().updateSerial(mClickedSerialId, nameTextValue, altNameTextValue, mIsEpisodeWithName.isChecked() ? 1 : 0, columnNumberTextValue, mIsActive.isChecked() ? 1 : 0, releaseStudioNameTextValue, translateStudioNameTextValue);
            }
            else
            {
                long id = MyApplication.getDBAdapter().addSerial(nameTextValue, altNameTextValue, mIsEpisodeWithName.isChecked() ? 1 : 0, columnNumberTextValue, mIsActive.isChecked() ? 1 : 0, releaseStudioNameTextValue, translateStudioNameTextValue);
                if (id > 0)
                    mClickedSerialId = id;
                MyApplication.getDBAdapter().addHistory(new LocalDateTime().toDateTime(DateTimeZone.UTC).getMillis(), MyApplication.HISTORY_ADD_SERIAL, mClickedSerialId, 0);
                MyApplication.setNeed2RebuildSerialList(true);
            }
        }
    }



    private class SpinnerAdapter extends ArrayAdapter<StudioGeneric>
    {

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<StudioGeneric> objects)
        {
            super(context, textViewResourceId, objects);
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

            StudioGeneric data = getItem(position);

            ((TextView) convertView.findViewById(R.id.spinner_text)).setText(data.mName);

            if (data.mLogo != null)
                ((ImageView) convertView.findViewById(R.id.spinner_image)).setImageResource(getResources().getIdentifier(data.mLogo, "drawable", getPackageName()));
            else
                ((ImageView) convertView.findViewById(R.id.spinner_image)).setImageDrawable(null);

            return convertView;
        }

    }

    public class StudioGeneric
    {
        String mName;
        String mLogo;

        public StudioGeneric(String name, String logo)
        {
            mName = name;

            if (logo != null && !logo.isEmpty())
                mLogo = logo.substring(0, logo.indexOf("."));
            else
                mLogo = null;
        }
    }

}
