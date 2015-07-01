package ru.home.housing;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class AddFragment extends Fragment
{
    private View mContentView;
    private onAddEventListener addEventListener;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            addEventListener = (onAddEventListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement onAddEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mContentView = inflater.inflate(R.layout.add_fragment, null);

        int initialCold = 0, initialHot = 0, initialElectricity = 0;
        long initialDate = new Date().getTime();

        Cursor cursor = MyApplication.getDBAdapter().getLastData();
        cursor.moveToFirst();
        if (!cursor.isAfterLast())
        {
            initialDate = cursor.getLong(1);
            initialCold = cursor.getInt(2);
            initialHot = cursor.getInt(3);
            initialElectricity = cursor.getInt(4);
        }
        prepareIntWheel(R.id.wheelCold1, initialCold/100);
        prepareIntWheel(R.id.wheelCold2, (initialCold - initialCold/100*100)/10);
        prepareIntWheel(R.id.wheelCold3, initialCold - initialCold/10*10);
        prepareIntWheel(R.id.wheelHot1, initialHot/100);
        prepareIntWheel(R.id.wheelHot2, (initialHot - initialHot/100*100)/10);
        prepareIntWheel(R.id.wheelHot3, initialHot - initialHot/10*10);
        prepareIntWheel(R.id.wheelElectricity1, initialElectricity/10000);
        prepareIntWheel(R.id.wheelElectricity2, (initialElectricity - initialElectricity/10000*10000)/1000);
        prepareIntWheel(R.id.wheelElectricity3, (initialElectricity - initialElectricity/1000*1000)/100);
        prepareIntWheel(R.id.wheelElectricity4, (initialElectricity - initialElectricity/100*100)/10);
        prepareIntWheel(R.id.wheelElectricity5, initialElectricity - initialElectricity/10*10);
        prepareDateWheel(R.id.wheelDay, R.id.wheelMonth, R.id.wheelYear, initialDate);

        Button confirmData = (Button) mContentView.findViewById(R.id.buttonAdd);
        confirmData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                WheelView wheelViewYear = (WheelView) mContentView.findViewById(R.id.wheelYear);
                WheelView wheelViewMonth = (WheelView) mContentView.findViewById(R.id.wheelMonth);
                WheelView wheelViewDay = (WheelView) mContentView.findViewById(R.id.wheelDay);
                int cold, hot, electricity;

                cold = ((WheelView) mContentView.findViewById(R.id.wheelCold1)).getCurrentItem() * 100 +
                       ((WheelView) mContentView.findViewById(R.id.wheelCold2)).getCurrentItem() * 10 +
                       ((WheelView) mContentView.findViewById(R.id.wheelCold3)).getCurrentItem();

                hot = ((WheelView) mContentView.findViewById(R.id.wheelHot1)).getCurrentItem() * 100 +
                      ((WheelView) mContentView.findViewById(R.id.wheelHot2)).getCurrentItem() * 10 +
                      ((WheelView) mContentView.findViewById(R.id.wheelHot3)).getCurrentItem();

                electricity = ((WheelView) mContentView.findViewById(R.id.wheelElectricity1)).getCurrentItem() * 10000 +
                              ((WheelView) mContentView.findViewById(R.id.wheelElectricity2)).getCurrentItem() * 1000 +
                              ((WheelView) mContentView.findViewById(R.id.wheelElectricity3)).getCurrentItem() * 100 +
                              ((WheelView) mContentView.findViewById(R.id.wheelElectricity4)).getCurrentItem() * 10 +
                              ((WheelView) mContentView.findViewById(R.id.wheelElectricity5)).getCurrentItem();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 10 + wheelViewYear.getCurrentItem());
                calendar.set(Calendar.MONTH, wheelViewMonth.getCurrentItem());
                calendar.set(Calendar.DAY_OF_MONTH, wheelViewDay.getCurrentItem() + 1);

                MyApplication.getDBAdapter().addData(calendar.getTime().getTime(), cold, hot, electricity);

                addEventListener.NotifyAddEvent();
            }
        });

        return mContentView;
    }

    public void prepareIntWheel(int wheelId, int value)
    {
        WheelView wheelView = (WheelView) mContentView.findViewById(wheelId);
        wheelView.setViewAdapter(new NumericWheelAdapter(getActivity(), 0, 9));
        wheelView.setVisibleItems(2);
        wheelView.setCurrentItem(value);
        wheelView.setCyclic(true);
    }

    public void prepareDateWheel(int wheelIdDay, int wheelIdMonth, int wheelIdYear, long value)
    {
        final WheelView wheelViewDay = (WheelView) mContentView.findViewById(wheelIdDay);
        final WheelView wheelViewMonth = (WheelView) mContentView.findViewById(wheelIdMonth);
        final WheelView wheelViewYear = (WheelView) mContentView.findViewById(wheelIdYear);

        OnWheelChangedListener listener = new OnWheelChangedListener()
        {
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                updateDays(wheelViewYear, wheelViewMonth, wheelViewDay);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(value);
        // month
        int initialMonth = calendar.get(Calendar.MONTH);
        String months[] = getResources().getStringArray(R.array.months);
        wheelViewMonth.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), months));
        wheelViewMonth.setVisibleItems(2);
        wheelViewMonth.setCurrentItem(initialMonth);
        wheelViewMonth.addChangingListener(listener);

        // year
        int initialYear = calendar.get(Calendar.YEAR);
        wheelViewYear.setViewAdapter(new NumericWheelAdapter(getActivity(), curYear - 10, curYear + 10));
        wheelViewYear.setVisibleItems(2);
        wheelViewYear.setCurrentItem(initialYear + 10 - curYear);
        wheelViewYear.addChangingListener(listener);  
        
        //day
        updateDays(wheelViewYear, wheelViewMonth, wheelViewDay);
        wheelViewDay.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        wheelViewDay.setVisibleItems(2);
    }

    void updateDays(WheelView year, WheelView month, WheelView day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new NumericWheelAdapter(getActivity(), 1, maxDays));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

    public interface onAddEventListener
    {
        void NotifyAddEvent();
    }
}