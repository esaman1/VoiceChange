package com.example.storage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.storage.data.RecordFile;
import com.example.storage.database.RecordFileDao;
import com.example.storage.database.RecordFileRoomDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class FileManagerLowApi implements RecordFileManager{
    RecordFileRoomDatabase recordFileRoomDatabase;
    private final Context applicationContext;
    private RecordFileDao recordFileDao;

    public FileManagerLowApi(Context applicationContext) {
        this.applicationContext = applicationContext;
        recordFileRoomDatabase = RecordFileRoomDatabase.getDatabase(applicationContext);
        recordFileDao = recordFileRoomDatabase.recordFileDao();
    }

    @Override
    public LiveData<List<RecordFile>> getRecordFiles() {
        return recordFileDao.getAllFile();
    }

    @Override
    public String genFilePath() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + File.separator + "CallVoiceChanger");
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(file);
        stringBuilder.append("/Audio-");
        stringBuilder.append(path);
        stringBuilder.append(".mp3");

        return stringBuilder.toString();
    }

    @Override
    public void delete(String path) {
        recordFileDao.deleteFile(path);
    }

    @Override
    public void save(String path) {
//        File file = new File(path);
//        recordFileDao.insert(new RecordFile(path, file.getName()));
    }
}
