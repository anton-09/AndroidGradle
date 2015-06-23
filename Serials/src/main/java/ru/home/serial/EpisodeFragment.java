package ru.home.serial;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import ru.home.serial.popup.ActionItem;
import ru.home.serial.popup.QuickActionPopupWindow;

import java.util.ArrayList;

public class EpisodeFragment extends SherlockFragment
{
    private static final int ID_DELETE = 1;
    private static final int REQUEST_CODE_EPISODE_EDIT = 1;
    private static final int REQUEST_CODE_EPISODE_ADD = 2;
    private static final int ASYNC_TASK_FINISHED = 1;
    private static final int ASYNC_TASK_INTERRUPTED = 2;

    private LayoutInflater mLayoutInflater;

    private Serial mWorkingSerial;

    private Long mClickedEpisodeId = -1l;

    private int mTag;

    private ViewGroup mListContainer;

    private QuickActionPopupWindow mQuickAction;

    private EpisodeActivity mHostingActivity;

    private FillNewPageTask mFillNewPageTask;

    private boolean mNeed2Rebuild;

    public static EpisodeFragment newInstance(Bundle args)
    {
        EpisodeFragment f = new EpisodeFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mWorkingSerial = getArguments().getParcelable("serial");
        mTag = getArguments().getInt("seasonIndex");

        Log.e("FUCK", "Fragment.onCreate(): Fragment tag = " + mTag + " mIsAsyncTaskRunning=" + ((mFillNewPageTask == null)?"NULL":mFillNewPageTask.getStatus()));
    }

