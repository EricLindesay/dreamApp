package com.android.androidTesting.db;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TagDao {

    @Query("SELECT * FROM tag ORDER BY tid ASC")
    List<Tag> getAllTags();

    @Query("SELECT * FROM tag WHERE tid == :tagid LIMIT 1")
    Tag getTagById(String tagid);

    @Query("SELECT * FROM tag WHERE tid LIKE :tagname ORDER BY tid ASC")
    List<Tag> getTagsByName(String tagname);

    @Insert(onConflict=IGNORE)
    void insert(Tag tag);

    @Delete
    void delete(Tag tag);
}
