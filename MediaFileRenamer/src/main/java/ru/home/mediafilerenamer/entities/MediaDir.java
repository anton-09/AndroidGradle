package ru.home.mediafilerenamer.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class MediaDir implements Parcelable
{
    private File mUrl;
    private String mPhotoCount;
    private String mVideoCount;

    public MediaDir(File url, String photoCount, String videoCount)
    {
        mUrl = url;
        mPhotoCount = photoCount;
        mVideoCount = videoCount;
    }

    public File getUrl()
    {
        return mUrl;
    }

    public String getPhotoCount()
    {
        return mPhotoCount;
    }

    public String getVideoCount()
    {
        return mVideoCount;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mUrl.getAbsolutePath());
        parcel.writeString(mPhotoCount);
        parcel.writeString(mVideoCount);
    }

    public static final Parcelable.Creator<MediaDir> CREATOR = new
            Parcelable.Creator<MediaDir>()
            {
                @Override
                public MediaDir createFromParcel(Parcel in) {
                    return new MediaDir(new File(in.readString()),in.readString(),in.readString());
                }

                @Override
                public MediaDir[] newArray(int size) {
                    return new MediaDir[size];
                }
            };

}
