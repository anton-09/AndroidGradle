package ru.home.serial;

public class Episode
{
    public long mId;
    public int mNumber;
    public int mSeason;
    public String mName;
    public long mReleaseDate;
    public long mDownloadDate;
    public long mWatchDate;
    public long mSerialId;

    public Episode(long id, int number, int season, String name, long releaseDate, long downloadDate, long watchDate, long serialId)
    {
        mId = id;
        mNumber = number;
        mSeason = season;
        mName = name;
        mReleaseDate = releaseDate;
        mDownloadDate = downloadDate;
        mWatchDate = watchDate;
        mSerialId = serialId;
    }

    public boolean isReleased()
    {
        return (mReleaseDate != -1) && (mDownloadDate == -1) && (mWatchDate == -1);
    }

    public boolean isDownloaded()
    {
        return (mReleaseDate != -1) && (mDownloadDate != -1) && (mWatchDate == -1);
    }

    public boolean isWatched()
    {
        return (mReleaseDate != -1) && (mDownloadDate != -1) && (mWatchDate != -1);
    }
}
