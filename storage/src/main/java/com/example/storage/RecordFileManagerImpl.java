package com.example.storage;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.storage.data.RecordFile;

import java.util.List;

public class RecordFileManagerImpl implements RecordFileManager{

    private final RecordFileManager delegate;
    public RecordFileManagerImpl(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            delegate = new FileManagerHighApi(context);
        }else{
            delegate = new FileManagerLowApi(context) ;
        }
    }

    @Override
    public LiveData<List<RecordFile>> getRecordFiles() {
        return delegate.getRecordFiles();
    }

    @Override
    public String genFilePath() {
        return delegate.genFilePath();
    }

    @Override
    public void delete(String path) {
        delegate.delete(path);
    }

    @Override
    public void save(String path) {
        delegate.save(path);
    }
}
