package ru.home.fitness.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ru.home.fitness.R;
import ru.home.fitness.RecyclerViewClickListener;
import ru.home.fitness.entities.Action;

public class MainRecyclerViewAdapter extends SelectableAdapter<MainRecyclerViewAdapter.ViewHolder>
{
    private final RecyclerViewClickListener mRecyclerViewClickListener;
    private ArrayList<Action> mList;

    public MainRecyclerViewAdapter(RecyclerViewClickListener recyclerViewClickListener)
    {
        mList = new ArrayList<Action>();
        mRecyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        Action item = mList.get(i);

        viewHolder.name.setText(item.getExercise().getName());
        viewHolder.muscleName.setText(item.getExercise().getMuscle().getName());
        viewHolder.workoutName.setText(item.getWorkoutName());
        viewHolder.comment.setText(item.getComment());
        viewHolder.selectedOverlayView.setVisibility(isSelected(i) ? View.VISIBLE : View.INVISIBLE);
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
        private final TextView workoutName;
        private final EditText comment;
        private final View selectedOverlayView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.exercise_name_text_view);
            muscleName = (TextView) itemView.findViewById(R.id.muscle_name_text_view);
            workoutName = (TextView) itemView.findViewById(R.id.workout_name_text_view);
            comment = (EditText) itemView.findViewById(R.id.comment_edit_text);
            selectedOverlayView = itemView.findViewById(R.id.selected_overlay);

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

    public void setDataModel(ArrayList<Action> dataModel)
    {
        mList = dataModel;
    }
}

