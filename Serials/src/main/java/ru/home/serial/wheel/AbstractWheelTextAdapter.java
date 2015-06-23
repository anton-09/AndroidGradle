package ru.home.serial.wheel;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.home.serial.MyApplication;

public abstract class AbstractWheelTextAdapter implements WheelViewAdapter
{
    public static final int TEXT_VIEW_ITEM_RESOURCE = -1;
    protected static final int NO_RESOURCE = 0;
    public static final int DEFAULT_TEXT_COLOR = 0xFF101010;
    public static final int DEFAULT_TEXT_SIZE = 24;
    
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mTextSize = DEFAULT_TEXT_SIZE;
    
    protected LayoutInflater mInflater;
    
    protected int mItemResourceId;
    protected int mItemTextResourceId;
    protected int mEmptyItemResourceId;
	
    protected AbstractWheelTextAdapter()
    {
        this(TEXT_VIEW_ITEM_RESOURCE);
    }

    /**
     * Constructor
     * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
     */
    protected AbstractWheelTextAdapter(int itemResource)
    {
        this(itemResource, NO_RESOURCE);
    }
    
    /**
     * Constructor
     * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
     * @param itemTextResource the resource ID for a text view in the item layout
     */
    protected AbstractWheelTextAdapter(int itemResource, int itemTextResource)
    {
        mItemResourceId = itemResource;
        mItemTextResourceId = itemTextResource;
        
        mInflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    /**
     * Sets text size
     * @param textSize the text size to set
     */
    public void setTextSize(int textSize)
    {
        mTextSize = textSize;
    }
    
    /**
     * Returns text for specified item
     * @param index the item index
     * @return the text of specified items
     */
    protected abstract CharSequence getItemText(int index);

    @Override
    public View getItem(int index, View convertView, ViewGroup parent)
    {
        if (index >= 0 && index < getItemsCount())
        {
            if (convertView == null)
            {
                convertView = getView(mItemResourceId, parent);
            }
            TextView textView = getTextView(convertView, mItemTextResourceId);
            if (textView != null)
            {
                CharSequence text = getItemText(index);
                if (text == null)
                {
                    text = "";
                }
                textView.setText(text);
    
                if (mItemResourceId == TEXT_VIEW_ITEM_RESOURCE)
                {
                    configureTextView(textView);
                }
            }
            return convertView;
        }
    	return null;
    }

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = getView(mEmptyItemResourceId, parent);
        }

        if (mEmptyItemResourceId == TEXT_VIEW_ITEM_RESOURCE && convertView instanceof TextView)
        {
            configureTextView((TextView)convertView);
        }
            
        return convertView;
	}

    /**
     * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
     * @param view the text view to be configured
     */
    protected void configureTextView(TextView view)
    {
        view.setTextColor(mTextColor);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(mTextSize);
        view.setLines(1);
        view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
    }
    
    /**
     * Loads a text view from view
     * @param view the text view or layout containing it
     * @param textResource the text resource Id in layout
     * @return the loaded text view
     */
    private TextView getTextView(View view, int textResource)
    {
    	TextView text = null;
    	try
        {
            if (textResource == NO_RESOURCE && view instanceof TextView)
            {
                text = (TextView) view;
            }
            else if (textResource != NO_RESOURCE)
            {
                text = (TextView) view.findViewById(textResource);
            }
        }
        catch (ClassCastException e)
        {
            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException("AbstractWheelAdapter requires the resource ID to be a TextView", e);
        }
        
        return text;
    }
    
    /**
     * Loads view from resources
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     */
    private View getView(int resource, ViewGroup parent)
    {
        switch (resource)
        {
            case NO_RESOURCE:
                return null;
            case TEXT_VIEW_ITEM_RESOURCE:
                return new TextView(MyApplication.getAppContext());
            default:
                return mInflater.inflate(resource, parent, false);
        }
    }
}
