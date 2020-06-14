package ru.home.yoga.model;


public class SummaryItem
{
    private String mStudioName;
    private Integer mCount;
    private Integer mPrice;

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
