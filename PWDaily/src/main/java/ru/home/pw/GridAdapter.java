package ru.home.pw;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener
{
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private DataModel mDataModel;

    public GridAdapter(Context context, DataModel dataModel)
    {
        mDataModel = dataModel;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            view = mLayoutInflater.inflate(R.layout.grid_cell, parent, false);
        }

        Object gridElement = getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.cell_item);

        if (gridElement == null)
        {
            textView.setText("");
            textView.setBackgroundResource(R.drawable.black);
        }
        else if (gridElement instanceof DataModel.Char)
        {
            textView.setOnClickListener(this);

            int resID = mContext.getResources().getIdentifier(((DataModel.Char)gridElement).mPicture, "drawable", mContext.getPackageName());
            textView.setText("");
            textView.setBackgroundResource(resID);
        }
        else if (gridElement instanceof DataModel.Event)
        {
            textView.setText(((DataModel.Event)gridElement).mName);
            textView.setBackgroundResource(R.drawable.black);
        }
        else if (gridElement instanceof DataModel.Storage)
        {
            textView.setOnClickListener(this);
            textView.setOnLongClickListener(this);

            textView.setText("");
            textView.setBackground(mContext.getResources().getDrawable(DataModel.status2Drawable(((DataModel.Storage) gridElement).mStatus)));
        }

        textView.setTag(position);

        return view;
    }

    @Override
    public boolean onLongClick(final View view)
    {
        Log.d("PWDaily", "GridAdapter::onLongClick");
        final int index = Integer.parseInt(view.getTag().toString());
        final DataModel.Storage storage = (DataModel.Storage) getItem(index);

        if (storage.ResetStatus())
            view.setBackground(mContext.getResources().getDrawable(R.drawable.red));

        else if (storage.mCharEvent.mUsed)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setMessage(R.string.off)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
						public void onClick(DialogInterface dialog, int whichButton)
                        {
                            storage.OffStatus();
                            view.setBackground(mContext.getResources().getDrawable(R.drawable.gray));
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                    {
                        @Override
						public void onClick(DialogInterface dialog, int whichButton)
                        {
                        }
                    })
                    .show();
        }

        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setMessage(R.string.on)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
						public void onClick(DialogInterface dialog, int whichButton) {
                            storage.OnStatus();
                            view.setBackground(mContext.getResources().getDrawable(DataModel.status2Drawable(((DataModel.Storage)getItem(index)).mStatus)));
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                    {
                        @Override
						public void onClick(DialogInterface dialog, int whichButton)
                        {

                        }
                    })
                    .show();

        }
        return true;
    }

    @Override
    public void onClick(final View view)
    {
        Log.d("PWDaily", "GridAdapter::onClick");
        int index = Integer.parseInt(view.getTag().toString());
        final Object gridElement = getItem(index);

        if (gridElement instanceof DataModel.Char)
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append(((DataModel.Char) gridElement).mName)
                    .append(" ")
                    .append(((DataModel.Char) gridElement).mLevel+1)
                    .append(" ")
                    .append(mContext.getResources().getString(R.string.levelup));

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setMessage(stringBuilder)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
						public void onClick(DialogInterface dialog, int whichButton) {
                            ((DataModel.Char) gridElement).LevelUp();
                            mDataModel.fillCharsFromCursor();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                    {
                        @Override
						public void onClick(DialogInterface dialog, int whichButton)
                        {
                        }
                    })
                    .show();
        }
        else if (gridElement instanceof DataModel.Storage && ((DataModel.Storage)gridElement).mCharEvent.mUsed)
        {
            int newStatus = ((DataModel.Storage)gridElement).UpdateStatus();
            if (newStatus > 0)
                view.setBackground(mContext.getResources().getDrawable(DataModel.status2Drawable(newStatus)));
        }
    }

    @Override
	public final int getCount()
    {
        return (mDataModel.mChars.size() + 1) * (mDataModel.mEvents.size() + 1);
    }

    @Override
	public final Object getItem(int position)
    {
        return mDataModel.getItem(position);
    }

    @Override
	public final long getItemId(int position)
    {
        return position;
    }

}
