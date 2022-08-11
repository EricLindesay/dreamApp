package com.android.androidTesting.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LinkTableDao {

    @Query("SELECT * FROM LinkTable")
    List<LinkTable> getAllLinks();

    @Query("SELECT * FROM linktable WHERE lid == :linkid LIMIT 1")
    Note getLinkById(int linkid);

    @Insert
    void insertLink(LinkTable... links);

    @Delete
    void delete(LinkTable link);
}
