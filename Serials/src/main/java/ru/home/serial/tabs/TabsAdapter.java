package ru.home.serial.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import ru.home.serial.MyApplication;
import ru.home.serial.R;
import ru.home.serial.tabs.ViewPagerTabButton;

public class TabsAdapter
{
    private boolean mFixed;
	
	public TabsAdapter(boolean fixed)
    {
        mFixed = fixed;
	}
	
	public View getView(int position)
    {
        ViewPagerTabButton tab;
		
		LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mFixed)
		    tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_item_fixed, null);
        else
            tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_item_scrolled, null);
		
		tab.setText(MyApplication.getAppContext().getResources().getString(R.string.season) + " " + (position + 1));
		
		return tab;
	}
	
}
