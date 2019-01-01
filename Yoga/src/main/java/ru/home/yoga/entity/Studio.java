package ru.home.yoga.entity;

public class Studio extends EntityGeneric
{
    final Integer mId;
    final String mName;
    final Integer mGroup;

    public Studio(Integer id, String name, Integer group)
    {
        mId = id;
        mName = name;
        mGroup = group;
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

    public boolean isGroup()
    {
        return (mGroup == 1);
    }
}
