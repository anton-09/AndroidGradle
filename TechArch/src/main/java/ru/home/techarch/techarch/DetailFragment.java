package ru.home.techarch.techarch;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class DetailFragment extends Fragment
{
    private static final String ARG_HARDWARE_PHOTO = "hardware_photo";
    private static final String ARG_LIST_INDEX = "list_index";

    public static DetailFragment newInstance(String hardwarePhoto, int index)
    {
        Log.d("DetailFragment", "newInstance");
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HARDWARE_PHOTO, hardwarePhoto);
        args.putInt(ARG_LIST_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment()
    {
    }

    public int getIndex()
    {
        return getArguments().getInt(ARG_LIST_INDEX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("DetailFragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d("DetailFragment", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        if (getArguments() == null)
        {
            return;
        }

        String mHardwarePhoto = getArguments().getString(ARG_HARDWARE_PHOTO);

        try
        {
            InputStream inputStream = getActivity().getAssets().open("img/" + mHardwarePhoto);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            ((ImageView)getView().findViewById(R.id.imageView)).setImageDrawable(drawable);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