    @Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState)
	{
        mLayoutInflater = layoutInflater;
        return mLayoutInflater.inflate(R.layout.list_container, null);
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        mHostingActivity = (EpisodeActivity) getSherlockActivity();
        mListContainer = (ViewGroup) getView().findViewById(R.id.serial_list);

        initPopupMenu();

        mNeed2Rebuild = true;

        Log.e("FUCK","Fragment.onActivityCreated(): Fragment tag = "+mTag+" mIsAsyncTaskRunning=" + ((mFillNewPageTask == null)?"NULL":mFillNewPageTask.getStatus()));
    }


    public void invokeAddNewEpisode()
    {
        /**
         * Case EP1
         */
        Intent intent = new Intent(mHostingActivity, EpisodeEditActivity.class);
        intent.putExtra(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_SEASON, mTag);
        intent.putExtra(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, mWorkingSerial.mId);
        startActivityForResult(intent, REQUEST_CODE_EPISODE_ADD);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.e("FUCK","Fragment.onResume(): Fragment tag = " + mTag);

        if (mNeed2Rebuild)
            fillData();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.e("FUCK", "Fragment.onPause(): Fragment tag = " + mTag + " mIsAsyncTaskRunning=" + ((mFillNewPageTask == null) ? "NULL" : mFillNewPageTask.getStatus()));

        if (mFillNewPageTask != null && mFillNewPageTask.getStatus() != AsyncTask.Status.FINISHED)
        {
            mNeed2Rebuild = true;
            mFillNewPageTask.cancel(true);
        }
    }

    public Object getFragmentTag()
    {
        return getArguments().getInt("seasonIndex");
    }


    public void fillData()
    {
        Log.e("FUCK","Fragment.fillData(): Fragment tag = "+mTag+" mIsAsyncTaskRunning=" + ((mFillNewPageTask == null)?"NULL":mFillNewPageTask.getStatus()));

        mNeed2Rebuild = false;

        mFillNewPageTask = new FillNewPageTask();
        mFillNewPageTask.execute();
    }

    public void updateData()
    {
        new UpdateExistingPageTask().execute();
    }

    class FillNewPageTask extends AsyncTask<Void, Object, Integer>
    {
        @Override
        protected void onPreExecute()
        {
            mListContainer.removeAllViews();
            mHostingActivity.setSupportProgress(0);
        }

        @Override
        protected void onProgressUpdate(Object... params)
        {
            if (isCancelled())
                return;

            mHostingActivity.setSupportProgress((Integer) params[0]);

            if (params[1] != null)
                mListContainer.addView((View) params[1]);
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            int containerIndex = 0;

            ArrayList<Episode> mEpisodeArrayList = new ArrayList<Episode>();

            View view;
            LinearLayout linearLayout;


            mEpisodeArrayList.clear();
            Cursor episodeCursor = MyApplication.getDBAdapter().getEpisodes(mWorkingSerial.mId, mTag);
            episodeCursor.moveToFirst();
            while(!episodeCursor.isAfterLast())
            {
                // Finish if activity is left
                if (isCancelled())
                {
                    episodeCursor.close();
                    mNeed2Rebuild = true;
                    return ASYNC_TASK_INTERRUPTED;
                }
                Episode episode = new Episode(episodeCursor.getInt(0), episodeCursor.getInt(1), episodeCursor.getInt(2), episodeCursor.getString(3), episodeCursor.getLong(4), episodeCursor.getLong(5), episodeCursor.getLong(6), episodeCursor.getInt(7));
                mEpisodeArrayList.add(episode);
                episodeCursor.moveToNext();
            }
            episodeCursor.close();



            linearLayout = new LinearLayout(mHostingActivity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(layoutParams);




            for (Episode obj: mEpisodeArrayList)
            {
                // Finish if activity is left
                if (isCancelled())
                {
                    mNeed2Rebuild = true;
                    return ASYNC_TASK_INTERRUPTED;
                }


                if (mWorkingSerial.IsEpisodeWithName() || mWorkingSerial.mColumnNumber == 1)
                {
                    if (containerIndex == 0 && mEpisodeArrayList.size() == 1)
                        view = mLayoutInflater.inflate(R.layout.list_item_single, linearLayout, false);
                    else if (containerIndex == 0 && mEpisodeArrayList.size() > 1)
                        view = mLayoutInflater.inflate(R.layout.list_item_top, linearLayout, false);
                    else if (containerIndex == mEpisodeArrayList.size() - 1)
                        view = mLayoutInflater.inflate(R.layout.list_item_bottom, linearLayout, false);
                    else
                        view = mLayoutInflater.inflate(R.layout.list_item_middle, linearLayout, false);

                    ((TextView) view.findViewById(R.id.list_item_name)).setText(String.format(MyApplication.mEpisodeNameFormat, obj.mNumber, obj.mName));
                }
                else
                {
                    if (mEpisodeArrayList.size() <= mWorkingSerial.mColumnNumber && containerIndex % mWorkingSerial.mColumnNumber == 0)
                        view = mLayoutInflater.inflate(R.layout.list_item_left, linearLayout, false);
                    else if (mEpisodeArrayList.size() <= mWorkingSerial.mColumnNumber && (containerIndex + 1) % mWorkingSerial.mColumnNumber == 0)
                        view = mLayoutInflater.inflate(R.layout.list_item_right, linearLayout, false);
                    else if (containerIndex / mWorkingSerial.mColumnNumber == 0 && containerIndex % mWorkingSerial.mColumnNumber == 0)
                        view = mLayoutInflater.inflate(R.layout.list_item_top_left, linearLayout, false);
                    else if (containerIndex / mWorkingSerial.mColumnNumber == (mEpisodeArrayList.size() - 1) / mWorkingSerial.mColumnNumber && containerIndex % mWorkingSerial.mColumnNumber == 0)
                        view = mLayoutInflater.inflate(R.layout.list_item_bottom_left, linearLayout, false);
                    else if (containerIndex / mWorkingSerial.mColumnNumber == 0 && (containerIndex + 1) % mWorkingSerial.mColumnNumber == 0)
                        view = mLayoutInflater.inflate(R.layout.list_item_top_right, linearLayout, false);
                    else if (containerIndex / mWorkingSerial.mColumnNumber == (mEpisodeArrayList.size() - 1) / mWorkingSerial.mColumnNumber && (containerIndex + 1) % mWorkingSerial.mColumnNumber == 0)
                        view = mLayoutInflater.inflate(R.layout.list_item_bottom_right, linearLayout, false);
                    else
                        view = mLayoutInflater.inflate(R.layout.list_item_middle, linearLayout, false);

                    ((TextView) view.findViewById(R.id.list_item_name)).setText(String.format(MyApplication.mEpisodeWithoutNameFormat, obj.mNumber));
                }


                view.setTag(obj.mId);
                view.setOnClickListener(new View.OnClickListener()
                {
                    /**
                     * Case EP2, EP3
                     */
                    @Override
                    public void onClick(View view)
                    {
                        mClickedEpisodeId = Long.parseLong(view.getTag().toString());
                        Intent intent = new Intent(mHostingActivity, EpisodeEditActivity.class);
                        intent.putExtra(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_SEASON, mTag);
                        intent.putExtra(SerialDbAdapter.TABLE_EPISODES + SerialDbAdapter.EPISODES_ID, mClickedEpisodeId);
                        intent.putExtra(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, mWorkingSerial.mId);
                        startActivityForResult(intent, REQUEST_CODE_EPISODE_EDIT);
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener()
                {
                    /**
                     * Case EP4
                     */
                    @Override
                    public boolean onLongClick(View view)
                    {
                        mClickedEpisodeId = Long.parseLong(view.getTag().toString());
                        mQuickAction.show(view);
                        return true;
                    }
                });

                // Set custom LinearLayout states
                ((ExtendedStateRelativeLayout)view).setReleased(obj.isReleased());
                ((ExtendedStateRelativeLayout)view).setDownloaded(obj.isDownloaded());
                ((ExtendedStateRelativeLayout)view).setWatched(obj.isWatched());

                if (mWorkingSerial.IsEpisodeWithName() || (containerIndex + 1) % mWorkingSerial.mColumnNumber == 0)
                {
                    linearLayout.addView(view);
                    MyApplication.incrementAsyncEpisodeCount();
                    publishProgress(MyApplication.mAsyncEpisodeMax == 0 ? 10000 : MyApplication.getAsyncEpisodeCount() * 10000 / MyApplication.mAsyncEpisodeMax, linearLayout);
                    linearLayout = new LinearLayout(mHostingActivity);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setLayoutParams(layoutParams);
                }
                else
                {
                    linearLayout.addView(view);
                    MyApplication.incrementAsyncEpisodeCount();
                    publishProgress(MyApplication.mAsyncEpisodeMax == 0 ? 10000 : MyApplication.getAsyncEpisodeCount() * 10000 / MyApplication.mAsyncEpisodeMax, null);
                }

                containerIndex++;

            }

            if (linearLayout.getChildCount() != 0)
            {
                int toAdd = mWorkingSerial.mColumnNumber - linearLayout.getChildCount();
                for (int i = 0; i < toAdd; i++)
                {
                    mLayoutInflater.inflate(R.layout.invisible_formatter, linearLayout, true);
                }

                publishProgress(MyApplication.mAsyncEpisodeMax == 0 ? 10000 : MyApplication.getAsyncEpisodeCount() * 10000 / MyApplication.mAsyncEpisodeMax, linearLayout);
            }

            return ASYNC_TASK_FINISHED;
        }



        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);

            Log.e("FUCK","Fragment.onPostExecute(): Fragment tag = "+mTag+" mIsAsyncTaskRunning=" + ((mFillNewPageTask == null)?"NULL":mFillNewPageTask.getStatus()));
        }
    }


    class UpdateExistingPageTask extends AsyncTask<Void, Void, Void>
    {
        ExtendedStateRelativeLayout updatedView;
        Episode episode;

        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            updatedView = (ExtendedStateRelativeLayout) getView().findViewWithTag(mClickedEpisodeId);

            Cursor episodeCursor = MyApplication.getDBAdapter().getEpisodeDetails(mClickedEpisodeId);
            episodeCursor.moveToFirst();
            episode = new Episode(episodeCursor.getInt(0), episodeCursor.getInt(1), episodeCursor.getInt(2), episodeCursor.getString(3), episodeCursor.getLong(4), episodeCursor.getLong(5), episodeCursor.getLong(6), episodeCursor.getInt(7));

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (mWorkingSerial.IsEpisodeWithName() || mWorkingSerial.mColumnNumber == 1)
            {
                ((TextView) updatedView.findViewById(R.id.list_item_name)).setText(String.format(MyApplication.mEpisodeNameFormat, episode.mNumber, episode.mName));
            }
            else
            {
                ((TextView) updatedView.findViewById(R.id.list_item_name)).setText(String.format(MyApplication.mEpisodeWithoutNameFormat, episode.mNumber));
            }

            updatedView.setReleased(episode.isReleased());
            updatedView.setDownloaded(episode.isDownloaded());
            updatedView.setWatched(episode.isWatched());
        }
    }


    public void initPopupMenu()
    {
        ActionItem deleteEpisode = new ActionItem(ID_DELETE, getResources().getString(R.string.delete_episode), getResources().getDrawable(R.drawable.ic_delete));
        mQuickAction = new QuickActionPopupWindow();
        mQuickAction.addActionItem(deleteEpisode);
        mQuickAction.setOnActionItemClickListener(new QuickActionPopupWindow.OnActionItemClickListener()
        {
            @Override
            public void onItemClick(QuickActionPopupWindow quickAction, int pos, int actionId)
            {


                /**
                 * Case EP4
                 */
                if (actionId == ID_DELETE)
                {
                    MyApplication.getDBAdapter().deleteHistoryEpisode(mClickedEpisodeId);
                    MyApplication.getDBAdapter().deleteEpisode(mClickedEpisodeId);


                    if (MyApplication.mShowSerialFormat == 3)
                        MyApplication.setNeed2RebuildSerialList(true);

                    mFillNewPageTask.cancel(true);
                    fillData();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e("FUCK", "Fragment.onActivityResult(): Fragment tag = "+mTag+" Serial=" + mWorkingSerial.mId + " Episode=" + mClickedEpisodeId);

        mNeed2Rebuild = mNeed2Rebuild || intent.getBooleanExtra("Need2RebuildEpisodeList", mNeed2Rebuild);

        /**
         * Case EP1, EP3
         */
        /**
         * fillData (e.g. after episode editing) moved to onResume(), so it will be called from there
         */

        /**
         * Case EP2
         */
        if (!mNeed2Rebuild && mClickedEpisodeId != -1)
        //else if (mClickedEpisodeId != -1 && requestCode == REQUEST_CODE_EPISODE_EDIT)
        {
            /**
             * mClickedEpisodeId != -1 here is needed for cases when activity was destroyed
             * Then log will be onCreate() -> onStart() -> onActivityResult() -> onResume()
             * and episode list will be recreated in onCreate() method!
             * instead of normal onActivityResult() -> onRestart() -> onStart() -> onResume()
             */
            updateData();
        }

    }
}
