package ru.home.yoga.entity;

public class Type extends EntityGeneric
{
    final Integer mId;
    final String mType;

    public Type(Integer id, String type)
    {
        mId = id;
        mType = type;
    }

    public Integer getId()
    {
        return mId;
    }

    public String getTypeName()
    {
        return mType;
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
