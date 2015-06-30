package ru.home.babylog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SimpleRecyclerViewDivider extends RecyclerView.ItemDecoration
{
    private Paint mPaint;

    public SimpleRecyclerViewDivider(Context context)
    {
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.accent));
        mPaint.setStrokeWidth(3);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        outRect.set(0, 0, 0, (int) mPaint.getStrokeWidth());
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin + (int) mPaint.getStrokeWidth() / 2;
            int bottom = top;

            c.drawLine(left, top, right, bottom, mPaint);
        }
    }
}