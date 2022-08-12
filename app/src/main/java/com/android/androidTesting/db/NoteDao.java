package com.android.androidTesting.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM note ORDER BY date DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM note WHERE nid == :noteid LIMIT 1")
    Note getNoteById(int noteid);

    @Insert
    void insertNote(Note... notes);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);
}
