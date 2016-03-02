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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class BackupRecyclerViewAdapter extends RecyclerView.Adapter<BackupRecyclerViewAdapter.ViewHolder>
{
    private final SimpleDateFormat mBackupDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private final SimpleDateFormat mBackupHumanReadableDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.backup_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        File item = mList.get(i);

        try
        {
            viewHolder.name.setText(mBackupHumanReadableDateFormat.format(mBackupDateFormat.parse(item.getName())));
        }
        catch (ParseException e)
        {
            viewHolder.name.setText(item.getName());
        }

        int rowCount = 0;
        try
        {
            FileReader fileReader = new FileReader(item);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.readLine() != null)
            {
                rowCount++;
            }
            bufferedReader.close();
        }
        catch (Exception ignored)
        {
        }

        //viewHolder.size.setText(item.length() + " " + mContext.getString(R.string.bytes));
        //viewHolder.rowCount.setText(rowCount + " " + mContext.getString(R.string.row_count));
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final TextView name;
        private final TextView size;
        private final TextView rowCount;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_view_backup_date);
            size = (TextView) itemView.findViewById(R.id.text_view_backup_size);
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
