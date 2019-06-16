package ru.home.mediafilerenamer.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class MediaDir implements Parcelable
{
    private File mUrl;
    private String mPhotoJpgCount;
    private String mVideoMp4Count;
    private String mPhotoJpegCount;

    public MediaDir(File url, String photoJpgCount, String videoMp4Count, String photoJpegCount)
    {
        mUrl = url;
        mPhotoJpgCount = photoJpgCount;
        mVideoMp4Count = videoMp4Count;
        mPhotoJpegCount = photoJpegCount;
    }

    public File getUrl()
    {
        return mUrl;
    }

    public String getPhotoJpgCount()
    {
        return mPhotoJpgCount;
    }

    public String getVideoMp4Count()
    {
        return mVideoMp4Count;
    }

    public String getPhotoJpegCount()
    {
        return mPhotoJpegCount;
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
        parcel.writeString(mPhotoJpgCount);
        parcel.writeString(mVideoMp4Count);
        parcel.writeString(mPhotoJpegCount);
    }

    public static final Parcelable.Creator<MediaDir> CREATOR = new
            Parcelable.Creator<MediaDir>()
            {
                @Override
                public MediaDir createFromParcel(Parcel in) {
                    return new MediaDir(new File(in.readString()),in.readString(),in.readString(),in.readString());
                }

                @Override
                public MediaDir[] newArray(int size) {
                    return new MediaDir[size];
                }
            };

}
