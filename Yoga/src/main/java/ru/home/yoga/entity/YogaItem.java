package ru.home.yoga.entity;

public class YogaItem
{
    final Long mId;
    final String mDate;
    final Integer mPrice;
    final String mPayType;
    final Integer mPeople;
    final Type mType;
    final PracticeDuration mDuration;
    final Studio mStudio;
    final String mFIO;

    public YogaItem(String date, int price, String payType, int people, int type, int duration, int studio, String fio)
    {
        mId = -1L;
        mDate = date;
        mPrice = price;
        mPayType = payType;
        mPeople = people;
        mType = new Type(type, "");
        mDuration = new PracticeDuration(duration, -1D);
        mStudio = new Studio(studio, "", -1);
        mFIO = fio;
    }

    public YogaItem(long id, String date, int price, String payType, int people, Type type, PracticeDuration duration, Studio studio, String fio)
    {
        mId = id;
        mDate = date;
        mPrice = price;
        mPayType = payType;
        mPeople = people;
        mType = type;
        mDuration = duration;
        mStudio = studio;
        mFIO = fio;
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

    public String getPayType()
    {
        return mPayType;
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

    public String getFIO()
    {
        return mFIO;
    }
}

