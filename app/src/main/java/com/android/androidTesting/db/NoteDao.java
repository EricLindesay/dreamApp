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

    @Query("SELECT * FROM note WHERE date >= :date1 AND date < :date2")
    List<Note> getNotesBetweenDates(long date1, long date2);

    @Insert
    long insertNote(Note note);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);
}
