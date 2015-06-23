package ru.home.serial;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.devspark.collapsiblesearchmenu.CollapsibleMenuUtils;

import org.joda.time.LocalDate;

import ru.home.serial.popup.ActionItem;
import ru.home.serial.popup.QuickActionPopupWindow;

import java.util.ArrayList;

public class SerialActivity extends SherlockFragmentActivity
{
    private static final int IDM_MENU_GROUP_VIEWS = 1;
    private static final int IDM_SHOW_STATS = 103;
    private static final int IDM_WHAT_TO_DO = 102;
    private static final int IDM_ADD_NEW = 101;
    private static final int IDM_CHANGE_VIEW = 100;
    private static final int IDM_CHANGE_VIEW_ALL = 0;
    private static final int IDM_CHANGE_VIEW_ACTIVE = 1;
    private static final int IDM_CHANGE_VIEW_TODO = 2;
    private static final int IDM_CHANGE_VIEW_RECENT_ACTION1 = 4;
    private static final int IDM_CHANGE_VIEW_RECENT_ACTION2 = 3;


    private static final int ID_EDIT = 2;
    private static final int ID_DELETE = 3;
    private static final int ID_NEW_SEASON = 4;
    private static final int ID_DELETE_SEASON = 5;
    private static final int REQUEST_CODE_SERIAL_EDIT = 1;
    private static final int REQUEST_CODE_SERIAL_ADD = 2;
    private static final int REQUEST_CODE_SERIAL_ENTER = 3;

    private ArrayList<Serial> mSerialArrayList = new ArrayList<Serial>();
    private LayoutInflater mLayoutInflater;
    private ViewGroup mListContainer;

    private QuickActionPopupWindow mQuickAction;
    
    private Long mClickedSerialId;

    TextView mLoadingTextView;

    private MenuItem mSearchMenuItem;

    private FillListTask mFillListTask;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.e("FUCK","SerialActivity.onCreate()");

        requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_PROGRESS);
        setContentView(R.layout.list_container);

        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.title);

        mLayoutInflater = getLayoutInflater();

        initPopupMenu();

        mListContainer = (ViewGroup) findViewById(R.id.serial_list);
        mLoadingTextView = (TextView) findViewById(R.id.loading_text);

        mClickedSerialId = -1l;

        MyApplication.setNeed2RebuildSerialList(true);
    }


//    public void showHistory()
//    {
//        Cursor historyCursor = MyApplication.getDBAdapter().getHistory();
//        String msg="";
//        historyCursor.moveToFirst();
//        while(!historyCursor.isAfterLast())
//        {
//            msg += "id: "+historyCursor.getLong(0);
//            msg += " date: "+MyApplication.mHumanReadableDateFormat.format(new Date(historyCursor.getLong(1)));
//            msg += " type: "+historyCursor.getInt(2);
//            msg += " serial: "+historyCursor.getInt(3);
//            msg += " episode: "+historyCursor.getInt(4);
//            msg += "\n";
//            historyCursor.moveToNext();
//        }
//        historyCursor.close();
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//    }

