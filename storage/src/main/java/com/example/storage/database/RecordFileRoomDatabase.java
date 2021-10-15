package com.example.storage.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.storage.data.RecordFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {RecordFile.class}, version = 1, exportSchema = false)
public abstract class RecordFileRoomDatabase extends RoomDatabase {

    public abstract RecordFileDao recordFileDao();

    private static volatile RecordFileRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RecordFileRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordFileRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordFileRoomDatabase.class, "record_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}