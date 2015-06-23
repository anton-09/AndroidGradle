package ru.home.serial.popup;

import android.graphics.drawable.Drawable;

public class ActionItem
{
	public Drawable mIcon;
	public String mTitle;
	public int mActionId = -1;
    public boolean mIsSticky;
	
    public ActionItem(int actionId, String title, Drawable icon)
    {
        mTitle = title;
        mIcon = icon;
        mActionId = actionId;
    }
}