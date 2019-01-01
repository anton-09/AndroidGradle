package ru.home.yoga;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class SimpleRecyclerViewDivider extends RecyclerView.ItemDecoration
{
    //private final Paint mPaint;
    private Drawable mDivider;

    public SimpleRecyclerViewDivider(Context context)
    {
        mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
//        mPaint = new Paint();
//        mPaint.setColor(context.getResources().getColor(R.color.colorAccent));
//        mPaint.setStrokeWidth(2);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

//            int top = child.getBottom() + params.bottomMargin + (int) mPaint.getStrokeWidth() / 2;
//            int bottom = top;
//
//            c.drawLine(left, top, right, bottom, mPaint);
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}