package com.example.storage.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecordFile {
    @PrimaryKey
    @NonNull
    public final String path;
    public final String name;

    public RecordFile(String path, String name) {
        this.path = path;
        this.name = name;
    }
}
