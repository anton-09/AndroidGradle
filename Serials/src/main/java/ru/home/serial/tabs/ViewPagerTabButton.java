package ru.home.serial.tabs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;
import ru.home.serial.MyApplication;
import ru.home.serial.R;

public class ViewPagerTabButton extends Button 
{
	private static final int mLineHeight = 2;
	private static final int mLineHeightSelected = 6;

	public ViewPagerTabButton(Context context)
    {
		this(context, null);
	}

	public ViewPagerTabButton(Context context, AttributeSet attrs)
    {
		this(context, attrs, 0);
	}

	public ViewPagerTabButton(Context context, AttributeSet attrs, int defStyle)
    {
		super(context, attrs, defStyle);
	}


	private Paint mLinePaint = new Paint();

	@Override
	protected synchronized void onDraw(Canvas canvas)
    {
		super.onDraw(canvas);

        mLinePaint.setColor(getResources().getColor(R.color.view_pager_tab_button_line));

		final int height = isSelected() ? mLineHeightSelected : mLineHeight;

		// draw the line
		canvas.drawRect(0, getMeasuredHeight() - height, getMeasuredWidth(), getMeasuredHeight(), mLinePaint);

	}

}