//    public void recreateHistory()
//    {
//        MyApplication.getDBAdapter().clearHistory();
//
//        Cursor serialCursor = MyApplication.getDBAdapter().getSerials("");
//        serialCursor.moveToFirst();
//        while(!serialCursor.isAfterLast())
//        {
//            Serial serial = new Serial(serialCursor.getInt(0), serialCursor.getString(1), serialCursor.getString(2), serialCursor.getInt(3), serialCursor.getInt(4), serialCursor.getInt(5), serialCursor.getInt(6), 0, 0, 0, 0, 0);
//
//            Cursor datesRange = MyApplication.getDBAdapter().getEpisodesTimeRange(serial.mId);
//            if (datesRange.moveToFirst())
//            {
//                serial.mMinEpisodeDate = datesRange.getLong(0);
//                serial.mMaxEpisodeDate = datesRange.getLong(1);
//            }
//
//            MyApplication.getDBAdapter().addHistory(serial.mMinEpisodeDate, MyApplication.HISTORY_ADD_SERIAL, serial.mId, 0);
//
//            for (int i=0; i<serial.mSeasonsCount; i++)
//            {
//                Cursor episodeCursor = MyApplication.getDBAdapter().getEpisodes(serial.mId, i+1);
//                episodeCursor.moveToFirst();
//
//                while(!episodeCursor.isAfterLast())
//                {
//                    Episode episode = new Episode(episodeCursor.getInt(0), episodeCursor.getInt(1), episodeCursor.getInt(2), episodeCursor.getString(3), episodeCursor.getLong(4), episodeCursor.getLong(5), episodeCursor.getLong(6), episodeCursor.getInt(7));
//
//                    if (episode.mDownloadDate != -1) MyApplication.getDBAdapter().addHistory(episode.mDownloadDate, MyApplication.HISTORY_DOWNLOAD_EPISODE, serial.mId, episode.mId);
//                    if (episode.mWatchDate != -1) MyApplication.getDBAdapter().addHistory(episode.mWatchDate, MyApplication.HISTORY_WATCH_EPISODE, serial.mId, episode.mId);
//                    episodeCursor.moveToNext();
//                }
//                episodeCursor.close();
//            }
//
//            serialCursor.moveToNext();
//        }
//        serialCursor.close();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        mSearchMenuItem = CollapsibleMenuUtils.addSearchMenuItem(menu, false, mOnQueryTextListener);

        menu.add(Menu.NONE, IDM_WHAT_TO_DO, Menu.NONE, R.string.menu_advice)
            .setIcon(R.drawable.menu_advice);

        menu.add(Menu.NONE, IDM_SHOW_STATS, Menu.NONE, R.string.menu_stats)
                .setIcon(R.drawable.menu_stats);


        menu.add(Menu.NONE, IDM_ADD_NEW, Menu.NONE, R.string.add_serial)
                .setIcon(R.drawable.ab_addnew)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        SubMenu subMenu1 = menu.addSubMenu(Menu.NONE, IDM_CHANGE_VIEW, Menu.NONE, R.string.menu_view_filter);
        subMenu1.add(IDM_MENU_GROUP_VIEWS, IDM_CHANGE_VIEW_ALL, Menu.NONE, R.string.menu_view_all).setCheckable(true);
        subMenu1.add(IDM_MENU_GROUP_VIEWS, IDM_CHANGE_VIEW_ACTIVE, Menu.NONE, R.string.menu_view_active).setCheckable(true);
        subMenu1.add(IDM_MENU_GROUP_VIEWS, IDM_CHANGE_VIEW_TODO, Menu.NONE, R.string.menu_view_todo).setCheckable(true);
        subMenu1.add(IDM_MENU_GROUP_VIEWS, IDM_CHANGE_VIEW_RECENT_ACTION2, Menu.NONE, R.string.menu_view_recent_action2).setCheckable(true);
        subMenu1.add(IDM_MENU_GROUP_VIEWS, IDM_CHANGE_VIEW_RECENT_ACTION1, Menu.NONE, R.string.menu_view_recent_action1).setCheckable(true);


        subMenu1.getItem().setIcon(R.drawable.ab_viewfilter)
                          .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        subMenu1.getItem(MyApplication.mShowSerialFormat).setChecked(true);



        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mSearchMenuItem.isActionViewExpanded())
        {
            mSearchMenuItem.collapseActionView();
        }

        StringBuilder message = new StringBuilder();

        if (item.getItemId() == IDM_WHAT_TO_DO)
        {
            Cursor episodeCursor = MyApplication.getDBAdapter().getEpisodesForHelp();
            episodeCursor.moveToFirst();
            while(!episodeCursor.isAfterLast())
            {
                if (episodeCursor.getLong(5) == -1 && episodeCursor.getLong(4) != -1)
                {
                    message.append(getResources().getString(R.string.advice_watch));
                    break;
                }

                episodeCursor.moveToNext();
            }
            if (message.length() == 0)
            {
                episodeCursor.moveToFirst();
                while(!episodeCursor.isAfterLast())
                {
                    if (episodeCursor.getLong(4) == -1)
                    {
                        message.append(getResources().getString(R.string.advice_download));
                        break;
                    }

                    episodeCursor.moveToNext();
                }
            }

            if (message.length() == 0) message.append(getResources().getString(R.string.advice_nothing_todo));
            else
                message.append(getResources().getString(R.string.advice_serial))
                        .append(episodeCursor.getString(6))
                        .append(getResources().getString(R.string.advice_season))
                        .append(episodeCursor.getInt(1))
                        .append(getResources().getString(R.string.advice_episode_number))
                        .append(episodeCursor.getInt(0))
                        .append(getResources().getString(R.string.advice_episode_name))
                        .append(episodeCursor.getString(2));


            episodeCursor.close();

            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
        }
        else if (item.getItemId() == IDM_SHOW_STATS)
        {
            message.append(getResources().getString(R.string.stats_serials))
                    .append(MyApplication.getDBAdapter().getStatisticsSerials())
                    .append(getResources().getString(R.string.stats_episodes))
                    .append(MyApplication.getDBAdapter().getStatisticsEpisodes())
                    .append(getResources().getString(R.string.stats_history))
                    .append(MyApplication.getDBAdapter().getStatisticsHistory());

            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
        }
        else if (item.getItemId() == IDM_ADD_NEW)
        {
            /**
             * Case S1
             */
            Intent intent = new Intent(SerialActivity.this, SerialEditActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SERIAL_ADD);
        }
        else if (item.getItemId() < 10)
        {
            MyApplication.mShowSerialFormat = item.getItemId();

            invalidateOptionsMenu();


            mFillListTask.cancel(true);
            fillData();
        }

        return false;
    }

    private CollapsibleMenuUtils.OnQueryTextListener mOnQueryTextListener = new CollapsibleMenuUtils.OnQueryTextListener()
    {
        @Override
        public void onQueryTextSubmit(String query)
        {
        }

        @Override
        public void onQueryTextChange(String newText)
        {
            Log.e("FUCK","SerialActivity.onTextChanged1(): New filter text = " + newText + " status = " + mFillListTask.getStatus() + " cancelled = " + mFillListTask.isCancelled());

            mFillListTask.cancel(true);
            fillData(newText.toLowerCase());

            Log.e("FUCK","SerialActivity.onTextChanged2(): New filter text = " + newText + " status = " + mFillListTask.getStatus() + " cancelled = " + mFillListTask.isCancelled());
        }
    };



    @Override
    public void onStart()
    {
        super.onStart();
        Log.e("FUCK","SerialActivity.onStart(): Serial="+mClickedSerialId);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.e("FUCK","SerialActivity.onResume(): Serial="+mClickedSerialId);

        if (MyApplication.need2RebuildSerialList())
            fillData();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        Log.e("FUCK","SerialActivity.onRestart(): Serial="+mClickedSerialId);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.e("FUCK","SerialActivity.onStop(): Serial="+mClickedSerialId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("FUCK","SerialActivity.onDestroy(): Serial="+mClickedSerialId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.e("FUCK", "SerialActivity.onSaveInstanceState(): Serial=" + mClickedSerialId);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        Log.e("FUCK","SerialActivity.onPause(): Serial="+mClickedSerialId);

        if (mFillListTask.getStatus() != AsyncTask.Status.FINISHED)
        {
            MyApplication.setNeed2RebuildSerialList(true);
            mFillListTask.cancel(true);
        }

        if (mSearchMenuItem.isActionViewExpanded())
        {
            mSearchMenuItem.collapseActionView();
        }
    }



    class UpdateListTask extends AsyncTask<Long, Void, Void>
    {
        ExtendedStateRelativeLayout updatedView;
        Serial serial;

        @Override
        protected Void doInBackground(Long... params)
        {
            updatedView = (ExtendedStateRelativeLayout) mListContainer.findViewWithTag(params[0]);

            Cursor serialCursor = MyApplication.getDBAdapter().getSerialDetails(params[0]);
            serialCursor.moveToFirst();
            serial = new Serial(serialCursor.getInt(0), serialCursor.getString(1), serialCursor.getString(2), serialCursor.getInt(3), serialCursor.getInt(4), serialCursor.getInt(5), serialCursor.getInt(6), serialCursor.getInt(7), serialCursor.getInt(8), 0, 0, 0, 0, 0, 0);
            serial.mReleasedCount = MyApplication.getDBAdapter().getEpisodeReleasedCountById(serial.mId);
            serial.mDownloadedCount = MyApplication.getDBAdapter().getEpisodeDownloadedCountById(serial.mId);
            serial.mWatchedCount = MyApplication.getDBAdapter().getEpisodeWatchedCountById(serial.mId);

            Cursor datesRange = MyApplication.getDBAdapter().getEpisodesTimeRange(serial.mId);
            if (datesRange.moveToFirst())
            {
                serial.mMinEpisodeDate = datesRange.getLong(0);
                serial.mMaxEpisodeDate = datesRange.getLong(1);
            }

            return null;
        }


        @SuppressLint("WrongViewCast")
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            View tempView;

            ((TextView) updatedView.findViewById(R.id.list_item_name)).setText(serial.mName + " (" + serial.mSeasonsCount + ")");

            if (!serial.mAltName.equals(""))
            {
                tempView = updatedView.findViewById(R.id.list_item_altname);

                if (tempView instanceof ViewStub)
                    tempView = ((ViewStub) tempView).inflate();
                else
                    tempView.setVisibility(View.VISIBLE);

                ((TextView) tempView).setText(serial.mAltName);
            }
            else
            {
                updatedView.findViewById(R.id.list_item_altname).setVisibility(View.GONE);
            }

            ((ImageView) updatedView.findViewById(R.id.list_item_dopinfo1_image)).setImageResource(R.drawable.ic_released);
            ((ImageView) updatedView.findViewById(R.id.list_item_dopinfo2_image)).setImageResource(R.drawable.ic_downloaded);
            ((ImageView) updatedView.findViewById(R.id.list_item_dopinfo3_image)).setImageResource(R.drawable.ic_watched);

            ((TextView) updatedView.findViewById(R.id.list_item_dopinfo1_text)).setText(String.format(MyApplication.mSerialCountsFormat, serial.mReleasedCount));
            ((TextView) updatedView.findViewById(R.id.list_item_dopinfo2_text)).setText(String.format(MyApplication.mSerialCountsFormat, serial.mDownloadedCount));
            ((TextView) updatedView.findViewById(R.id.list_item_dopinfo3_text)).setText(String.format(MyApplication.mSerialCountsFormat, serial.mWatchedCount));

            if (serial.mMinEpisodeDate > 0 && serial.mMaxEpisodeDate > 0)
            {
                tempView = updatedView.findViewById(R.id.list_item_dopinfo4_image);

                if (tempView instanceof ViewStub)
                    tempView = ((ViewStub) tempView).inflate();
                else
                    tempView.setVisibility(View.VISIBLE);

                if (serial.IsActive())
                    ((ImageView) tempView).setImageResource(R.drawable.ic_clock);
                else
                    ((ImageView) tempView).setImageResource(R.drawable.ic_finished);

                tempView = updatedView.findViewById(R.id.list_item_dopinfo4_text);

                if (tempView instanceof ViewStub)
                    tempView = ((ViewStub) tempView).inflate();
                else
                    tempView.setVisibility(View.VISIBLE);

                ((TextView) tempView).setText(String.format(MyApplication.mSerialDatesRangeFormat, new LocalDate(serial.mMinEpisodeDate).getYear(), new LocalDate(serial.mMaxEpisodeDate).getYear()));
            }
            else
            {
                updatedView.findViewById(R.id.list_item_dopinfo4_image).setVisibility(View.GONE);
                updatedView.findViewById(R.id.list_item_dopinfo4_text).setVisibility(View.GONE);
            }

            if (serial.mReleasedCount == serial.mDownloadedCount && serial.mDownloadedCount == serial.mWatchedCount)
                updatedView.setWatched(true);
            else
                updatedView.setWatched(false);
        }
    }

    class FillListTask extends AsyncTask<Object, Object, Void>
    {
        String filter;

        @Override
        protected void onPreExecute()
        {
            Log.e("FUCK","SerialActivity.onPreExecute(): status = " + getStatus() + " cancelled = " + isCancelled());

            mListContainer.removeAllViews();
            mSerialArrayList.clear();

            mLoadingTextView.setVisibility(View.VISIBLE);
            mLoadingTextView.setText(String.format("%s %2d%%", getResources().getString(R.string.progress_dialog), 0));
        }

        @Override
        protected void onProgressUpdate(Object... params)
        {
            Log.e("FUCK","SerialActivity.onProgressUpdate(): status = " + getStatus() + " cancelled = " + isCancelled());

            if (isCancelled())
                return;

            mLoadingTextView.setText(String.format("%s %2d%%", getResources().getString(R.string.progress_dialog), params[0]));
            mListContainer.addView((View) params[1]);
        }

        @Override
        protected Void doInBackground(Object... params)
        {
            Log.e("FUCK","SerialActivity.doInBackground(): status = " + getStatus() + " cancelled = " + isCancelled());

            if (params.length > 0)
                filter = (String) params[0];
            else
                filter = "";

            long lastActionDate;
            int containerIndex = 0;
            View view;

            Cursor serialCursor;

            if (MyApplication.mShowSerialFormat == 0 || filter.length() > 0)
                serialCursor = MyApplication.getDBAdapter().getSerials(filter);
            else if (MyApplication.mShowSerialFormat == 1)
                serialCursor = MyApplication.getDBAdapter().getSerialsActive();
            else if (MyApplication.mShowSerialFormat == 2)
                serialCursor = MyApplication.getDBAdapter().getSerialsToDo();
            else if (MyApplication.mShowSerialFormat == 4)
                serialCursor = MyApplication.getDBAdapter().getSerialsRecentMonthOld();
            else //if (MyApplication.mShowSerialFormat == 3)
                serialCursor = MyApplication.getDBAdapter().getSerialsRecentMonthNew();

            serialCursor.moveToFirst();
            while(!serialCursor.isAfterLast())
            {
                // Finish if activity is left
                if (isCancelled())
                {
                    serialCursor.close();
                    return null;
                }

                if (MyApplication.mShowSerialFormat == 3)
                {
                    try
                    {
                        lastActionDate = serialCursor.getLong(9);
                    }
                    catch (Exception e)
                    {
                        lastActionDate = 0;
                    }
                }
                else
                    lastActionDate = 0;

                Serial serial = new Serial(serialCursor.getInt(0), serialCursor.getString(1), serialCursor.getString(2), serialCursor.getInt(3), serialCursor.getInt(4), serialCursor.getInt(5), serialCursor.getInt(6), serialCursor.getInt(7), serialCursor.getInt(8), 0, 0, 0, 0, 0, lastActionDate);

                mSerialArrayList.add(serial);

                serialCursor.moveToNext();
            }
            serialCursor.close();


            lastActionDate = 0;


            for (Serial obj: mSerialArrayList)
            {
                // Finish if activity is left
                if (isCancelled())
                {
                    return null;
                }

                obj.mReleasedCount = MyApplication.getDBAdapter().getEpisodeReleasedCountById(obj.mId);
                obj.mDownloadedCount = MyApplication.getDBAdapter().getEpisodeDownloadedCountById(obj.mId);
                obj.mWatchedCount = MyApplication.getDBAdapter().getEpisodeWatchedCountById(obj.mId);

                Cursor datesRange = MyApplication.getDBAdapter().getEpisodesTimeRange(obj.mId);
                if (datesRange.moveToFirst())
                {
                    obj.mMinEpisodeDate = datesRange.getLong(0);
                    obj.mMaxEpisodeDate = datesRange.getLong(1);
                }

                if (!MyApplication.mHumanFullDateFormat.print(obj.mLastAction).equals(MyApplication.mHumanFullDateFormat.print(lastActionDate)))
                {
                    if (containerIndex == 0)
                    {
                        view = mLayoutInflater.inflate(R.layout.list_item_divider_top, null);
                        containerIndex++;
                    }
                    else
                        view = mLayoutInflater.inflate(R.layout.list_item_divider_middle, null);

                    ((TextView) view.findViewById(R.id.list_item_name)).setText(MyApplication.mHumanFullDateFormat.print(obj.mLastAction));
                    publishProgress((containerIndex+(obj.mLastAction == 0?1:0))*100/mSerialArrayList.size(), view);
                }
                lastActionDate = obj.mLastAction;

                if (containerIndex == 0 && mSerialArrayList.size() == 1)
                    view = mLayoutInflater.inflate(R.layout.list_item_single, null);
                else if (containerIndex == 0 && mSerialArrayList.size() > 1)
                    view = mLayoutInflater.inflate(R.layout.list_item_top, null);
                else if (containerIndex == mSerialArrayList.size() - (obj.mLastAction == 0?1:0)) // +1 because of top history row
                    view = mLayoutInflater.inflate(R.layout.list_item_bottom, null);
                else
                    view = mLayoutInflater.inflate(R.layout.list_item_middle, null);


                ((TextView) view.findViewById(R.id.list_item_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                ((TextView) view.findViewById(R.id.list_item_name)).setText(obj.mName + " (" + obj.mSeasonsCount + ")");

                if (!obj.mAltName.equals(""))
                {
                    ((TextView) ((ViewStub) view.findViewById(R.id.list_item_altname)).inflate()).setText(obj.mAltName);
                }


                ((ImageView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo1_image)).inflate()).setImageResource(R.drawable.ic_released);
                ((ImageView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo2_image)).inflate()).setImageResource(R.drawable.ic_downloaded);
                ((ImageView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo3_image)).inflate()).setImageResource(R.drawable.ic_watched);

                ((TextView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo1_text)).inflate()).setText(String.format(MyApplication.mSerialCountsFormat, obj.mReleasedCount));
                ((TextView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo2_text)).inflate()).setText(String.format(MyApplication.mSerialCountsFormat, obj.mDownloadedCount));
                ((TextView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo3_text)).inflate()).setText(String.format(MyApplication.mSerialCountsFormat, obj.mWatchedCount));

                if (obj.mMinEpisodeDate > 0 && obj.mMaxEpisodeDate > 0)
                {
                    if (obj.IsActive())
                        ((ImageView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo4_image)).inflate()).setImageResource(R.drawable.ic_clock);
                    else
                        ((ImageView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo4_image)).inflate()).setImageResource(R.drawable.ic_finished);

                    ((TextView) ((ViewStub) view.findViewById(R.id.list_item_dopinfo4_text)).inflate()).setText(String.format(MyApplication.mSerialDatesRangeFormat, new LocalDate(obj.mMinEpisodeDate).getYear(), new LocalDate(obj.mMaxEpisodeDate).getYear()));
                }

                if (obj.mReleasedCount == obj.mDownloadedCount && obj.mDownloadedCount == obj.mWatchedCount)
                    ((ExtendedStateRelativeLayout)view).setWatched(true);

                view.setTag(obj.mId);
                view.setOnClickListener(new View.OnClickListener()
                {
                    /**
                     * Case S5
                     */
                    @Override
                    public void onClick(View view)
                    {
                        mClickedSerialId = Long.parseLong(view.getTag().toString());
                        if (MyApplication.getDBAdapter().getSerialSeasonCountById(mClickedSerialId) > 0)
                        {
                            Intent intent = new Intent(SerialActivity.this, EpisodeActivity.class);
                            intent.putExtra(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, mClickedSerialId);
                            startActivityForResult(intent, REQUEST_CODE_SERIAL_ENTER);
                        } else
                            Toast.makeText(SerialActivity.this, getResources().getString(R.string.no_seasons_cant_enter), Toast.LENGTH_LONG).show();
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View view)
                    {
                        mClickedSerialId = Long.parseLong(view.getTag().toString());
                        mQuickAction.show(view);
                        return true;
                    }
                });

                publishProgress((containerIndex+(obj.mLastAction == 0?1:0))*100/mSerialArrayList.size(), view);


                containerIndex++;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Log.e("FUCK", "SerialActivity.onPostExecute(): status = " + getStatus() + " cancelled = " + isCancelled());

            super.onPostExecute(result);
            mLoadingTextView.setVisibility(View.GONE);
        }
    }

    private void fillData()
    {
        MyApplication.setNeed2RebuildSerialList(false);

        mFillListTask = new FillListTask();
        mFillListTask.execute();
    }

    private void fillData(String s)
    {
        MyApplication.setNeed2RebuildSerialList(false);

        mFillListTask = new FillListTask();
        mFillListTask.execute(s);
    }

    private void updateData(long serialId)
    {
        new UpdateListTask().execute(serialId);
    }

    public void initPopupMenu()
    {
        ActionItem addSeason = new ActionItem(ID_NEW_SEASON, getResources().getString(R.string.add_season), getResources().getDrawable(R.drawable.ic_plus));
        ActionItem deleteSeason = new ActionItem(ID_DELETE_SEASON, getResources().getString(R.string.delete_season), getResources().getDrawable(R.drawable.ic_minus));
        ActionItem editItem = new ActionItem(ID_EDIT, getResources().getString(R.string.edit), getResources().getDrawable(R.drawable.ic_edit));
        ActionItem deleteItem = new ActionItem(ID_DELETE, getResources().getString(R.string.delete_serial), getResources().getDrawable(R.drawable.ic_delete));

        mQuickAction = new QuickActionPopupWindow();
        mQuickAction.addActionItem(addSeason);
        mQuickAction.addActionItem(deleteSeason);
        mQuickAction.addActionItem(editItem);
        mQuickAction.addActionItem(deleteItem);
        mQuickAction.setOnActionItemClickListener(new QuickActionPopupWindow.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickActionPopupWindow quickAction, int pos, int actionId) {
                //ActionItem actionItem = quickAction.getActionItem(pos);

                /**
                 * Case S4
                 */
                if (actionId == ID_NEW_SEASON)
                {
                    MyApplication.getDBAdapter().addSeason(mClickedSerialId);
                    updateData(mClickedSerialId);
                }
                /**
                 * Case S4
                 */
                else if (actionId == ID_DELETE_SEASON)
                {
                    final int seasonCount = MyApplication.getDBAdapter().getSerialSeasonCountById(mClickedSerialId);
                    if (seasonCount > 0)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SerialActivity.this);
                        builder
                                .setMessage(R.string.dialog_confirm)
                                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener()
                                {
                                    @Override
									public void onClick(DialogInterface dialog, int whichButton)
                                    {
                                        MyApplication.getDBAdapter().deleteHistorySeason(mClickedSerialId, seasonCount);
                                        MyApplication.getDBAdapter().deleteSeason(mClickedSerialId, seasonCount);
                                        if (MyApplication.mShowSerialFormat == 3)
                                        {
                                            mFillListTask.cancel(true);
                                            fillData();
                                        }
                                        else
                                            updateData(mClickedSerialId);
                                    }
                                })
                                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
                                {
                                    @Override
									public void onClick(DialogInterface dialog, int whichButton)
                                    {
                                    }
                                })
                                .show();
                    }
                    else
                        Toast.makeText(SerialActivity.this, getResources().getString(R.string.no_seasons_cant_delete), Toast.LENGTH_LONG).show();
                }
                /**
                 * Case S2
                 */
                else if (actionId == ID_EDIT)
                {
                    Intent intent = new Intent(SerialActivity.this, SerialEditActivity.class);
                    intent.putExtra(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, mClickedSerialId);
                    startActivityForResult(intent, REQUEST_CODE_SERIAL_EDIT);
                }
                /**
                 * Case S3
                 */
                else if (actionId == ID_DELETE)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SerialActivity.this);
                    builder
                            .setMessage(R.string.dialog_confirm)
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener()
                            {
                                @Override
								public void onClick(DialogInterface dialog, int whichButton)
                                {
                                    MyApplication.getDBAdapter().deleteHistorySerial(mClickedSerialId);
                                    MyApplication.getDBAdapter().deleteSerial(mClickedSerialId);

                                    mFillListTask.cancel(true);
                                    fillData();
                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
								public void onClick(DialogInterface dialog, int whichButton)
                                {
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e("FUCK","SerialActivity.onActivityResult(): Serial="+mClickedSerialId);
        /**
         * Case S1
         */
        /**
         * fillData (e.g. after serial adding) moved to onResume(), so it will be called from there
         */

        /**
         * Case S2, S5
         */
        if (!MyApplication.need2RebuildSerialList() && mClickedSerialId != -1)
        //if (mClickedSerialId != -1 && (requestCode == REQUEST_CODE_SERIAL_EDIT || requestCode == REQUEST_CODE_SERIAL_ENTER))
        {
            /**
             * mClickedSerialId != -1 here is needed for cases when activity was destroyed
             * Then log will be onCreate() -> onStart() -> onActivityResult() -> onResume()
             * and serial list will be recreated in onCreate() method!
             * instead of normal onActivityResult() -> onRestart() -> onStart() -> onResume()
             */
            updateData(mClickedSerialId);
        }
    }
}
