package ru.home.babylog;

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
    SimpleDateFormat mBackupDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat mBackupHumanReadableDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    Context mContext;
    private List<File> mList;
    private RecyclerViewClickListener mRecyclerViewClickListener;

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
        catch (Exception e)
        {
        }

        viewHolder.size.setText(item.length() + " " + mContext.getString(R.string.bytes));
        viewHolder.rowCount.setText(rowCount + " " + mContext.getString(R.string.row_count));
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private TextView name;
        private TextView size;
        private TextView rowCount;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.listItemBackupDate);
            size = (TextView) itemView.findViewById(R.id.listItemBackupSize);
            rowCount = (TextView) itemView.findViewById(R.id.listItemBackupRowCount);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            mRecyclerViewClickListener.recyclerViewListClicked(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view)
        {
            mRecyclerViewClickListener.recyclerViewListLongClicked(view, getAdapterPosition());
            return true;
        }
    }
}
