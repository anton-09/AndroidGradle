package ru.home.fitness.entities;

public class Muscle
{
    private final Long mId;
    private final String mName;

    public Muscle(Long id, String name)
    {
        mId = id;
        mName = name;
    }

    public Long getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }
}
