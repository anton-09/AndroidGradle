package ru.home.mediafilerenamer.helper;

import ru.home.mediafilerenamer.entities.MediaDir;

public interface OneFileCallback
{
    void handleProgress(MediaDir result);
    void handleFinal(MediaDir result);
}
