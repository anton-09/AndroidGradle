package ru.home.serial;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class EpisodeActivity extends SherlockFragmentActivity
{
    private static final int IDM_ADD_NEW = 101;

    ViewPager mViewPager;
	TabsAdapter mTabsAdapter;

    private SparseArray<Fragment> mPages;



	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.tabs_container_sherlock);

        mViewPager = (ViewPager) findViewById(R.id.pager);


        Long serialId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID);
        Log.e("FUCK", "EpisodeActivity.onCreate1(): Serial=" + serialId);
        if (serialId == null)
        {
            serialId = getIntent().getLongExtra(SerialDbAdapter.TABLE_SERIALS + SerialDbAdapter.SERIALS_ID, -1);
        }
        Log.e("FUCK","EpisodeActivity.onCreate2(): Serial="+ serialId);


        Cursor serialDetails = MyApplication.getDBAdapter().getSerialDetails(serialId);
        serialDetails.moveToFirst();
        Serial mWorkingSerial = new Serial(serialDetails.getInt(0), serialDetails.getString(1), serialDetails.getString(2), serialDetails.getInt(3), serialDetails.getInt(4), serialDetails.getInt(5), serialDetails.getInt(6), serialDetails.getInt(7), serialDetails.getInt(8), 0, 0, 0, 0, 0, 0);
        serialDetails.close();

        mPages = new SparseArray<Fragment>();
        for (int seasonIndex = 0; seasonIndex < mWorkingSerial.mSeasonsCount; seasonIndex++)
        {
            Fragment tryToFindPreviouslyCreatedFragmentInFragmentManager = getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+seasonIndex);
            if (tryToFindPreviouslyCreatedFragmentInFragmentManager == null)
            {
                Bundle args = new Bundle();
                args.putParcelable("serial", mWorkingSerial);
                args.putInt("seasonIndex", seasonIndex + 1);

                mPages.put(seasonIndex, EpisodeFragment.newInstance(args));
            }
            else
            {
                mPages.put(seasonIndex, tryToFindPreviouslyCreatedFragmentInFragmentManager);
            }
        }

        mTabsAdapter = new TabsAdapter(this, mViewPager, mPages);

        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(mWorkingSerial.mName);
        if (!mWorkingSerial.mAltName.isEmpty())
            bar.setSubtitle(mWorkingSerial.mAltName);

        MyApplication.mAsyncTaskCount = 0;
        MyApplication.mAsyncTaskMax = mWorkingSerial.mSeasonsCount;
        MyApplication.resetAsyncEpisodeCount();
        MyApplication.mAsyncEpisodeMax = MyApplication.getDBAdapter().getEpisodeReleasedCountById(mWorkingSerial.mId);

        Cursor lastActionEpisode = MyApplication.getDBAdapter().getHistoryLastActionEpisode(serialId);
        lastActionEpisode.moveToFirst();
        if (!lastActionEpisode.isAfterLast())
        {
            mViewPager.setCurrentItem(lastActionEpisode.getInt(0)-1);
        }
        lastActionEpisode.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(Menu.NONE, IDM_ADD_NEW, Menu.NONE, R.string.add_episode)
                .setIcon(R.drawable.ab_addnew)
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
            case IDM_ADD_NEW:
                ((EpisodeFragment)mPages.get(mViewPager.getCurrentItem())).invokeAddNewEpisode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static class TabsAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener
	{
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final SparseArray<Fragment> mPages;

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager, SparseArray<Fragment> pages)
		{
			super(activity.getSupportFragmentManager());
			mActionBar = activity.getSupportActionBar();
            mPages = pages;
            mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
            mViewPager.setOffscreenPageLimit(mPages.size() - 1);
            mViewPager.setPageMargin(1);

            for (int i=0; i<getCount(); i++)
            {
                ActionBar.Tab tab = mActionBar.newTab();
                tab.setText(MyApplication.getAppContext().getResources().getString(R.string.season) + " " + ((EpisodeFragment)mPages.get(i)).getFragmentTag());
                tab.setTag(((EpisodeFragment)mPages.get(i)).getFragmentTag());
                tab.setTabListener(this);
                mActionBar.addTab(tab);
            }
		}

		@Override
		public int getCount()
		{
			return mPages.size();
		}

		@Override
		public Fragment getItem(int position)
		{
			return mPages.get(position);
		}

		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels)
		{
		}

		public void onPageSelected(int position)
		{
            mActionBar.setSelectedNavigationItem(position);
        }

		public void onPageScrollStateChanged(int state)
		{
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft)
		{
			Object tag = tab.getTag();
			for (int i = 0; i < mPages.size(); i++)
			{
				if (((EpisodeFragment)mPages.get(i)).getFragmentTag() == tag)
				{
                    if (mViewPager.getCurrentItem()!=i)
                    {
                        mViewPager.setCurrentItem(i);
                    }
				}
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft)
		{
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft)
		{
		}
	}
}