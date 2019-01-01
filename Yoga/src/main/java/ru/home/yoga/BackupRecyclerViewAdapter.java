package ru.home.yoga;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;


public class BackupRecyclerViewAdapter extends RecyclerView.Adapter<BackupRecyclerViewAdapter.ViewHolder>
{
    private final Context mContext;
    private final List<File> mList;
    private final RecyclerViewClickListener mRecyclerViewClickListener;

    public BackupRecyclerViewAdapter(List<File> list, Context context, RecyclerViewClickListener recyclerViewClickListener)
    {
        mList = list;
        mContext = context;
        mRecyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.backup_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        File item = mList.get(position);

        viewHolder.name.setText(MyApplication.mBackupDateFormat.parseLocalDateTime(item.getName()).toString(MyApplication.mHumanReadableDateFormat));

        int rowCount = 0;
        try
        {
            FileReader fileReader = new FileReader(item);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            if (line != null)
            {
                rowCount = Integer.parseInt(line);
            }
            bufferedReader.close();
        } catch (Exception ignored)
        {
        }

        viewHolder.rowCount.setText(String.format(mContext.getString(R.string.row_count), rowCount));
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final TextView name;
        private final TextView rowCount;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_view_backup_date);
            rowCount = (TextView) itemView.findViewById(R.id.text_view_backup_row_count);

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
