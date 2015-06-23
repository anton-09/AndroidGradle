package ru.home.vertel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.home.vertel.wheel.OnWheelScrollListener;
import ru.home.vertel.wheel.WheelView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class VertelActivity extends Activity
{
    private ArrayList<ImagePack> mImagePackArray;
    private Integer mCurrentPackId = 0;
    private WheelView mWheel1, mWheel2, mWheel3;
    private boolean mWheelReady1, mWheelReady2, mWheelReady3;
    private ImageView runWheels;

    private SoundPool mSoundPool;
    private int mSoundStart, mSoundWin;

    private String skinFolder;
    
    private int currentRuns, currentWins;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        skinFolder = Environment.getExternalStorageDirectory().toString() + MyApplication.getDBAdapter().getSkinPath();
        if (initImagePacks())
        {
            initApplication();
        }
        else
        {
            setContentView(R.layout.quit);

            final EditText editText = (EditText) findViewById(R.id.find_edit);
            editText.setText(skinFolder);

            Button findButton = (Button) findViewById(R.id.find_button);
            findButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    skinFolder = editText.getText().toString();
                    if (initImagePacks())
                    {
                        MyApplication.getDBAdapter().updateSkinPath(skinFolder.substring(Environment.getExternalStorageDirectory().toString().length()));
                        initApplication();
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            });

            Button exitButton = (Button) findViewById(R.id.exit_button);
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    public void initApplication()
    {
        setContentView(R.layout.main);

        mWheel1 = (WheelView) findViewById(R.id.vertel_1);
        mWheel2 = (WheelView) findViewById(R.id.vertel_2);
        mWheel3 = (WheelView) findViewById(R.id.vertel_3);
        initWheels();

        clearWheels();

        fillTextViews();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        mSoundStart = mSoundPool.load(this, R.raw.bank, 1);
        mSoundWin = mSoundPool.load(this, R.raw.victory, 1);

        runWheels = (ImageView) findViewById(R.id.lever_animation);
        runWheels.setOnClickListener(new View.OnClickListener()
        {
            @Override
			public void onClick(View v)
            {
                mWheelReady1 = mWheelReady2 = mWheelReady3 = false;
                rollWheels();

                runWheels.setBackgroundResource(R.drawable.frame01);
                runWheels.setBackgroundResource(R.drawable.animation);

                AnimationDrawable frameAnimation = (AnimationDrawable) runWheels.getBackground();
                frameAnimation.start();

                mSoundPool.play(mSoundStart, 1.0f, 1.0f, 0, 0, 1.0f);
            }
        });
    }

    // Wheel scrolled listener
    OnWheelScrollListener scrolledListener = new OnWheelScrollListener()
    {
        @Override
		public void onScrollingStarted(WheelView wheel)
        {
            runWheels.setEnabled(false);
        }

        @Override
		public void onScrollingFinished(WheelView wheel)
        {
            if (wheel == mWheel1) mWheelReady1 = true;
            if (wheel == mWheel2) mWheelReady2 = true;
            if (wheel == mWheel3) mWheelReady3 = true;
            
            if (mWheelReady1 && mWheelReady2 && mWheelReady3)
                checkVictory();
        }
    };

    /**
     * Initializes wheels
     */
    private void initWheels()
    {
        mWheel1.addScrollingListener(scrolledListener);
        mWheel1.setCyclic(true);
        mWheel1.setEnabled(false);

        mWheel2.addScrollingListener(scrolledListener);
        mWheel2.setCyclic(true);
        mWheel2.setEnabled(false);

        mWheel3.addScrollingListener(scrolledListener);
        mWheel3.setCyclic(true);
        mWheel3.setEnabled(false);
    }

    /**
     * Clear wheels
     */
    private void clearWheels()
    {
        mWheelReady1 = mWheelReady2 = mWheelReady3 = false;

        mWheel1.setViewAdapter(new VertelAdapter(mImagePackArray.get(mCurrentPackId)));
        mWheel1.setCurrentItem(0);

        mWheel2.setViewAdapter(new VertelAdapter(mImagePackArray.get(mCurrentPackId)));
        mWheel2.setCurrentItem(0);

        mWheel3.setViewAdapter(new VertelAdapter(mImagePackArray.get(mCurrentPackId)));
        mWheel3.setCurrentItem(0);

        currentRuns = 0;
        currentWins = 0;
    }

    private void checkVictory()
    {
        if (mWheel1.getCurrentItem() == mWheel2.getCurrentItem() &&
            mWheel2.getCurrentItem() == mWheel3.getCurrentItem())
        {
            MyApplication.getDBAdapter().win(mImagePackArray.get(mCurrentPackId).mCheckedResURIs.size());
            currentWins++;

            View view = getLayoutInflater().inflate(R.layout.victory, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(view);

            final AlertDialog alertDialog = alertDialogBuilder.create();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.cancel();
                    runWheels.setEnabled(true);
                }
            });

            alertDialog.show();
            mSoundPool.play(mSoundWin, 1.0f, 1.0f, 0, 0, 1.0f);
        }
        else
        {
            MyApplication.getDBAdapter().lose(mImagePackArray.get(mCurrentPackId).mCheckedResURIs.size());
            runWheels.setEnabled(true);
        }

        currentRuns++;
        fillTextViews();
    }

    /**
     * Mixes wheel
     */
    private void rollWheels()
    {
        mWheel1.scroll(-350 + (int)(Math.random() * 50), 2000);
        mWheel2.scroll(-350 + (int)(Math.random() * 50), 2000);
        mWheel3.scroll(-350 + (int)(Math.random() * 50), 2000);
    }

    public boolean initImagePacks()
    {
        mImagePackArray = new ArrayList<ImagePack>();
        
        String sdState = Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            try {
                File file = new File(skinFolder);
                File[] skinDirs = file.listFiles();

                for (File skinDir : skinDirs) {
                    ArrayList<String> skinFileFullPaths = new ArrayList<String>();

                    File[] skinFiles = skinDir.listFiles(new ImageFilenameFilter());

                    if (skinFiles.length == 0) continue;
                    for (File skinFile : skinFiles)
                    {
                        skinFileFullPaths.add(skinFile.getAbsolutePath());
                    }
                    mImagePackArray.add(new ImagePack(skinDir.getName(), skinFileFullPaths.toArray()));
                }
                
            } catch (Exception e) {
                Toast.makeText(this, R.string.no_images, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else
        {
            Toast.makeText(this, R.string.no_SD, Toast.LENGTH_LONG).show();
            return false;
        }

        if (mImagePackArray.size() == 0)
        {
            Toast.makeText(this, R.string.no_images, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK)
        {
            mCurrentPackId = requestCode;
            mImagePackArray.set(mCurrentPackId, (ImagePack) intent.getParcelableExtra("ImagePack"));

        }

        initApplication();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (runWheels != null && runWheels.isEnabled())
        {
            if(keyCode == KeyEvent.KEYCODE_MENU && mImagePackArray.size() > 0)
            {
                MenuDialog menuDialog = new MenuDialog(this, mImagePackArray);
                menuDialog.show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
    public void fillTextViews()
    {
        final int items = mImagePackArray.get(mCurrentPackId).mCheckedResURIs.size();

        View currentLayout = findViewById(R.id.current_layout);

        float percentCurrent = (currentRuns > 0) ? currentWins * 100.0f / currentRuns : 0;

        TextView currentItemsTextView = (TextView) currentLayout.findViewById(R.id.list_item_items);
        TextView currentRunsTextView = (TextView) currentLayout.findViewById(R.id.list_item_runs);
        TextView currentWinsTextView = (TextView) currentLayout.findViewById(R.id.list_item_wins);
        TextView currentPercentTextView = (TextView) currentLayout.findViewById(R.id.list_item_percent);
        TextView currentChanceTextView = (TextView) currentLayout.findViewById(R.id.list_item_chance);

        currentItemsTextView.setText("" + items);
        currentRunsTextView.setText("" + currentRuns);
        currentWinsTextView.setText("" + currentWins);
        currentPercentTextView.setText(String.format("%.2f%%", percentCurrent));
        currentChanceTextView.setText(String.format("%.2f%%", mImagePackArray.get(mCurrentPackId).getWinPercent()));

    }
    
    private class ImageFilenameFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File file, String s) {
            return (s.toLowerCase().endsWith(".png") ||
                    s.toLowerCase().endsWith(".ico") ||
                    s.toLowerCase().endsWith(".gif"));
        }
    }
}
