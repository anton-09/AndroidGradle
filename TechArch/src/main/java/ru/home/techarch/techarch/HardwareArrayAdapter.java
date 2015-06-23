package ru.home.techarch.techarch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HardwareArrayAdapter extends ArrayAdapter<Hardware>
{
    private Context mContext;

    public HardwareArrayAdapter(Context context, int textViewResourceId, List<Hardware> objects)
    {
        super(context, textViewResourceId, objects);

        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        if (v == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.hardware_item, null);
        }


        Hardware hardware = getItem(position);

        if (hardware != null)
        {
            TextView hardwareNameTextView = (TextView) v.findViewById(R.id.hardwareName);
            TextView hardwareDateTextView = (TextView) v.findViewById(R.id.hardwareDate);

            hardwareNameTextView.setText(hardware.mName);
            hardwareDateTextView.setText(hardware.mDate);
        }

        return v;
    }
}
