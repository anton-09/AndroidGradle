package ru.home.babylog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class EntityAdapter extends BaseAdapter
{
    private LayoutInflater mLayoutInflater;
    private ArrayList<ActivityItem> mList;

    public EntityAdapter(Context context)
    {
        mList = new ArrayList<ActivityItem>();
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataModel(ArrayList<ActivityItem> dataModel)
    {
        mList = dataModel;
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
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
            view = mLayoutInflater.inflate(R.layout.main_list_item, parent, false);
        }

        TextView itemDate = (TextView) view.findViewById(R.id.listItemDate);
        TextView itemWeight = (TextView) view.findViewById(R.id.listItemWeight);
        TextView itemEat = (TextView) view.findViewById(R.id.listItemEat);
        TextView itemFeed = (TextView) view.findViewById(R.id.listItemFeed);
        TextView itemComments = (TextView) view.findViewById(R.id.listItemComments);

        ActivityItem activityItem = (ActivityItem) getItem(position);

        itemDate.setText(activityItem.mDate);
        itemEat.setText(activityItem.mEat);

        String feedParsed = activityItem.mFeed.replaceAll(",", "\n");
        itemFeed.setText(feedParsed);

        if (activityItem.mWeight == 0)
            itemWeight.setText("");
        else
            itemWeight.setText(String.format("%d %03d", activityItem.mWeight/1000, (activityItem.mWeight - activityItem.mWeight/1000*1000)));

        if (activityItem.mComments.isEmpty())
            view.findViewById(R.id.listItemlinearLayoutComments).setVisibility(View.GONE);
        else
        {
            view.findViewById(R.id.listItemlinearLayoutComments).setVisibility(View.VISIBLE);
            itemComments.setText(activityItem.mComments);
        }

        return view;
    }
}