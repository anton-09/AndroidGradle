package ru.home.yoga;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.home.yoga.entity.YogaItem;


public class MainRecyclerViewAdapter extends RecyclerView.Adapter
{
    private static final int VIEW_PROG = 0;
    private static final int VIEW_ITEM = 1;

    private final RecyclerViewClickListener mRecyclerViewClickListener;
    private List<YogaItem> mList;
    private final Context mContext;

    public MainRecyclerViewAdapter(Context context, RecyclerViewClickListener recyclerViewClickListener)
    {
        mList = new ArrayList<>();
        mContext = context;
        mRecyclerViewClickListener = recyclerViewClickListener;
    }

    public void setDataModel(ArrayList<YogaItem> dataModel)
    {
        mList = dataModel;
    }


    @Override
    public int getItemViewType(int position)
    {
        return mList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
            vh = new YogaItemViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progess_bar, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if (viewHolder instanceof YogaItemViewHolder)
        {
            YogaItem item = mList.get(position);

            ((YogaItemViewHolder) viewHolder).itemDate.setText(item.getDate());
            ((YogaItemViewHolder) viewHolder).itemPrice.setText(String.format(mContext.getString(R.string.price), item.getPrice()));
            ((YogaItemViewHolder) viewHolder).itemPeople.setText(item.getPeople().toString());
            ((YogaItemViewHolder) viewHolder).itemType.setText(item.getType().getTypeName());
            ((YogaItemViewHolder) viewHolder).itemFIO.setText(item.getFIO());

            if (item.getFIO().isEmpty())
            {
                ((YogaItemViewHolder) viewHolder).itemFIO.setVisibility(View.GONE);
            }
            else
            {
                ((YogaItemViewHolder) viewHolder).itemFIO.setVisibility(View.VISIBLE);
            }

            switch (item.getPayType())
            {
                case "+":
                    ((YogaItemViewHolder) viewHolder).frameLayout.setBackgroundResource(R.color.plus);
                    break;
                case "-":
                    ((YogaItemViewHolder) viewHolder).frameLayout.setBackgroundResource(R.color.minus);
                    break;
                default:
                    ((YogaItemViewHolder) viewHolder).frameLayout.setBackgroundResource(R.color.background);
                    break;
            }
        }
        else
        {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }

    class YogaItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final FrameLayout frameLayout;
        private final TextView itemDate;
        private final TextView itemPeople;
        private final TextView itemPrice;
        private final TextView itemType;
        private final TextView itemFIO;


        public YogaItemViewHolder(View itemView)
        {
            super(itemView);

            frameLayout = (FrameLayout) itemView.findViewById(R.id.frame);
            itemDate = (TextView) itemView.findViewById(R.id.text_view_date);
            itemPrice = (TextView) itemView.findViewById(R.id.text_view_price);
            itemPeople = (TextView) itemView.findViewById(R.id.text_view_people);
            itemType = (TextView) itemView.findViewById(R.id.text_view_type);
            itemFIO = (TextView) itemView.findViewById(R.id.text_view_fio);

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

    class ProgressViewHolder extends RecyclerView.ViewHolder
    {
        private final ProgressBar progressBar;

        public ProgressViewHolder(View itemView)
        {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}