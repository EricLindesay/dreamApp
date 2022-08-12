package com.android.androidTesting.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LinkTable {
    @PrimaryKey(autoGenerate = true)
    public int lid;

    @ColumnInfo(name = "nid")
    public int nid;

    @ColumnInfo(name = "tid")
    public String tid;
}
