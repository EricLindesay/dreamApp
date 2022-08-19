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
    EditText yearInput, monthInput, dayInput, searchInput;
    NoteListAdapter noteListAdapter;
    long fromDate = 0;
    long toDate = 0;

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

        yearInput = findViewById(R.id.yearInput);
        monthInput = findViewById(R.id.monthInput);
        dayInput = findViewById(R.id.dayInput);
        yearInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { dateChanged(); }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        monthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { dateChanged(); }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        dayInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { dateChanged(); }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        // Setup calendar
        ImageView calendar = findViewById(R.id.calendarIcon);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarClass(SearchActivity.this, yearInput, monthInput, dayInput);
            }
        });

        // Setup date clear button
        ImageView clearDate = findViewById(R.id.clearDate);
        clearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearInput.setText("");
                monthInput.setText("");
                dayInput.setText("");
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
        if (fromDate == 0 && toDate == 0)
            noteList = db.noteDao().getAllNotes();
        else
            noteList = db.noteDao().getNotesBetweenDates(fromDate, toDate);
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

    void dateChanged() {
        String year = yearInput.getText().toString();
        String month = monthInput.getText().toString();
        String day = dayInput.getText().toString();
        if (year.isEmpty() && month.isEmpty() && day.isEmpty()) fromDate = toDate = 0;
        else {
            // 0L = 1st Jan 1970
            String firstDate = "";
            String secondDate = "";

            if (!year.isEmpty() && month.isEmpty() && day.isEmpty()) {
                firstDate += "" + Integer.parseInt(year)+"-01-01";
                secondDate += "" + (Integer.parseInt(year) + 1)+"-01-01";
            } else if (!year.isEmpty() && !month.isEmpty() && day.isEmpty()) {
                firstDate += "" + Integer.parseInt(year)+"-"+Integer.parseInt(month)+"-01";
                secondDate += "" + Integer.parseInt(year)+"-"+(Integer.parseInt(month)+1)+"-01";
            } else if (!year.isEmpty() && !month.isEmpty() && !day.isEmpty()) {
                firstDate += "" + Integer.parseInt(year)+"-"+Integer.parseInt(month)+"-"+Integer.parseInt(day);
                secondDate += "" + Integer.parseInt(year)+"-"+Integer.parseInt(month)+"-"+(Integer.parseInt(day)+1);
            }
            Log.d("Eric", "First: "+firstDate);
            Log.d("Eric", "Second: "+secondDate);
            fromDate = FormatNote.formatDate(firstDate);
            toDate = FormatNote.formatDate(secondDate);
        }
        loadNoteList();
    }
}
