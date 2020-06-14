package ru.home.yoga.model;

public class Type extends EntityGeneric
{
    private final Integer mId;
    private final String mType;

    public Type(Integer id, String type)
    {
        mId = id;
        mType = type;
    }

    @Override
    public Integer getEntityId()
    {
        return mId;
    }

    @Override
    public String getEntityValue()
    {
        return mType;
    }
}
