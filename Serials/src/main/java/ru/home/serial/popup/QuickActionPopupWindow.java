package ru.home.serial.popup;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import ru.home.serial.MyApplication;
import ru.home.serial.R;

import java.util.ArrayList;
import java.util.List;

public class QuickActionPopupWindow
{
	private View mRootView;
	private ImageView mArrowUp;
	private ImageView mArrowDown;

	private ViewGroup mTrack;
	private FrameLayout mScroller;
	private OnActionItemClickListener mItemClickListener;

    private PopupWindow mWindow;
    private Drawable mBackground = null;
    private WindowManager mWindowManager;
    private LayoutInflater mLayoutInflater;

	private List<ActionItem> mActionItems = new ArrayList<ActionItem>();
	
	private int mChildPos;
    private int mInsertPos;
    private int rootWidth=0;
    
    public QuickActionPopupWindow()
    {
        mWindowManager = (WindowManager) MyApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mChildPos = 0;

        mWindow	= new PopupWindow(MyApplication.getAppContext());

        initView();
    }

	public void initView()
    {
		mRootView = mLayoutInflater.inflate(R.layout.popup_vertical, null);

		mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);
		mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
		mScroller = (FrameLayout) mRootView.findViewById(R.id.scroller);
		
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mWindow.setContentView(mRootView);

        mWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mWindow.dismiss();
                    return true;
                }

                return false;
            }
        });
	}
	
	public void setOnActionItemClickListener(OnActionItemClickListener listener)
    {
		mItemClickListener = listener;
	}

    public ActionItem getActionItem(int index)
    {
        return mActionItems.get(index);
    }

	public void addActionItem(ActionItem action)
    {
		mActionItems.add(action);
		
		String title = action.mTitle;
		Drawable icon = action.mIcon;
		
		View container = mLayoutInflater.inflate(R.layout.action_item_vertical, null);

		ImageView img = (ImageView) container.findViewById(R.id.iv_icon);
		TextView text = (TextView) container.findViewById(R.id.tv_title);
		
		if (icon != null)
			img.setImageDrawable(icon);
        else
			img.setVisibility(View.GONE);

		if (title != null)
			text.setText(title);
        else
			text.setVisibility(View.GONE);

		final int pos =  mChildPos;
		final int actionId = action.mActionId;
		
		container.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
            {
				if (mItemClickListener != null)
                    mItemClickListener.onItemClick(QuickActionPopupWindow.this, pos, actionId);

                if (!getActionItem(pos).mIsSticky)
                	mWindow.dismiss();
			}
		});
		
		container.setFocusable(true);
		container.setClickable(true);
			 
		mTrack.addView(container, mInsertPos);
		
		mChildPos++;
		mInsertPos++;
	}
	
	public void show (View anchor)
    {
        if (mRootView == null)
            throw new IllegalStateException("setContentView was not called with a view to display.");

        if (mBackground == null)
            mWindow.setBackgroundDrawable(new BitmapDrawable());
        else
            mWindow.setBackgroundDrawable(mBackground);

        mWindow.setWidth(LayoutParams.WRAP_CONTENT);
        mWindow.setHeight(LayoutParams.WRAP_CONTENT);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);

        mWindow.setContentView(mRootView);
		
		int xPos, yPos, arrowPos;
		
		int[] location = new int[2];
	
		anchor.getLocationOnScreen(location);

		Rect anchorRect	= new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
		int rootHeight = mRootView.getMeasuredHeight();
		
		if (rootWidth == 0)
			rootWidth = mRootView.getMeasuredWidth();

		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
		
		if ((anchorRect.left + rootWidth) > screenWidth)
        {
			xPos = anchorRect.left - (rootWidth - anchor.getWidth());
			xPos = (xPos < 0) ? 0 : xPos;
			
			arrowPos = anchorRect.centerX() - xPos;
		}
        else
        {
			if (anchor.getWidth() > rootWidth)
				xPos = anchorRect.centerX() - (rootWidth / 2);
            else
				xPos = anchorRect.left;

			arrowPos = anchorRect.centerX() -  xPos;
		}
		
		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = dyTop > dyBottom;

		if (onTop)
        {
			if (rootHeight > dyTop)
            {
				yPos = 15;
				LayoutParams l = mScroller.getLayoutParams();
				l.height = dyTop - anchor.getHeight();
			}
            else
				yPos = anchorRect.top - rootHeight;
		}
        else
        {
			yPos = anchorRect.bottom;
			
			if (rootHeight > dyBottom)
            {
				LayoutParams l = mScroller.getLayoutParams();
				l.height = dyBottom;
			}
		}
		
		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);
		
		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop)
    {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

        if (arrowPos <= screenWidth/4)
            mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
        else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4))
            mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
        else
            mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
	}
	
	private void showArrow(int whichArrow, int requestedX)
    {
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
       
        param.leftMargin = requestedX - arrowWidth / 2;
        
        hideArrow.setVisibility(View.INVISIBLE);
    }
	
	public interface OnActionItemClickListener
    {
		public abstract void onItemClick(QuickActionPopupWindow source, int pos, int actionId);
	}
}