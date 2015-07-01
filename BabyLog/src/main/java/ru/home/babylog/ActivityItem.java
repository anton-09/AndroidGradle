package ru.home.babylog;

class ActivityItem
{
    final Long mId;
    final String mDate;
    final Integer mWeight;
    final String mFeed;
    final String mEat;
    final String mComments;

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
