package ru.home.mediafilerenamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity
{
    Switch jpgToggleButton;
    Switch mp4ToggleButton;
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
        verboseLogToggleButton = (Switch) findViewById(R.id.verbose_log_switch);
        showMediaFilesToggleButton = (Switch) findViewById(R.id.show_media_files_switch);
        initialFolderEditText = (EditText) findViewById(R.id.initial_folder_edit_text);
        renameMaskEditText = (EditText) findViewById(R.id.rename_mask_edit_text);

        jpgToggleButton.setChecked(MyApplication.isJPGon());
        mp4ToggleButton.setChecked(MyApplication.isMP4on());
        verboseLogToggleButton.setChecked(MyApplication.isVerboseLog());
        showMediaFilesToggleButton.setChecked(MyApplication.isShowMediaFiles());
        initialFolderEditText.setText(MyApplication.getInitialFolder());
        renameMaskEditText.setText(MyApplication.getRenameMask());
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        MyApplication.setJPGon(jpgToggleButton.isChecked());
        MyApplication.setMP4on(mp4ToggleButton.isChecked());
        MyApplication.setVerboseLog(verboseLogToggleButton.isChecked());
        MyApplication.setShowMediaFiles(showMediaFilesToggleButton.isChecked());
        MyApplication.setInitialFolder(initialFolderEditText.getText().toString());
        MyApplication.setRenameMask(renameMaskEditText.getText().toString());

        MyApplication.getSharedPreferences().edit()
                .putBoolean("mJPGon", MyApplication.isJPGon())
                .putBoolean("mMP4on", MyApplication.isMP4on())
                .putBoolean("mVerboseLog", MyApplication.isVerboseLog())
                .putBoolean("mShowMediaFiles", MyApplication.isShowMediaFiles())
                .putString("mInitialFolder", MyApplication.getInitialFolder())
                .putString("mRenameMask", MyApplication.getRenameMask())
                .apply();
    }
}
