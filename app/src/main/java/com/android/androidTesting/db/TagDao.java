package com.android.androidTesting.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TagDao {

    @Query("SELECT * FROM tag")
    List<Tag> getAllTags();

    @Query("SELECT * FROM tag WHERE tid == :tagid LIMIT 1")
    Note getTagById(int tagid);

    @Insert
    void insertTag(Tag... tags);

    @Delete
    void delete(Tag tag);
}
