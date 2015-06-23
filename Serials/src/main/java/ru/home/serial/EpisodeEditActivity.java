package ru.home.serial;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;



public class EpisodeEditActivity extends SherlockFragmentActivity
{
    private static final int IDM_CANCEL = 101;

    private LinearLayout mNumberTextLayout;
    private LinearLayout mReleaseDateTextLayout;
    private LinearLayout mDownloadDateTextLayout;
    private LinearLayout mWatchDateTextLayout;
    private TextView mNumberText;
    private EditText mNameText;
    private EditText mAltNameText;
    private TextView mReleaseDateText;
    private TextView mDownloadDateText;
    private TextView mWatchDateText;
    private TextView mTempView;
    
    private Long mClickedEpisodeId;
    private Integer mClickedEpisodeSeason;
    private Long mClickedEpisodeSerialId;

    private int oldEpisodeNumber;
    private long oldEpisodeDownloadDate;
    private long oldEpisodeWatchDate;

    private boolean mSave;

    private Boolean mResultArg;
    private Intent mResult;

    @Override
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_edit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mNumberTextLayout = (LinearLayout) findViewById(R.id.edit_episode_number_layout);
        mNumberText = (TextView) findViewById(R.id.edit_episode_number);
        mNameText = (EditText) findViewById(R.id.edit_episode_name);
        mAltNameText = (EditText) findViewById(R.id.edit_episode_alt_name);
        mReleaseDateTextLayout = (LinearLayout) findViewById(R.id.edit_episode_release_date_layout);
        mReleaseDateText = (TextView) findViewById(R.id.edit_episode_release_date);
        mDownloadDateTextLayout = (LinearLayout) findViewById(R.id.edit_episode_download_date_layout);
        mDownloadDateText = (TextView) findViewById(R.id.edit_episode_download_date);
        mWatchDateTextLayout = (LinearLayout) findViewById(R.id.edit_episode_watch_date_layout);
        mWatchDateText = (TextView) findViewById(R.id.edit_episode_watch_date);

