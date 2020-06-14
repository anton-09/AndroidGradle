package ru.home.yoga.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.home.yoga.MyApplication;
import ru.home.yoga.R;
import ru.home.yoga.model.SummaryItem;
import ru.home.yoga.view.SimpleRecyclerViewDivider;
import ru.home.yoga.view.adapter.SummaryRecyclerViewAdapter;

public class TabPagerFragment extends Fragment
{
    private ArrayList<SummaryItem> mData;
    private String mTitle;
    private RecyclerView recyclerView;

    public static TabPagerFragment getInstance(String title, ArrayList<SummaryItem> data)
    {
        TabPagerFragment tabPagerFragment = new TabPagerFragment();
        tabPagerFragment.setTitle(title);
        tabPagerFragment.setData(data);
        return tabPagerFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tab_pager_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleRecyclerViewDivider(getContext()));
        SummaryRecyclerViewAdapter summaryRecyclerViewAdapter = new SummaryRecyclerViewAdapter(getContext(), mData);
        recyclerView.setAdapter(summaryRecyclerViewAdapter);
        return view;
    }

    public void setTitle(String title)
    {
        mTitle = MyApplication.mSummaryDbDateFormat.parseLocalDate(title).toString(MyApplication.mSummaryViewDateFormat);
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setData(ArrayList<SummaryItem> data)
    {
        mData = data;
    }
}
