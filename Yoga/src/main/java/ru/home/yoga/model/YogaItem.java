package ru.home.yoga.model;

public class YogaItem
{
    private final Long mId;
    private final String mDate;
    private final Integer mPrice;
    private final String mPayType;
    private final Integer mPeople;
    private final Type mType;
    private final PracticeDuration mDuration;
    private final Studio mStudio;
    private final Place mPlace;
    private final String mComment;

    public YogaItem(String date, int price, String payType, int people, int type, int duration, int studio, int place, String comment)
    {
        mId = -1L;
        mDate = date;
        mPrice = price;
        mPayType = payType;
        mPeople = people;
        mType = new Type(type, "");
        mDuration = new PracticeDuration(duration, -1D);
        mStudio = new Studio(studio, "", -1, "");
        mPlace = new Place(place, "", "");
        mComment = comment;
    }

    public YogaItem(long id, String date, int price, String payType, int people, Type type, PracticeDuration duration, Studio studio, Place place, String comment)
    {
        mId = id;
        mDate = date;
        mPrice = price;
        mPayType = payType;
        mPeople = people;
        mType = type;
        mDuration = duration;
        mStudio = studio;
        mPlace = place;
        mComment = comment;
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

    public Place getPlace()
    {
        return mPlace;
    }

    public String getComment()
    {
        return mComment;
    }
}

