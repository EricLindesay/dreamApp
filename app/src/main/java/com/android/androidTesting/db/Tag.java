package com.android.androidTesting.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Tag {
    @NonNull
    @PrimaryKey
    public String tid;
}
