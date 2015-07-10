package ru.home.fitness.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.home.fitness.MyApplication;
import ru.home.fitness.R;
import ru.home.fitness.RecyclerViewClickListener;
import ru.home.fitness.entities.Workout;

public class WorkoutRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutRecyclerViewAdapter.ViewHolder>
{
    private final RecyclerViewClickListener mRecyclerViewClickListener;
    private ArrayList<Workout> mList;

    public WorkoutRecyclerViewAdapter(RecyclerViewClickListener recyclerViewClickListener)
    {
        mList = new ArrayList<Workout>();
        mRecyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workout_recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        Workout item = mList.get(i);

        String startDateString = "";
        String endDateString = "";
        String showDatesString = "";

        if (item.getStartDate().getTime() != 0) startDateString = MyApplication.viewDateFormat.format(item.getStartDate());
        if (item.getEndDate().getTime() != 0) endDateString = MyApplication.viewDateFormat.format(item.getEndDate());
        if (!startDateString.isEmpty() || !endDateString.isEmpty())
            showDatesString = startDateString + " - " + endDateString;


        viewHolder.name.setText(item.getName());
        viewHolder.dates.setText(showDatesString);
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final TextView name;
        private final TextView dates;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.workout_name_text_view);
            dates = (TextView) itemView.findViewById(R.id.workout_dates_text_view);

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

    public void setDataModel(ArrayList<Workout> dataModel)
    {
        mList = dataModel;
    }
}
