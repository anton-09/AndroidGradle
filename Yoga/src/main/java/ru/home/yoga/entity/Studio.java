package ru.home.yoga.entity;

public class Studio extends EntityGeneric
{
    final Integer mId;
    final String mName;

    public Studio(Integer id, String name)
    {
        mId = id;
        mName = name;
    }

    public Integer getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    @Override
    public Integer getEntityId()
    {
        return mId;
    }

    @Override
    public String getEntityValue()
    {
        return mName;
    }
}
