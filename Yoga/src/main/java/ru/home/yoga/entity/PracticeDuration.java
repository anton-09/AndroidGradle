package ru.home.yoga.entity;

public class PracticeDuration extends EntityGeneric
{
    final Integer mId;
    final Double mDuration;

    public PracticeDuration(Integer id, Double duration)
    {
        mId = id;
        mDuration = duration;
    }

    public Integer getId()
    {
        return mId;
    }

    public Double getDurationValue()
    {
        return mDuration;
    }

    @Override
    public Integer getEntityId()
    {
        return mId;
    }
    @Override
    public String getEntityValue()
    {
        return mDuration.toString();
    }
}
