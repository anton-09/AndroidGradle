package ru.home.serial;

import android.os.Parcel;
import android.os.Parcelable;

public class Serial implements Parcelable
{
    public long mId;
    public String mName;
    public String mAltName;
    public int mSeasonsCount;
    public int mEpisodeWithName;
    public int mColumnNumber;
    public int mIsActive;
    public int mReleaseStudioId;
    public int mTranslateStudioId;
    public int mReleasedCount;
    public int mDownloadedCount;
    public int mWatchedCount;
    public long mMinEpisodeDate;
    public long mMaxEpisodeDate;
    public long mLastAction;
    
    public Serial(long id, String name, String altName, int seasonsCount, int episodeWithName, int columnNumber, int isActive, int releaseStudioId, int translateStudioId, int releasedCount, int downloadedCount, int watchedCount, long minEpisodeDate, long maxEpisodeDate, long lastAction)
    {
        mId = id;
        mName = name;
        mAltName = altName;
        mSeasonsCount = seasonsCount;
        mEpisodeWithName = episodeWithName;
        mColumnNumber = columnNumber;
        mIsActive = isActive;
        mReleaseStudioId = releaseStudioId;
        mTranslateStudioId = translateStudioId;
        mReleasedCount = releasedCount;
        mDownloadedCount = downloadedCount;
        mWatchedCount = watchedCount;
        mMinEpisodeDate = minEpisodeDate;
        mMaxEpisodeDate = maxEpisodeDate;
        mLastAction = lastAction;
    }

    public boolean IsEpisodeWithName()
    {
        return mEpisodeWithName == 1;
    }

    public boolean IsActive()
    {
        return mIsActive == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(mName);
        parcel.writeString(mAltName);
        parcel.writeInt(mSeasonsCount);
        parcel.writeInt(mEpisodeWithName);
        parcel.writeInt(mColumnNumber);
        parcel.writeInt(mIsActive);
        parcel.writeInt(mReleaseStudioId);
        parcel.writeInt(mTranslateStudioId);
        parcel.writeInt(mReleasedCount);
        parcel.writeInt(mDownloadedCount);
        parcel.writeInt(mWatchedCount);
        parcel.writeLong(mMinEpisodeDate);
        parcel.writeLong(mMaxEpisodeDate);
        parcel.writeLong(mLastAction);
    }

    public static final Parcelable.Creator<Serial> CREATOR = new
            Parcelable.Creator<Serial>() {
                @Override
				public Serial createFromParcel(Parcel in) {
                    return new Serial(in.readLong(),in.readString(),in.readString(),in.readInt(),in.readInt(),in.readInt(),in.readInt(),in.readInt(),in.readInt(),in.readInt(),in.readInt(),in.readInt(),in.readLong(),in.readLong(),in.readLong());
                }

                @Override
				public Serial[] newArray(int size) {
                    return new Serial[size];
                }
            };
}
