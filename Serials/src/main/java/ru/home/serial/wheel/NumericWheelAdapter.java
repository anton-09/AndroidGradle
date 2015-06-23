package ru.home.serial.wheel;

public class NumericWheelAdapter extends AbstractWheelTextAdapter
{
    private int mMinValue;
    private int mMaxValue;
    private String mFormat;
    
    public NumericWheelAdapter(int minValue, int maxValue)
    {
        this(minValue, maxValue, null);
    }

    public NumericWheelAdapter(int minValue, int maxValue, String format) {
        super();
        
        mMinValue = minValue;
        mMaxValue = maxValue;
        mFormat = format;
    }

    @Override
    public CharSequence getItemText(int index)
    {
        if (index >= 0 && index < getItemsCount())
        {
            int value = mMinValue + index;
            return mFormat != null ? String.format(mFormat, value) : Integer.toString(value);
        }
        return null;
    }

    @Override
    public int getItemsCount()
    {
        return mMaxValue - mMinValue + 1;
    }    
}
