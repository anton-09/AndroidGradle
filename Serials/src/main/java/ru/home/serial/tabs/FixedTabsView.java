package ru.home.serial.tabs;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import ru.home.serial.MyApplication;
import ru.home.serial.R;

public class FixedTabsView extends LinearLayout implements ViewPager.OnPageChangeListener
{
	private ViewPager mPager;
	private TabsAdapter mAdapter;

	private static final int mDividerMarginTop = 12;
	private static final int mDividerMarginBottom = 21;
    private static final int mDividerWidth = 1;

	public FixedTabsView(Context context)
    {
		this(context, null);
	}

	public FixedTabsView(Context context, AttributeSet attrs)
    {
		this(context, attrs, 0);
	}

	public FixedTabsView(Context context, AttributeSet attrs, int defStyle)
    {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
	}

	public void setAdapter(TabsAdapter adapter)
    {
		mAdapter = adapter;

		if (mPager != null && mAdapter != null) initTabs();
	}

	public void setViewPager(ViewPager pager)
    {
		mPager = pager;
		mPager.setOnPageChangeListener(this);

		if (mPager != null && mAdapter != null) initTabs();
	}

	private void initTabs()
    {
		removeAllViews();

		if (mAdapter == null) return;

		for (int i = 0; i < mPager.getAdapter().getCount(); i++)
        {
			final int index = i;

			View tab = mAdapter.getView(i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
			tab.setLayoutParams(params);
			addView(tab);

			if (i != mPager.getAdapter().getCount() - 1)
            {
				addView(getSeparator());
			}

			tab.setOnClickListener(new OnClickListener()
            {
				@Override
				public void onClick(View v)
                {
					mPager.setCurrentItem(index);
				}
			});

		}

		selectTab(mPager.getCurrentItem());
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position)
    {
		selectTab(position);
	}


	private View getSeparator()
    {
		View v = new View(MyApplication.getAppContext());

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDividerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
		params.setMargins(0, mDividerMarginTop, 0, mDividerMarginBottom);
		v.setLayoutParams(params);

		v.setBackgroundColor(getResources().getColor(R.color.tabs_divider));

		return v;
	}

	private void selectTab(int position)
    {
		for (int i = 0, pos = 0; i < getChildCount(); i++)
        {
			if (getChildAt(i) instanceof ViewPagerTabButton)
            {
				getChildAt(i).setSelected(pos == position);
				pos++;
			}
		}
	}

}