package ru.home.yoga;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.home.yoga.entity.SummaryItem;

public class TabPagerFragmentAdapter extends FragmentPagerAdapter
{
    private Map<Integer, TabPagerFragment> tabs;

    public TabPagerFragmentAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
        tabs = new HashMap<>();
    }

    public CharSequence getPageTitle(int position)
    {
        return tabs.get(position).getTitle();
    }

    public Fragment getItem(int position)
    {
        return tabs.get(position);
    }

    public int getCount()
    {
        return tabs.size();
    }

    public void addTab(String title, ArrayList<SummaryItem> data)
    {
        tabs.put(tabs.size(), TabPagerFragment.getInstance(title, data));
    }
}
