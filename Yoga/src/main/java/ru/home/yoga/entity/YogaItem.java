package ru.home.yoga.entity;

public class YogaItem
{
    final Long mId;
    final String mDate;
    final Integer mPrice;
    final Integer mPeople;
    final Type mType;
    final PracticeDuration mDuration;
    final Studio mStudio;

    public YogaItem(long id, String date, int price, int people, Type type, PracticeDuration duration, Studio studio)
    {
        mId = id;
        mDate = date;
        mPrice = price;
        mPeople = people;
        mType = type;
        mDuration = duration;
        mStudio = studio;
    }

    public Long getId()
    {
        return mId;
    }

    public String getDate()
    {
        return mDate;
    }

    public Integer getPrice()
    {
        return mPrice;
    }

    public Integer getPeople()
    {
        return mPeople;
    }

    public Type getType()
    {
        return mType;
    }

    public PracticeDuration getDuration()
    {
        return mDuration;
    }

    public Studio getStudio()
    {
        return mStudio;
    }
}

