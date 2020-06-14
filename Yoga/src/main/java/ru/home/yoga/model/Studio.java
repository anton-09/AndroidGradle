package ru.home.yoga.model;

public class Studio extends EntityGeneric
{
    private final Integer mId;
    private final String mName;
    private final Integer mGroup;

    private final String mIcon;

    public Studio(Integer id, String name, Integer group, String icon)
    {
        mId = id;
        mName = name;
        mGroup = group;
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
        return mName;
    }

    public boolean isGroup()
    {
        return (mGroup == 1);
    }

    public String getIcon()
    {
        return mIcon;
    }
}
