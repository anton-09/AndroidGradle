package ru.home.serial.wheel;

public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter
{
    private T mItems[];

    public ArrayWheelAdapter(T items[])
    {
        super();
        
        mItems = items;
    }
    
    @Override
    public CharSequence getItemText(int index)
    {
        if (index >= 0 && index < mItems.length)
        {
            T item = mItems[index];
            if (item instanceof CharSequence)
            {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    @Override
    public int getItemsCount()
    {
        return mItems.length;
    }
}
