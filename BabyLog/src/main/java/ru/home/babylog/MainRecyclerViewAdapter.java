package ru.home.babylog;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>
{
    private final RecyclerViewClickListener mRecyclerViewClickListener;
    private List<ActivityItem> mList;

    public MainRecyclerViewAdapter(RecyclerViewClickListener recyclerViewClickListener)
    {
        mList = new ArrayList<ActivityItem>();
        mRecyclerViewClickListener = recyclerViewClickListener;
    }

    public void setDataModel(ArrayList<ActivityItem> dataModel)
    {
        mList = dataModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        ActivityItem item = mList.get(i);

        viewHolder.itemDate.setText(item.mDate);
        viewHolder.itemEat.setText(item.mEat);

        String feedParsed = item.mFeed.replaceAll(",", "\n");
        viewHolder.itemFeed.setText(feedParsed);

        if (item.mWeight == 0)
        {
            viewHolder.itemWeight.setText("");
        }
        else
        {
            viewHolder.itemWeight.setText(String.format("%d %03d", item.mWeight / 1000, (item.mWeight - item.mWeight / 1000 * 1000)));
        }

        if (item.mComments.isEmpty())
        {
            viewHolder.linearLayoutComments.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.linearLayoutComments.setVisibility(View.VISIBLE);
            viewHolder.itemComments.setText(item.mComments);
        }

    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final TextView itemDate;
        private final TextView itemWeight;
        private final TextView itemEat;
        private final TextView itemFeed;
        private final TextView itemComments;
        private final LinearLayout linearLayoutComments;

        public ViewHolder(View itemView)
        {
            super(itemView);
            itemDate = (TextView) itemView.findViewById(R.id.listItemDate);
            itemWeight = (TextView) itemView.findViewById(R.id.listItemWeight);
            itemEat = (TextView) itemView.findViewById(R.id.listItemEat);
            itemFeed = (TextView) itemView.findViewById(R.id.listItemFeed);
            itemComments = (TextView) itemView.findViewById(R.id.listItemComments);
            linearLayoutComments = (LinearLayout) itemView.findViewById(R.id.listItemlinearLayoutComments);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            mRecyclerViewClickListener.recyclerViewListClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view)
        {
            mRecyclerViewClickListener.recyclerViewListLongClicked(getAdapterPosition());
            return true;
        }
    }
}