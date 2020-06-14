package ru.home.yoga.model;

public class PracticeDuration extends EntityGeneric
{
    private final Integer mId;
    private final Double mDuration;

    public PracticeDuration(Integer id, Double duration)
    {
        mId = id;
        mDuration = duration;
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
