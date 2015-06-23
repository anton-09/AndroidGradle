package ru.home.vertel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.*;

public class ImagePack implements Parcelable
{
    public String mName;
    public List<String> mResURIs;
    public List<String> mCheckedResURIs;
    public Drawable mCover;

    final int IMAGE_WIDTH = MyApplication.getAppContext().getResources().getDisplayMetrics().widthPixels / 4;
    final int IMAGE_HEIGHT = MyApplication.getAppContext().getResources().getDisplayMetrics().widthPixels / 4;

    ImagePack(String name, Object[] items)
    {
        mName = name;
        mResURIs = new ArrayList<String>();
        mCheckedResURIs = new ArrayList<String>();

        for (int i=0; i<items.length; i++)
        {
            mResURIs.add((String) items[i]);
            mCheckedResURIs.add((String) items[i]);
        }

        createCover();
    }

    ImagePack(Parcel in)
    {
        mName = in.readString();
        mResURIs = new ArrayList<String>();
        in.readList(mResURIs, null);
        mCheckedResURIs = new ArrayList<String>();
        in.readList(mCheckedResURIs, null);

        createCover();
    }

    public float getWinPercent()
    {
        if (mCheckedResURIs.size() < 1)
            return 100500.0f;
        if (mCheckedResURIs.size() < 2)
            return 100.0f;
        return 100.0f / (mCheckedResURIs.size()*mCheckedResURIs.size() - 1);
    }

    public void createCover()
    {
        Bitmap bitmapResult = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
        Bitmap source1, source2, source3, source4;
        if (mResURIs.size() == 0)
            mCover = null;
        else
        {
            Canvas canvas = new Canvas(bitmapResult);
            if (mResURIs.size() >= 1)
            {
                source1 = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mResURIs.get(0)), IMAGE_WIDTH/2, IMAGE_HEIGHT/2, false);
                canvas.drawBitmap(source1, 0, 0, null);
            }
            if (mResURIs.size() >= 2)
            {
                source2 = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mResURIs.get(1)), IMAGE_WIDTH/2, IMAGE_HEIGHT/2, false);
                canvas.drawBitmap(source2, IMAGE_WIDTH/2, 0, null);
            }
            if (mResURIs.size() >= 3)
            {
                source3 = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mResURIs.get(2)), IMAGE_WIDTH/2, IMAGE_HEIGHT/2, false);
                canvas.drawBitmap(source3, 0, IMAGE_HEIGHT/2, null);
            }
            if (mResURIs.size() >= 4)
            {
                source4 = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mResURIs.get(3)), IMAGE_WIDTH/2, IMAGE_HEIGHT/2, false);
                canvas.drawBitmap(source4, IMAGE_WIDTH/2, IMAGE_HEIGHT/2, null);
            }

            mCover = new BitmapDrawable(bitmapResult);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeList(mResURIs);
        parcel.writeList(mCheckedResURIs);
    }

    public static final Parcelable.Creator<ImagePack> CREATOR = new
            Parcelable.Creator<ImagePack>() {
                @Override
				public ImagePack createFromParcel(Parcel in) {
                    return new ImagePack(in);
                }

                @Override
				public ImagePack[] newArray(int size) {
                    return new ImagePack[size];
                }
            };
}