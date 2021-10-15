package com.voicechanger.soundeffect.soundchanger.task;

public interface IDBTaskListener {
    void onDoInBackground();

    void onPostExcute();

    void onPreExcute();
}
