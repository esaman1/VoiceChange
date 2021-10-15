package com.example.storage;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.storage.data.RecordFile;

import java.util.List;

class FileManagerHighApi implements RecordFileManager{
    private final Context applicationContext;
    public FileManagerHighApi(Context context) {
        applicationContext = context.getApplicationContext();
    }

    @Override
    public LiveData<List<RecordFile>> getRecordFiles() {
        return null;
    }

    @Override
    public String genFilePath() {
        return null;
    }

    @Override
    public void delete(String path) {

    }

    @Override
    public void save(String path) {

    }
}
