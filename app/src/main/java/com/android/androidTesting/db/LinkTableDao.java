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

    @Query("SELECT nid FROM LinkTable WHERE tid == :tagID")
    List<Integer> getAllNotesWithTag(String tagID);

    @Query("SELECT tid FROM LinkTable WHERE nid == :noteID")
    List<String> getAllTagsForNote(int noteID);

    @Insert
    void insertLink(LinkTable... links);

    @Delete
    void delete(LinkTable link);
}
