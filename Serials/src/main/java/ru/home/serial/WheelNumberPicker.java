package ru.home.serial;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import ru.home.serial.wheel.NumericWheelAdapter;
import ru.home.serial.wheel.WheelView;

public class WheelNumberPicker extends Dialog
{
    private OnNumberSetListener mOnNumberSetListener;
    private WheelView mWheelViewSingle;
    int mInitValue;

    public WheelNumberPicker(Context localActivityContext, OnNumberSetListener onNumberSetListener, int initValue)
    {
        super(localActivityContext, android.R.style.Theme_InputMethod);
        setContentView(R.layout.picker_dialog);
        WindowManager.LayoutParams dialogParams = getWindow().getAttributes();
        dialogParams.gravity = Gravity.BOTTOM;
        dialogParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialogParams.alpha = 0.9f;
        dialogParams.dimAmount = 0.75f;
        getWindow().setAttributes(dialogParams);

        mOnNumberSetListener = onNumberSetListener;
        mInitValue = initValue;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pickerDialogWheels);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        mWheelViewSingle = new WheelView(localActivityContext);
        mWheelViewSingle.setViewAdapter(new NumericWheelAdapter(0, 999));
        mWheelViewSingle.setCurrentItem(mInitValue);
        mWheelViewSingle.setCyclic(true);
        mWheelViewSingle.setInterpolator(new AnticipateOvershootInterpolator());
        mWheelViewSingle.setLayoutParams(layoutParams);

        linearLayout.addView(mWheelViewSingle);

        Button okButton = (Button) findViewById(R.id.pickerDialogOkButton);
        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnNumberSetListener != null)
                {
                    mInitValue = mWheelViewSingle.getCurrentItem();
                    mOnNumberSetListener.onNumberSet(mInitValue);
                }
                dismiss();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.pickerDialogCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        Button todayButton = (Button) findViewById(R.id.pickerDialogTodayButton);
        todayButton.setVisibility(View.GONE);

        Button resetButton = (Button) findViewById(R.id.pickerDialogResetButton);
        resetButton.setVisibility(View.GONE);
    }

    public interface OnNumberSetListener
    {
        public void onNumberSet(int selectedNumber);
    }
}
