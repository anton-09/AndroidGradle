package ru.home.yoga;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.home.yoga.entity.SummaryItem;

public class SummaryRecyclerViewAdapter extends RecyclerView.Adapter
{
    Context mContext;
    ArrayList<SummaryItem> mData;

    public SummaryRecyclerViewAdapter(Context context, ArrayList<SummaryItem> data)
    {
        mContext = context;
        mData = data;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.summary_list_item, viewGroup, false);
        return new SummaryItemViewHolder(v);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        SummaryItem summaryItem = (SummaryItem) mData.get(position);

        ((SummaryItemViewHolder) viewHolder).studioName.setText(summaryItem.getStudioName());
        ((SummaryItemViewHolder) viewHolder).count.setText(summaryItem.getCount().toString());
        ((SummaryItemViewHolder) viewHolder).price.setText(String.format(mContext.getString(R.string.price), summaryItem.getPrice()));
    }

    public int getItemCount()
    {
        return mData.size();
    }

    class SummaryItemViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView count;
        private final TextView price;
        private final TextView studioName;

        public SummaryItemViewHolder(View itemView)
        {
            super(itemView);
            studioName = (TextView) itemView.findViewById(R.id.text_view_studio);
            count = (TextView) itemView.findViewById(R.id.text_view_count);
            price = (TextView) itemView.findViewById(R.id.text_view_price);
        }
    }
}
