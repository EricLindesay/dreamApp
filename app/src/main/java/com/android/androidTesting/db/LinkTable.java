package com.android.androidTesting.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"nid","tid"})
public class LinkTable {
    @NonNull
    @ColumnInfo(name = "nid")
    public int nid;

    @NonNull
    @ColumnInfo(name = "tid")
    public String tid;
}
