package ru.home.serial;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import ru.home.serial.wheel.ArrayWheelAdapter;
import ru.home.serial.wheel.NumericWheelAdapter;
import ru.home.serial.wheel.OnWheelChangedListener;
import ru.home.serial.wheel.WheelView;


public class WheelDatePicker extends Dialog
{

    private OnDateSetListener mOnDateSetListener;
    private WheelView mWheelViewDay;
    private WheelView mWheelViewMonth;
    private WheelView mWheelViewYear;
    private LocalDateTime mInitValue;
    

    public WheelDatePicker(Context localActivityContext, OnDateSetListener onDateSetListener, LocalDateTime initValue)
    {
        super(localActivityContext, android.R.style.Theme_InputMethod);
        setContentView(R.layout.picker_dialog);
        WindowManager.LayoutParams dialogParams = getWindow().getAttributes();
        dialogParams.gravity = Gravity.BOTTOM;
        dialogParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialogParams.alpha = 0.9f;
        dialogParams.dimAmount = 0.75f;
        getWindow().setAttributes(dialogParams);

        mOnDateSetListener = onDateSetListener;
        mInitValue = initValue;


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pickerDialogWheels);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        mWheelViewDay = new WheelView(localActivityContext);
        mWheelViewDay.setLayoutParams(layoutParams);

        mWheelViewMonth = new WheelView(localActivityContext);
        mWheelViewMonth.setLayoutParams(layoutParams);

        mWheelViewYear = new WheelView(localActivityContext);
        mWheelViewYear.setLayoutParams(layoutParams);

        OnWheelChangedListener listener = new OnWheelChangedListener() {
            @Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(mWheelViewYear, mWheelViewMonth, mWheelViewDay);
            }
        };

        String months[] = new String[] {
                MyApplication.getAppContext().getResources().getString(R.string.month01),
                MyApplication.getAppContext().getResources().getString(R.string.month02),
                MyApplication.getAppContext().getResources().getString(R.string.month03),
                MyApplication.getAppContext().getResources().getString(R.string.month04),
                MyApplication.getAppContext().getResources().getString(R.string.month05),
                MyApplication.getAppContext().getResources().getString(R.string.month06),
                MyApplication.getAppContext().getResources().getString(R.string.month07),
                MyApplication.getAppContext().getResources().getString(R.string.month08),
                MyApplication.getAppContext().getResources().getString(R.string.month09),
                MyApplication.getAppContext().getResources().getString(R.string.month10),
                MyApplication.getAppContext().getResources().getString(R.string.month11),
                MyApplication.getAppContext().getResources().getString(R.string.month12),
        };

        mWheelViewMonth.setViewAdapter(new DateArrayAdapter(months, mInitValue.getMonthOfYear() - 1));
        mWheelViewMonth.setCurrentItem(mInitValue.getMonthOfYear() - 1);
        mWheelViewMonth.addChangingListener(listener);

        mWheelViewYear.setViewAdapter(new DateNumericAdapter(DateTime.now().getYear() - MyApplication.mYearDepth, DateTime.now().getYear() + MyApplication.mYearDepth, mInitValue.getYear() - DateTime.now().getYear() + MyApplication.mYearDepth));
        mWheelViewYear.setCurrentItem(mInitValue.getYear() - DateTime.now().getYear() + MyApplication.mYearDepth);
        mWheelViewYear.addChangingListener(listener);

        updateDays(mWheelViewYear, mWheelViewMonth, mWheelViewDay);
        mWheelViewDay.setCurrentItem(mInitValue.getDayOfMonth() - 1);


        linearLayout.addView(mWheelViewDay);
        linearLayout.addView(mWheelViewMonth);
        linearLayout.addView(mWheelViewYear);


        Button okButton = (Button) findViewById(R.id.pickerDialogOkButton);
        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnDateSetListener != null)
                {
                    mInitValue = new LocalDateTime(DateTime.now().getYear() - MyApplication.mYearDepth + mWheelViewYear.getCurrentItem(), mWheelViewMonth.getCurrentItem() + 1, mWheelViewDay.getCurrentItem() + 1, 0, 0, 0, 0);
                    mOnDateSetListener.onDateSet(mInitValue);
                }
                dismiss();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.pickerDialogCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        Button todayButton = (Button) findViewById(R.id.pickerDialogTodayButton);
        todayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnDateSetListener != null)
                {
                    mInitValue = new LocalDateTime();
                    mOnDateSetListener.onDateSet(mInitValue);
                }
                dismiss();
            }
        });

        Button resetButton = (Button) findViewById(R.id.pickerDialogResetButton);
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnDateSetListener != null)
                {
                    mOnDateSetListener.onDateSet(null);
                }
                dismiss();
            }
        });

    }

    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays(WheelView year, WheelView month, WheelView day)
    {
        int maxDays = new LocalDate(DateTime.now().getYear() - MyApplication.mYearDepth + year.getCurrentItem(), month.getCurrentItem() + 1, 1).dayOfMonth().withMaximumValue().getDayOfMonth();

        day.setViewAdapter(new DateNumericAdapter(1, maxDays, mInitValue.getDayOfMonth() - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

    public interface OnDateSetListener
    {
        void onDateSet(LocalDateTime selectedDate);
    }

    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter
    {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateNumericAdapter(int minValue, int maxValue, int current)
        {
            super(minValue, maxValue);
            currentValue = current;
            setTextSize(16);
        }

        @Override
        protected void configureTextView(TextView view)
        {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                //view.setTextColor(Color.BLUE);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent)
        {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String>
    {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateArrayAdapter(String[] items, int current)
        {
            super(items);
            currentValue = current;
            setTextSize(16);
        }

        @Override
        protected void configureTextView(TextView view)
        {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                //view.setTextColor(Color.BLUE);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent)
        {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
}
