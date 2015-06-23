package ru.home.serial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ExtendedStateRelativeLayout extends RelativeLayout
{
    private static final int[] STATE_RELEASED = {R.attr.state_released};
    private static final int[] STATE_DOWNLOADED = {R.attr.state_downloaded};
    private static final int[] STATE_WATCHED = {R.attr.state_watched};

    private boolean mIsReleased = false;
    private boolean mIsDownloaded = false;
    private boolean mIsWatched = false;

    public ExtendedStateRelativeLayout(Context context)
    {
        this(context, null);
    }

    public ExtendedStateRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setReleased(boolean isReleased)
    {
        if (mIsReleased != isReleased)
        {
            mIsReleased = isReleased;
            refreshDrawableState();
        }
    }
    public void setDownloaded(boolean isDownloaded)
    {
        if (mIsDownloaded != isDownloaded)
        {
            mIsDownloaded = isDownloaded;
            refreshDrawableState();
        }
    }
    public void setWatched(boolean isWatched)
    {
        if (mIsWatched != isWatched)
        {
            mIsWatched = isWatched;
            refreshDrawableState();
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
        if (mIsReleased) {
            mergeDrawableStates(drawableState, STATE_RELEASED);
        }
        if (mIsDownloaded) {
            mergeDrawableStates(drawableState, STATE_DOWNLOADED);
        }
        if (mIsWatched) {
            mergeDrawableStates(drawableState, STATE_WATCHED);
        }
        return drawableState;
    }

}
