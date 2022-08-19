package com.android.androidTesting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.androidTesting.adapters.NoteListAdapter;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.utility.CalendarClass;
import com.android.androidTesting.utility.FormatNote;

import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText startDate, endDate, searchInput;
    NoteListAdapter noteListAdapter;
    long startDateLong = -1;
    long endDateLong = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // if search bar has anything in it, only show notes with that phrase in the description
        // if the date has anything in it, only show notes with that date
        // if the calendar is pressed, update the date text fields
        // if the X is pressed, clear the date fields
        // if tags is pressed
            // go into the tags menu so they can select tags to search for
            // only show notes for those tags

        startDate = findViewById(R.id.startDateInput);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarClass(SearchActivity.this, startDate);
            }
        });
        startDate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                startDateChanged(true);
            }

            @Override public void afterTextChanged(Editable editable) {}
        });

        endDate = findViewById(R.id.endDateInput);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarClass(SearchActivity.this, endDate);
            }
        });
        endDate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                startDateChanged(false);
            }

            @Override public void afterTextChanged(Editable editable) {}
        });

        ImageView clearStartDate = findViewById(R.id.clearStartDate);
        clearStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate.setText("");
            }
        });

        ImageView clearEndDate = findViewById(R.id.clearEndDate);
        clearEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDate.setText("");
            }
        });

        initRecyclerView();
        loadNoteList();
        Log.d("Eric", "Search screen loaded");
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        noteListAdapter = new NoteListAdapter(this, this);
        recyclerView.setAdapter(noteListAdapter);

    }

    private void loadNoteList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Note> noteList;
        if (startDateLong == -1 && endDateLong == -1)
            noteList = db.noteDao().getAllNotes();
        else if (startDateLong != -1 && endDateLong == -1)
            noteList = db.noteDao().getNotesBetweenDates(startDateLong);
        else
            noteList = db.noteDao().getNotesBetweenDates(startDateLong, endDateLong);
        noteListAdapter.setNoteList(noteList);
    }


    public void clickedNote(int noteid) {
        Log.w("Debugging", "Clicked Note");
        Intent intent = new Intent(SearchActivity.this, NoteActivity.class);
        intent.putExtra("noteid", noteid);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100) {
            loadNoteList();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void startDateChanged(boolean start) {
        if (start) {
            if (startDate.getText().toString().isEmpty()) startDateLong = -1;
            else startDateLong = FormatNote.formatDate(startDate.getText().toString());
        } else {
            if (endDate.getText().toString().isEmpty()) endDateLong = -1;
            else endDateLong = FormatNote.formatDate(endDate.getText().toString());
        }
        loadNoteList();
    }
}
