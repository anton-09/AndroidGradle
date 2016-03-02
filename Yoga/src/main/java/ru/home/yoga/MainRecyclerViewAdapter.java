package ru.home.yoga;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.home.yoga.entity.YogaItem;


public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>
{
    private final RecyclerViewClickListener mRecyclerViewClickListener;
    private List<YogaItem> mList;
    private final Context mContext;

    public MainRecyclerViewAdapter(Context context, RecyclerViewClickListener recyclerViewClickListener)
    {
        mList = new ArrayList<YogaItem>();
        mContext = context;
        mRecyclerViewClickListener = recyclerViewClickListener;
    }

    public void setDataModel(ArrayList<YogaItem> dataModel)
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
        YogaItem item = mList.get(i);

        viewHolder.itemDate.setText(item.getDate());
        viewHolder.itemPrice.setText(String.format(mContext.getString(R.string.price), item.getPrice()));
        viewHolder.itemPeople.setText(item.getPeople().toString());
        viewHolder.itemType.setText(item.getType().getTypeName());
        if (item.getDuration().getDurationValue() == 1)
            viewHolder.itemDuration.setImageResource(R.drawable.duration60);
        else if (item.getDuration().getDurationValue() == 2)
            viewHolder.itemDuration.setImageResource(R.drawable.duration120);
        else viewHolder.itemDuration.setImageResource(R.drawable.duration90);
        viewHolder.itemStudio.setText(item.getStudio().getName());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final TextView itemDate;
        private final TextView itemPrice;
        private final TextView itemPeople;
        private final TextView itemType;
        private final ImageView itemDuration;
        private final TextView itemStudio;

        public ViewHolder(View itemView)
        {
            super(itemView);
            itemDate = (TextView) itemView.findViewById(R.id.text_view_date);
            itemPrice = (TextView) itemView.findViewById(R.id.text_view_price);
            itemPeople = (TextView) itemView.findViewById(R.id.text_view_people);
            itemType = (TextView) itemView.findViewById(R.id.text_view_type);
            itemDuration = (ImageView) itemView.findViewById(R.id.image_view_duration);
            itemStudio = (TextView) itemView.findViewById(R.id.text_view_studio);

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