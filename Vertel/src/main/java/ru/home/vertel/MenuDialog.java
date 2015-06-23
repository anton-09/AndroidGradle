package ru.home.vertel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuDialog extends Dialog
{
    private static final int MENU_ITEMS_IN_ROW = 3;

    public MenuDialog(final Activity context, final ArrayList<ImagePack> imagePackArray)
    {
        super(context, android.R.style.Theme_InputMethod);

        // Root vertical layout
        LinearLayout rootVerticalLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rootVerticalLayout.setLayoutParams(layoutParams);
        rootVerticalLayout.setOrientation(LinearLayout.VERTICAL);
        rootVerticalLayout.setBackgroundResource(R.drawable.toast_border);

        LinearLayout rowHorizontalLayout = null;
        for (int i=0; i<imagePackArray.size(); i++)
        {

            if (i%MENU_ITEMS_IN_ROW == 0)
            {
                // Row horizontal layout
                rowHorizontalLayout = new LinearLayout(context);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                rowHorizontalLayout.setLayoutParams(layoutParams2);

                rootVerticalLayout.addView(rowHorizontalLayout);
            }

            // !!! Very important to fill ViewGroup here, so the child will inherit parent's layout
            View menuItemView = getLayoutInflater().inflate(R.layout.menu_item, rowHorizontalLayout, false);
            ImageView menuItemImage = (ImageView) menuItemView.findViewById(R.id.menu_item_image);
            TextView menuItemText = (TextView) menuItemView.findViewById(R.id.menu_item_text);

            menuItemImage.setImageDrawable(imagePackArray.get(i).mCover);
            menuItemText.setText(imagePackArray.get(i).mName);

            menuItemView.setTag(i);
            menuItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SelectPackActivity.class);
                    intent.putExtra("ImagePack", imagePackArray.get(Integer.parseInt(view.getTag().toString())));
                    context.startActivityForResult(intent, Integer.parseInt(view.getTag().toString()));
                    dismiss();
                }
            });
            rowHorizontalLayout.addView(menuItemView);
        }
        
        for (int i=0; i<MENU_ITEMS_IN_ROW-imagePackArray.size()%MENU_ITEMS_IN_ROW; i++)
        {
            View menuItemView = getLayoutInflater().inflate(R.layout.menu_item, rowHorizontalLayout, false);
            rowHorizontalLayout.addView(menuItemView);
        }

        setContentView(rootVerticalLayout);

        WindowManager.LayoutParams dialogParams = getWindow().getAttributes();
        dialogParams.gravity = Gravity.BOTTOM;
        dialogParams.width = LayoutParams.MATCH_PARENT;
        dialogParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialogParams.alpha = 0.9f;
        dialogParams.dimAmount = 0.75f;
        getWindow().setAttributes(dialogParams);
        getWindow().getAttributes().windowAnimations = R.style.MenuDialogAnimation;
    }
}
