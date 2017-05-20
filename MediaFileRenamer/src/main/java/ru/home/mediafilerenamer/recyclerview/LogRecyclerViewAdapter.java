package ru.home.mediafilerenamer.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.home.mediafilerenamer.R;
import ru.home.mediafilerenamer.entities.MediaLog;

public class LogRecyclerViewAdapter extends RecyclerView.Adapter<LogRecyclerViewAdapter.LogViewHolder>
{
    private List<MediaLog> mList;

    public LogRecyclerViewAdapter()
    {
        mList = new ArrayList<>();
    }

    public void setDataModel(ArrayList<MediaLog> dataModel)
    {
        mList = dataModel;
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_log, parent, false);
        return new LogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position)
    {
        holder.textViewOld.setText(mList.get(position).getFileOldName());
        holder.textViewNew.setText(mList.get(position).getFileNewName());
        switch (mList.get(position).getStatus())
        {
            case MediaLog.STATUS_OK:
                holder.imageStatus.setImageResource(R.drawable.yes);
                break;
            case MediaLog.STATUS_EQUAL:
                holder.imageStatus.setImageResource(R.drawable.equal);
                break;
            case MediaLog.STATUS_CANCEL:
                holder.imageStatus.setImageResource(R.drawable.no);
                break;
        }
        switch(mList.get(position).getDir())
        {
            case MediaLog.DIR_FORWARD:
                holder.imageDir.setImageResource(R.drawable.forward);
                break;
            case MediaLog.DIR_BACKWARD:
                holder.imageDir.setImageResource(R.drawable.backward);
                break;
        }
    }

    class LogViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewOld;
        TextView textViewNew;
        ImageView imageStatus;
        ImageView imageDir;

        LogViewHolder(View itemView)
        {
            super(itemView);
            textViewOld = (TextView) itemView.findViewById(R.id.text_view_old);
            textViewNew = (TextView) itemView.findViewById(R.id.text_view_new);
            imageStatus = (ImageView) itemView.findViewById(R.id.image_status);
            imageDir = (ImageView) itemView.findViewById(R.id.image_dir);
        }
    }


}