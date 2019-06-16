package ru.home.mediafilerenamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import ru.home.mediafilerenamer.entities.MediaDir;
import ru.home.mediafilerenamer.helper.FileCountHelper;
import ru.home.mediafilerenamer.helper.OneFileCallback;
import ru.home.mediafilerenamer.recyclerview.RecyclerViewClickListener;
import ru.home.mediafilerenamer.recyclerview.SimpleRecyclerViewAdapter;
import ru.home.mediafilerenamer.recyclerview.SimpleRecyclerViewDivider;

public class ChooseDirActivity extends AppCompatActivity implements RecyclerViewClickListener, OneFileCallback
{
    RecyclerView recyclerView;
    SimpleRecyclerViewAdapter simpleRecyclerViewAdapter;
    TextView textPath;
    MediaDir currentItem = null;

    ArrayList<MediaDir> arrayDir = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_dir);

        textPath = (TextView) findViewById(R.id.current_path);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleRecyclerViewDivider(this));

        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter(this);
        simpleRecyclerViewAdapter.setDataModel(arrayDir);
        simpleRecyclerViewAdapter.setCurrentItem(new MediaDir(null, "", "", ""));
        recyclerView.setAdapter(simpleRecyclerViewAdapter);

        updateListDir(new File(MyApplication.getInitialFolder()));
    }

    public void onClickConfirm(View view)
    {
        MyApplication.setInitialFolder(currentItem.getUrl().getAbsolutePath());
        MyApplication.getSharedPreferences().edit()
                .putString("mInitialFolder", currentItem.getUrl().getAbsolutePath())
                .apply();


        Intent intent = new Intent();
        intent.putExtra("url", currentItem);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void updateListDir(File file)
    {
        arrayDir.clear();

        textPath.setText(file.getAbsolutePath());

        FileCountHelper.processFile(file, true, this, null, true);
    }


    @Override
    public void recyclerViewListClicked(int position)
    {
        if (position >= 0)
            updateListDir(arrayDir.get(position).getUrl());
        else
            updateListDir((currentItem.getUrl().getParentFile() != null && !currentItem.getUrl().equals(Environment.getExternalStorageDirectory())) ? currentItem.getUrl().getParentFile() : currentItem.getUrl());
    }

    @Override
    public void handleProgress(MediaDir result)
    {
        arrayDir.add(result);
        simpleRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void handleFinal(MediaDir result)
    {
        currentItem = result;
        simpleRecyclerViewAdapter.setCurrentItem(currentItem);
        simpleRecyclerViewAdapter.notifyDataSetChanged();
    }
}