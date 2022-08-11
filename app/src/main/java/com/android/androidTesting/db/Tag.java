package com.android.androidTesting.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Tag {
    @PrimaryKey(autoGenerate = true)
    public int tid;

    @ColumnInfo(name = "name")
    public String name;
}
