package ru.home.vertel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter
{
    private LayoutInflater mLayoutInflater;
    public ImagePack mImagePack;

    final int IMAGE_WIDTH = MyApplication.getAppContext().getResources().getDisplayMetrics().widthPixels / MyApplication.getCropFactor();
    final int IMAGE_HEIGHT = MyApplication.getAppContext().getResources().getDisplayMetrics().widthPixels / MyApplication.getCropFactor();

    public GridAdapter(ImagePack imagePack)
    {
        mLayoutInflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mImagePack = imagePack;
    }

    @Override
    public int getCount()
    {
        return mImagePack.mResURIs.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mImagePack.mResURIs.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            view = mLayoutInflater.inflate(R.layout.grid_cell, parent, false);
        }

        Bitmap bitmap = BitmapFactory.decodeFile((String)getItem(position));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
        ImageView imageView = (ImageView) view.findViewById(R.id.cell_image);
        imageView.setImageBitmap(scaled);

        ImageView borderView = (ImageView) view.findViewById(R.id.cell_border);
        if (mImagePack.mCheckedResURIs.contains(getItem(position)))
            borderView.setVisibility(View.VISIBLE);
        else
            borderView.setVisibility(View.INVISIBLE);

        return view;
    }
}