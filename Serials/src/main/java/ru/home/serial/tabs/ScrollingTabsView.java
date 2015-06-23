package ru.home.serial.tabs;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import ru.home.serial.MyApplication;
import ru.home.serial.R;

public class ScrollingTabsView extends HorizontalScrollView implements OnPageChangeListener
{

    private ViewPager mPager;
    private TabsAdapter mAdapter;
    private LinearLayout mContainer;

    private static final int mDividerMarginTop = 12;
    private static final int mDividerMarginBottom = 12;
    private static final int mDividerWidth = 1;

    public ScrollingTabsView(Context context)
    {
        this(context, null);
    }

    public ScrollingTabsView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ScrollingTabsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs);

        setHorizontalScrollBarEnabled(false);
        setHorizontalFadingEdgeEnabled(false);

        mContainer = new LinearLayout(MyApplication.getAppContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(params);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);

        addView(mContainer);
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
        mContainer.removeAllViews();

        if (mAdapter == null) return;

        for (int i = 0; i < mPager.getAdapter().getCount(); i++)
        {
            final int index = i;

            View tab = mAdapter.getView(i);
            mContainer.addView(tab);

            tab.setFocusable(true);

            if (i != mPager.getAdapter().getCount() - 1)
            {
                mContainer.addView(getSeparator());
            }

            tab.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mPager.getCurrentItem() == index) selectTab(index);
                    else mPager.setCurrentItem(index);
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


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

        if (changed) selectTab(mPager.getCurrentItem());
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

        for (int i = 0, pos = 0; i < mContainer.getChildCount(); i += 2, pos++)
        {
            View tab = mContainer.getChildAt(i);
            tab.setSelected(pos == position);
        }

        View selectedTab = mContainer.getChildAt(position * 2);

        if (selectedTab != null)
        {
            final int w = selectedTab.getMeasuredWidth();
            final int l = selectedTab.getLeft();

            final int x = l - getWidth() / 2 + w / 2;

            smoothScrollTo(x, getScrollY());
        }

    }

}