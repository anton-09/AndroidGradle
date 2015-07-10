package ru.home.fitness.entities;

import java.util.ArrayList;
import java.util.Date;

public class Workout
{
    private final Long mId;
    private final String mName;
    private final Date mStartDate;
    private final Date mEndDate;
    private final ArrayList<Exercise> mExerciseLise;

    public Workout(Long id, String name, Date startDate, Date endDate)
    {
        mId = id;
        mName = name;
        mStartDate = startDate;
        mEndDate = endDate;
        mExerciseLise = new ArrayList<Exercise>();
    }

    public Long getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public Date getEndDate()
    {
        return mEndDate;
    }

    public void addExercise(Exercise exercise)
    {
        mExerciseLise.add(exercise);
    }

}
