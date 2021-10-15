package com.example.storage;

import androidx.lifecycle.LiveData;

import com.example.storage.data.RecordFile;

import java.util.List;

interface RecordFileManager {

     LiveData<List<RecordFile>> getRecordFiles();

     String genFilePath();

    public void delete(String path);

    public void save(String path);
}
