package ru.home.mediafilerenamer.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.home.mediafilerenamer.R;
import ru.home.mediafilerenamer.entities.MediaDir;

public class SimpleRecyclerViewAdapter extends RecyclerView.Adapter
{
    private static final int ITEM_UP = 0;
    private static final int ITEM_DIR = 1;
    private static final int ITEM_FILE = 2;

    private final RecyclerViewClickListener mRecyclerViewClickListener;
    private List<MediaDir> mList;
    private MediaDir mCurrentItem;

    public SimpleRecyclerViewAdapter(RecyclerViewClickListener recyclerViewClickListener)
    {
        mList = new ArrayList<>();
        mRecyclerViewClickListener = recyclerViewClickListener;
    }

    public void setDataModel(ArrayList<MediaDir> dataModel)
    {
        mList = dataModel;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == 0)
            return ITEM_UP;
        else
            return mList.get(position - 1).getUrl().isDirectory() ? ITEM_DIR : ITEM_FILE;
    }

    @Override
    public int getItemCount()
    {
        return mList.size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder;

        if (viewType == ITEM_DIR)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_dir, parent, false);
            viewHolder = new DirViewHolder(v);
        }
        else if (viewType == ITEM_FILE)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_file, parent, false);
            viewHolder = new FileViewHolder(v);
        }
        else //if (viewType  == ITEM_UP)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_dir, parent, false);
            viewHolder = new UpViewHolder(v);
        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof DirViewHolder)
        {
            ((DirViewHolder)holder).textView.setText(mList.get(position - 1).getUrl().getName());
            ((DirViewHolder)holder).textViewPhotoJpgCount.setText(mList.get(position - 1).getPhotoJpgCount());
            ((DirViewHolder)holder).textViewVideoMp4Count.setText(mList.get(position - 1).getVideoMp4Count());
            ((DirViewHolder)holder).textViewPhotoJpegCount.setText(mList.get(position - 1).getPhotoJpegCount());
        }
        else if (holder instanceof FileViewHolder)
        {
            ((FileViewHolder)holder).textView.setText(mList.get(position - 1).getUrl().getName());
        }
        else if (holder instanceof UpViewHolder)
        {
            ((UpViewHolder)holder).textView.setText("...");
            ((UpViewHolder)holder).textViewPhotoJpgCount.setText(mCurrentItem.getPhotoJpgCount());
            ((UpViewHolder)holder).textViewVideoMp4Count.setText(mCurrentItem.getVideoMp4Count());
            ((UpViewHolder)holder).textViewPhotoJpegCount.setText(mCurrentItem.getPhotoJpegCount());
        }
    }

    public void setCurrentItem(MediaDir currentItem)
    {
        mCurrentItem = currentItem;
    }

    private class DirViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textView;
        TextView textViewPhotoJpgCount;
        TextView textViewVideoMp4Count;
        TextView textViewPhotoJpegCount;

        DirViewHolder(View itemView)
        {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
            textViewPhotoJpgCount = (TextView) itemView.findViewById(R.id.text_view_photo_jpg_count);
            textViewVideoMp4Count = (TextView) itemView.findViewById(R.id.text_view_video_mp4_count);
            textViewPhotoJpegCount = (TextView) itemView.findViewById(R.id.text_view_photo_jpeg_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            mRecyclerViewClickListener.recyclerViewListClicked(getAdapterPosition() - 1);
        }

    }

    private class FileViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;

        FileViewHolder(View itemView)
        {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
        }
    }

    private class UpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textView;
        TextView textViewPhotoJpgCount;
        TextView textViewVideoMp4Count;
        TextView textViewPhotoJpegCount;

        UpViewHolder(View itemView)
        {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
            textViewPhotoJpgCount = (TextView) itemView.findViewById(R.id.text_view_photo_jpg_count);
            textViewVideoMp4Count = (TextView) itemView.findViewById(R.id.text_view_video_mp4_count);
            textViewPhotoJpegCount = (TextView) itemView.findViewById(R.id.text_view_photo_jpeg_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            mRecyclerViewClickListener.recyclerViewListClicked(getAdapterPosition() - 1);
        }
    }
}