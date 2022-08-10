package com.android.androidTesting.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note {

    @PrimaryKey(autoGenerate = true)
    public int nid;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "description")
    public String description;
}
