package ru.home.yoga.entity;


public class SummaryItem
{
    String mStudioName;
    Integer mCount;
    Integer mPrice;

    public SummaryItem(String studioName, Integer count, Integer price)
    {
        mStudioName = studioName;
        mCount = count;
        mPrice = price;
    }

    public String getStudioName()
    {
        return mStudioName;
    }

    public Integer getCount()
    {
        return mCount;
    }

    public Integer getPrice()
    {
        return mPrice;
    }
}
