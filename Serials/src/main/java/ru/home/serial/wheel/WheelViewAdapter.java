package ru.home.serial.wheel;

import android.view.View;
import android.view.ViewGroup;

public interface WheelViewAdapter
{
	/**
	 * Gets items count
	 * @return the count of wheel items
	 */
	int getItemsCount();
	
	/**
	 * Get a View that displays the data at the specified position in the data set
	 * 
	 * @param index the item index
	 * @param convertView the old view to reuse if possible
	 * @param parent the parent that this view will eventually be attached to
	 * @return the wheel item View
	 */
	View getItem(int index, View convertView, ViewGroup parent);

	/**
	 * Get a View that displays an empty wheel item placed before the first or after
	 * the last wheel item.
	 * 
	 * @param convertView the old view to reuse if possible
     * @param parent the parent that this view will eventually be attached to
	 * @return the empty item View
	 */
	View getEmptyItem(View convertView, ViewGroup parent);
}
