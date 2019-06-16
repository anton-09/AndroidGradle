package ru.home.mediafilerenamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity
{
    Switch jpgToggleButton;
    Switch mp4ToggleButton;
    Switch jpegToggleButton;
    Switch verboseLogToggleButton;
    Switch showMediaFilesToggleButton;
    EditText initialFolderEditText;
    EditText renameMaskEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        jpgToggleButton = (Switch) findViewById(R.id.jpg_switch);
        mp4ToggleButton = (Switch) findViewById(R.id.mp4_switch);
        jpegToggleButton = (Switch) findViewById(R.id.jpeg_switch);
        verboseLogToggleButton = (Switch) findViewById(R.id.verbose_log_switch);
        showMediaFilesToggleButton = (Switch) findViewById(R.id.show_media_files_switch);
        initialFolderEditText = (EditText) findViewById(R.id.initial_folder_edit_text);
        renameMaskEditText = (EditText) findViewById(R.id.rename_mask_edit_text);

        jpgToggleButton.setChecked(MyApplication.isJpgOn());
        mp4ToggleButton.setChecked(MyApplication.isMp4On());
        jpegToggleButton.setChecked(MyApplication.isJpegOn());
        verboseLogToggleButton.setChecked(MyApplication.isVerboseLog());
        showMediaFilesToggleButton.setChecked(MyApplication.isShowMediaFiles());
        initialFolderEditText.setText(MyApplication.getInitialFolder());
        renameMaskEditText.setText(MyApplication.getRenameMask());
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        MyApplication.setJpgOn(jpgToggleButton.isChecked());
        MyApplication.setMp4On(mp4ToggleButton.isChecked());
        MyApplication.setJpegOn(jpegToggleButton.isChecked());
        MyApplication.setVerboseLog(verboseLogToggleButton.isChecked());
        MyApplication.setShowMediaFiles(showMediaFilesToggleButton.isChecked());
        MyApplication.setInitialFolder(initialFolderEditText.getText().toString());
        MyApplication.setRenameMask(renameMaskEditText.getText().toString());

        MyApplication.getSharedPreferences().edit()
                .putBoolean("mJpgOn", MyApplication.isJpgOn())
                .putBoolean("mMp4On", MyApplication.isMp4On())
                .putBoolean("mJpegOn", MyApplication.isJpegOn())
                .putBoolean("mVerboseLog", MyApplication.isVerboseLog())
                .putBoolean("mShowMediaFiles", MyApplication.isShowMediaFiles())
                .putString("mInitialFolder", MyApplication.getInitialFolder())
                .putString("mRenameMask", MyApplication.getRenameMask())
                .apply();
    }
}
