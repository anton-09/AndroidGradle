package ru.home.techarch.techarch;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContainerFragment extends ListFragment
{
    private static final String ARG_SELECTED_ITEM = "selected_item";
    private static final String ARG_HARDWARE_OWNER = "hardware_owner";

    private String mHardwareOwner;

    private boolean mDualPane;
    private boolean forceUpdateDetail = false;

    private int mCurrentCheckedPosition;

    String jsonStorage;
    List<Hardware> listContents = new ArrayList<Hardware>();

    public static ContainerFragment newInstance(String name)
    {
        Log.d("ContainerFragment", "newInstance");
        ContainerFragment fragment = new ContainerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HARDWARE_OWNER, name);
        fragment.setArguments(args);
        return fragment;
    }

    public ContainerFragment()
    {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d("ContainerFragment", "onActivityCreated savedInstanceState is null = " + (savedInstanceState==null));
        super.onActivityCreated(savedInstanceState);

        mHardwareOwner = getArguments().getString(ARG_HARDWARE_OWNER);


        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null)
        {
            mCurrentCheckedPosition = savedInstanceState.getInt(ARG_SELECTED_ITEM);
        }


        try
        {
            InputStream is = getActivity().getAssets().open("json/" + mHardwareOwner) ;
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonStorage = new String(buffer);
            parseJSON(false);
        }
        catch (JSONException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

    }

    @Override public void onListItemClick(ListView l, View v, int position, long id)
    {
        Log.d("ContainerFragment", "onListItemClick");
        mCurrentCheckedPosition = position;
        getListView().setItemChecked(mCurrentCheckedPosition, true);
        showDetails();
    }

    private void showDetails()
    {
        if (mDualPane)
        {
            // Check what fragment is currently shown, replace if needed.
            DetailFragment details = (DetailFragment) getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getIndex() != mCurrentCheckedPosition || forceUpdateDetail)
            {
                forceUpdateDetail = false;

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                        .replace(R.id.details, DetailFragment.newInstance(((Hardware) getListView().getItemAtPosition(mCurrentCheckedPosition)).mPhoto, mCurrentCheckedPosition))
                        .commit();
            }
        }
        else
        {
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailActivity.class);
            intent.putExtra("photo", ((Hardware) getListView().getItemAtPosition(mCurrentCheckedPosition)).mPhoto);
            intent.putExtra("owner", mHardwareOwner);
            intent.putExtra("position", mCurrentCheckedPosition);
            startActivity(intent);
        }
    }


    public void parseJSON(boolean redraw) throws JSONException
    {
        Log.d("ContainerFragment", "parseJSON");
        JSONObject obj = new JSONObject(jsonStorage);
        JSONArray jsonArray = obj.getJSONArray(((TechArchActivity)getActivity()).mCurrentMode);

        listContents.clear();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Hardware hardware =  new Hardware(jsonObject.getString("name"), jsonObject.getString("date"), jsonObject.getString("photo"));
            listContents.add(hardware);
        }

        forceUpdateDetail = true;
        if (redraw) mCurrentCheckedPosition = 0;

        setListAdapter(new HardwareArrayAdapter(getActivity(), R.layout.hardware_item, listContents));
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        getListView().setItemChecked(mCurrentCheckedPosition, true);

        if (mDualPane)
        {
            showDetails();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d("ContainerFragment", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_ITEM, mCurrentCheckedPosition);
    }
}