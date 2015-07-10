package ru.home.fitness.entities;

public class Exercise
{
    private final Long mId;
    private final String mName;
    private final Muscle mMuscle;

    public Exercise(Long id, String name, Muscle muscle)
    {
        mId = id;
        mName = name;
        mMuscle = muscle;
    }

    public Long getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public Muscle getMuscle()
    {
        return mMuscle;
    }

}
