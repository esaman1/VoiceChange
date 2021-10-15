package com.example.storage.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.storage.data.RecordFile;

import java.util.List;

@Dao
public interface RecordFileDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RecordFile recordFile);

    @Query("DELETE FROM RecordFile WHERE path = :path")
    void deleteFile(String path);

    @Query("SELECT * FROM RecordFile ORDER BY name ASC")
    LiveData<List<RecordFile>> getAllFile();
}
