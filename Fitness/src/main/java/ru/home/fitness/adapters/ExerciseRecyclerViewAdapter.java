package ru.home.fitness.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.home.fitness.R;
import ru.home.fitness.RecyclerViewClickListener;
import ru.home.fitness.entities.Exercise;

public class ExerciseRecyclerViewAdapter extends RecyclerView.Adapter<ExerciseRecyclerViewAdapter.ViewHolder>
{
    private final RecyclerViewClickListener mRecyclerViewClickListener;
    private ArrayList<Exercise> mList;

    public ExerciseRecyclerViewAdapter(RecyclerViewClickListener recyclerViewClickListener)
    {
        mList = new ArrayList<Exercise>();
        mRecyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercise_recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        Exercise item = mList.get(i);

        viewHolder.name.setText(item.getName());
        viewHolder.muscleName.setText(item.getMuscle().getName());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final TextView name;
        private final TextView muscleName;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.exercise_name_text_view);
            muscleName = (TextView) itemView.findViewById(R.id.muscle_name_text_view);

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

    public void setDataModel(ArrayList<Exercise> dataModel)
    {
        mList = dataModel;
    }
}