        mClickedEpisodeId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_ID);
        if (mClickedEpisodeId == null) {
            mClickedEpisodeId = getIntent().getLongExtra(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_ID, -1);
        }

        mClickedEpisodeSeason = (savedInstanceState == null) ? null :
                (Integer) savedInstanceState.getSerializable(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_SEASON);
        if (mClickedEpisodeSeason == null) {
            mClickedEpisodeSeason = getIntent().getIntExtra(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_SEASON, -1);
        }

        mClickedEpisodeSerialId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID);
        if (mClickedEpisodeSerialId == null) {
            mClickedEpisodeSerialId = getIntent().getLongExtra(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, -1);
        }

        mResultArg = (savedInstanceState == null) ? null :
            (Boolean) savedInstanceState.getSerializable("Need2RebuildEpisodeList");
        if (mResultArg == null)
            mResultArg = false;
        Log.e("FUCK", "EpisodeEditActivity.onCreate(): Serial="+mClickedEpisodeSerialId+" Episode="+mClickedEpisodeId+" Season="+mClickedEpisodeSeason+" mResultArg"+mResultArg);


        Cursor serialDetails = MyApplication.getDBAdapter().getSerialDetails(mClickedEpisodeSerialId);
        serialDetails.moveToFirst();
        String name, altname;
        name = serialDetails.getString(1);
        altname = serialDetails.getString(2);
        serialDetails.close();

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(name);
        if (!altname.isEmpty())
            bar.setSubtitle(altname);



        View.OnClickListener dateClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mTempView = (TextView) view.findViewWithTag("valueView");
                LocalDateTime mInitDate;
                try {
                    mInitDate = MyApplication.mHumanHiddenDateFormat.parseLocalDateTime(mTempView.getTag(R.string.date_viewer_tag).toString());
                } catch (Exception e) {
                    mInitDate = new LocalDateTime();
                }

                Dialog dialog = new WheelDatePicker(EpisodeEditActivity.this, mDateSetListener, mInitDate);
                dialog.show();
            }
        };

        View.OnClickListener numberClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int mInitInt = Integer.parseInt(mNumberText.getTag().toString());
                Dialog dialog = new WheelNumberPicker(EpisodeEditActivity.this, mNumberSetListener, mInitInt);
                dialog.show();
            }
        };

        mNumberTextLayout.setOnClickListener(numberClickListener);
        mReleaseDateTextLayout.setOnClickListener(dateClickListener);
        mDownloadDateTextLayout.setOnClickListener(dateClickListener);
        mWatchDateTextLayout.setOnClickListener(dateClickListener);

        mSave = true;

        mResult = new Intent();
        mResultArg = (mResultArg || mClickedEpisodeId == -1);
        mResult.putExtra("Need2RebuildEpisodeList", mResultArg);
        setResult(RESULT_OK, mResult);
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

    private WheelNumberPicker.OnNumberSetListener mNumberSetListener =
            new WheelNumberPicker.OnNumberSetListener()
            {
                @Override
                public void onNumberSet(int selectedNumber)
                {
                    mNumberText.setTag(selectedNumber);
                    mNumberText.setText(String.format(MyApplication.mEpisodeNumberFormat, mClickedEpisodeSeason, selectedNumber));

                    mResultArg = (mResultArg || oldEpisodeNumber != selectedNumber);
                    mResult.putExtra("Need2RebuildEpisodeList", mResultArg);
                    setResult(RESULT_OK, mResult);
                }
            };

    private WheelDatePicker.OnDateSetListener mDateSetListener =
            new WheelDatePicker.OnDateSetListener()
            {
                @Override
                public void onDateSet(LocalDateTime selectedDate)
                {
                    if (selectedDate != null)
                    {
                        mTempView.setText(MyApplication.mHumanReadableDateFormat.print(selectedDate));
                        mTempView.setTag(R.string.date_viewer_tag, MyApplication.mHumanHiddenDateFormat.print(selectedDate));
                    }
                    else
                    {
                        mTempView.setText(R.string.empty_date);
                        mTempView.setTag(R.string.date_viewer_tag, null);
                    }

                    setLayoutsEnable();
                }
            };

    private void populateFields()
    {
        Log.e("FUCK", "EpisodeEditActivity.populateFields(): Serial="+mClickedEpisodeSerialId+" Episode="+mClickedEpisodeId+" Season="+mClickedEpisodeSeason);
        if (mClickedEpisodeId != -1)
        {
            Cursor episodeDetails = MyApplication.getDBAdapter().getEpisodeDetails(mClickedEpisodeId);
            episodeDetails.moveToFirst();
            mNumberText.setTag(episodeDetails.getInt(1));
            mNumberText.setText(String.format(MyApplication.mEpisodeNumberFormat, mClickedEpisodeSeason, episodeDetails.getInt(1)));
            mNameText.setText(episodeDetails.getString(3));
            mAltNameText.setText(episodeDetails.getString(8));
            long releaseDate = episodeDetails.getLong(4);
            long downloadDate = episodeDetails.getLong(5);
            long watchDate = episodeDetails.getLong(6);
            episodeDetails.close();
            
            if (releaseDate != -1)
            {
                mReleaseDateText.setText(MyApplication.mHumanReadableDateFormat.print(releaseDate));
                mReleaseDateText.setTag(R.string.date_viewer_tag, MyApplication.mHumanHiddenDateFormat.print(releaseDate));
            }
            else mReleaseDateText.setText(R.string.empty_date);

            if (downloadDate != -1)
            {
                mDownloadDateText.setText(MyApplication.mHumanReadableDateFormat.print(downloadDate));
                mDownloadDateText.setTag(R.string.date_viewer_tag, MyApplication.mHumanHiddenDateFormat.print(downloadDate));
            }
            else mDownloadDateText.setText(R.string.empty_date);

            if (watchDate != -1)
            {
                mWatchDateText.setText(MyApplication.mHumanReadableDateFormat.print(watchDate));
                mWatchDateText.setTag(R.string.date_viewer_tag, MyApplication.mHumanHiddenDateFormat.print(watchDate));
            }
            else mWatchDateText.setText(R.string.empty_date);
        }
        else
        {
            LocalDateTime now = new LocalDateTime();
            int newEpisodeNumber = MyApplication.getDBAdapter().getMaxEpisodeNumberBySerialAndId(mClickedEpisodeSerialId, mClickedEpisodeSeason);
            mNumberText.setTag(String.valueOf(newEpisodeNumber + 1));
            mNumberText.setText(String.format(MyApplication.mEpisodeNumberFormat, mClickedEpisodeSeason, newEpisodeNumber + 1));
            mNameText.setText("");
            mAltNameText.setText("");
            mReleaseDateText.setText(MyApplication.mHumanReadableDateFormat.print(now));
            mReleaseDateText.setTag(R.string.date_viewer_tag, MyApplication.mHumanHiddenDateFormat.print(now));
            mDownloadDateText.setText(R.string.empty_date);
            mWatchDateText.setText(R.string.empty_date);
        }

        oldEpisodeNumber = Integer.parseInt(mNumberText.getTag().toString());
        oldEpisodeDownloadDate = -1;
        oldEpisodeWatchDate = -1;
        try {
            oldEpisodeDownloadDate = MyApplication.mHumanHiddenDateFormat.parseLocalDateTime(mDownloadDateText.getTag(R.string.date_viewer_tag).toString()).toDateTime(DateTimeZone.UTC).getMillis();
        } catch (Exception ignored) {}
        try {
            oldEpisodeWatchDate = MyApplication.mHumanHiddenDateFormat.parseLocalDateTime(mWatchDateText.getTag(R.string.date_viewer_tag).toString()).toDateTime(DateTimeZone.UTC).getMillis();
        } catch (Exception ignored) {}

        setLayoutsEnable();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.e("FUCK", "EpisodeEditActivity.onSaveInstanceState(): Serial="+mClickedEpisodeSerialId+" Episode="+mClickedEpisodeId+" Season="+mClickedEpisodeSeason);
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_ID, mClickedEpisodeId);
        outState.putSerializable(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_SEASON, mClickedEpisodeSeason);
        outState.putSerializable(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, mClickedEpisodeSerialId);
        outState.putSerializable("Need2RebuildEpisodeList", mResultArg);
    }

    @Override
    protected void onPause()
    {
        Log.e("FUCK", "EpisodeEditActivity.onPause(): Serial="+mClickedEpisodeSerialId+" Episode="+mClickedEpisodeId+" Season="+mClickedEpisodeSeason);
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume()
    {
        Log.e("FUCK", "EpisodeEditActivity.onResume(): Serial="+mClickedEpisodeSerialId+" Episode="+mClickedEpisodeId+" Season="+mClickedEpisodeSeason);
        super.onResume();
        populateFields();
    }


    private void saveState()
    {
        Log.e("FUCK", "EpisodeEditActivity.saveState(): Serial="+mClickedEpisodeSerialId+" Episode="+mClickedEpisodeId+" Season="+mClickedEpisodeSeason);
        if (mSave)
        {
            int numberTextValue = Integer.parseInt(mNumberText.getTag().toString());
            String nameTextValue = mNameText.getText().toString();
            String altNameTextValue = mAltNameText.getText().toString();
            long releaseDateTextValue = -1;
            long downloadDateTextValue = -1;
            long watchDateTextValue = -1;

            try {
                releaseDateTextValue = MyApplication.mHumanHiddenDateFormat.parseLocalDateTime(mReleaseDateText.getTag(R.string.date_viewer_tag).toString()).toDateTime(DateTimeZone.UTC).getMillis();
            } catch (Exception ignored) {}
            try {
                downloadDateTextValue = MyApplication.mHumanHiddenDateFormat.parseLocalDateTime(mDownloadDateText.getTag(R.string.date_viewer_tag).toString()).toDateTime(DateTimeZone.UTC).getMillis();
            } catch (Exception ignored) {}
            try {
                watchDateTextValue = MyApplication.mHumanHiddenDateFormat.parseLocalDateTime(mWatchDateText.getTag(R.string.date_viewer_tag).toString()).toDateTime(DateTimeZone.UTC).getMillis();
            } catch (Exception ignored) {}

            if (mClickedEpisodeId != -1)
            {
                MyApplication.getDBAdapter().updateEpisode(mClickedEpisodeId, numberTextValue, nameTextValue, releaseDateTextValue, downloadDateTextValue, watchDateTextValue, altNameTextValue);
            }
            else
            {
                long id = MyApplication.getDBAdapter().addEpisode(numberTextValue, mClickedEpisodeSeason, nameTextValue, releaseDateTextValue, downloadDateTextValue, watchDateTextValue, mClickedEpisodeSerialId, altNameTextValue);
                if (id > 0)
                    mClickedEpisodeId = id;
            }

            if (MyApplication.need2RebuildSerialList() || (oldEpisodeDownloadDate != downloadDateTextValue || oldEpisodeWatchDate != watchDateTextValue) && MyApplication.mShowSerialFormat == 3)
                MyApplication.setNeed2RebuildSerialList(true);
            else
                MyApplication.setNeed2RebuildSerialList(false);


            if (oldEpisodeDownloadDate == -1 && downloadDateTextValue != -1)
                MyApplication.getDBAdapter().addHistory(downloadDateTextValue, MyApplication.HISTORY_DOWNLOAD_EPISODE, mClickedEpisodeSerialId, mClickedEpisodeId);
            else if (oldEpisodeDownloadDate != -1 && downloadDateTextValue == -1)
                MyApplication.getDBAdapter().deleteHistoryEpisodeStatus(mClickedEpisodeId, MyApplication.HISTORY_DOWNLOAD_EPISODE);
            else if (oldEpisodeDownloadDate != downloadDateTextValue)
                MyApplication.getDBAdapter().updateHistoryEpisodeStatus(downloadDateTextValue, MyApplication.HISTORY_DOWNLOAD_EPISODE, mClickedEpisodeId);

            if (oldEpisodeWatchDate == -1 && watchDateTextValue != -1)
                MyApplication.getDBAdapter().addHistory(watchDateTextValue, MyApplication.HISTORY_WATCH_EPISODE, mClickedEpisodeSerialId, mClickedEpisodeId);
            else if (oldEpisodeWatchDate != -1 && watchDateTextValue == -1)
                MyApplication.getDBAdapter().deleteHistoryEpisodeStatus(mClickedEpisodeId, MyApplication.HISTORY_WATCH_EPISODE);
            else if (oldEpisodeWatchDate != watchDateTextValue)
                MyApplication.getDBAdapter().updateHistoryEpisodeStatus(watchDateTextValue, MyApplication.HISTORY_WATCH_EPISODE, mClickedEpisodeId);

            oldEpisodeDownloadDate = downloadDateTextValue;
            oldEpisodeWatchDate = watchDateTextValue;
        }
    }

    private void setLayoutsEnable()
    {
        if (mReleaseDateText.getText().equals(getResources().getString(R.string.empty_date)))
        {
            mDownloadDateTextLayout.setEnabled(false);
            mWatchDateTextLayout.setEnabled(false);
        }
        else if (mDownloadDateText.getText().equals(getResources().getString(R.string.empty_date)))
        {
            mDownloadDateTextLayout.setEnabled(true);
            mWatchDateTextLayout.setEnabled(false);
        }
        else if (mWatchDateText.getText().equals(getResources().getString(R.string.empty_date)))
        {
            mDownloadDateTextLayout.setEnabled(true);
            mWatchDateTextLayout.setEnabled(true);
        }
        else
        {
            mDownloadDateTextLayout.setEnabled(false);
            mWatchDateTextLayout.setEnabled(true);
        }
    }

}