package ru.home.yoga.model;

public class Place extends EntityGeneric
{
    private final Integer mId;
    private final String mPlace;
    private final String mIcon;

    public Place(Integer id, String place, String icon)
    {
        mId = id;
        mPlace = place;
        mIcon = icon;
    }

    @Override
    public Integer getEntityId()
    {
        return mId;
    }

    @Override
    public String getEntityValue()
    {
        return mPlace;
    }

    public String getIcon()
    {
        return mIcon;
    }
}
