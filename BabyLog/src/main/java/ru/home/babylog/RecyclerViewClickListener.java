package ru.home.babylog;

import android.view.View;

public interface RecyclerViewClickListener
{
    public void recyclerViewListClicked(View v, int position);
    public void recyclerViewListLongClicked(View v, int position);
}