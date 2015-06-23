package ru.home.babylog;

public class ActivityItem
{
    Long mId;
    String mDate;
    Integer mWeight;
    String mFeed;
    String mEat;
    String mComments;

    public ActivityItem(long id, String date, int weight, String eat, String feed, String comments)
    {
        mId = id;
        mDate = date;
        mWeight = weight;
        mEat = eat;
        mFeed = feed;
        mComments = comments;
    }

}
