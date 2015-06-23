package ru.home.serial.wheel;

public class ItemsRange
{
	private int mFirst;
	private int mCount;

    public ItemsRange()
    {
        this(0, 0);
    }
    
	public ItemsRange(int first, int count)
    {
		mFirst = first;
		mCount = count;
	}
	
	public int getFirst()
    {
		return mFirst;
	}
	
	public int getLast()
    {
		return getFirst() + getCount() - 1;
	}
	
	public int getCount()
    {
		return mCount;
	}
	
	public boolean contains(int index)
    {
		return index >= getFirst() && index <= getLast();
	}
}