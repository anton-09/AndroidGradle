package ru.home.fitness.entities;

import java.util.Date;

public class Action
{
    private final Long mId;
    private final Exercise mExercise;
    private final Date mDate;
    private final String mComment;
    private final String mWorkoutName;

    public Action(Long id, Date date, String comment, String workoutName, Exercise exercise)
    {
        mId = id;
        mDate = date;
        mComment = comment;
        mWorkoutName = workoutName;
        mExercise = exercise;
    }

    public Long getId()
    {
        return mId;
    }

    public Exercise getExercise()
    {
        return mExercise;
    }

    public Date getDate()
    {
        return mDate;
    }

    public String getComment()
    {
        return mComment;
    }

    public String getWorkoutName()
    {
        return mWorkoutName;
    }
}
