package com.android.androidTesting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteProgram;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.android.androidTesting.adapters.NoteListAdapter;
import com.android.androidTesting.adapters.TagList;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.LinkTable;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.db.Tag;
import com.android.androidTesting.utility.CalendarClass;
import com.android.androidTesting.utility.FormatNote;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText startDate, endDate, searchInput;
    NoteListAdapter noteListAdapter;
    long startDateLong = -1;
    long endDateLong = -1;
    String[] searchTerms = null;
    TagList tagList = new TagList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // if search bar has anything in it, only show notes with that phrase in the description
        // if the date has anything in it, only show notes with that date
        // if the X is pressed, clear the date fields
        // if tags is pressed
            // go into the tags menu so they can select tags to search for
            // only show notes for those tags

        ImageView backButton = findViewById(R.id.toolbarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchTermChanged(searchInput.getText().toString());
            }

            @Override public void afterTextChanged(Editable editable) {}
        });

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

        loadTagList();
        Button tagButton = findViewById(R.id.tagsButton);
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTagList();
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

    private void openTagList() {
        Intent intent = new Intent(SearchActivity.this, TagsActivity.class);
        intent.putExtra("noteID", -1);
        startActivityForResult(intent, 100);
    }

    private void loadTagList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        ArrayList<Tag> tags = new ArrayList<Tag>(db.tagDao().getAllTags());
        tagList.initialiseTagList(tags);
    }


    private void loadNoteList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        StringBuilder query = new StringBuilder("SELECT * FROM note WHERE 1 == 1");
        // filter for date
        if (startDateLong == -1 && endDateLong == -1) {
            // skip
        }
        else if (startDateLong != -1 && endDateLong == -1) {  // start date exists but end doesn't
            query.append(" AND date >= "+startDateLong);
        }
        else {
            query.append(" AND date BETWEEN "+startDateLong+" AND "+endDateLong);
        }

        // filter for search terms
        if (searchTerms != null) {
            for (String term : searchTerms) {
                // really should make sure the input is clean but idc
                query.append(" AND description LIKE '%"+term+"%'");
            }
        }

        // Filter tags
        List<String> selectedTags = TagList.selectedTags();
        if (!selectedTags.isEmpty()) {
            StringBuilder tags = new StringBuilder();
            for (String tag : selectedTags) {
                tags.append("\'"+tag+"\',");
            }
            tags.deleteCharAt(tags.length()-1);
            query.append(" AND nid IN (SELECT lt.nid FROM linktable lt WHERE lt.tid IN ("+tags.toString()+") GROUP BY lt.nid HAVING COUNT(*) >= "+selectedTags.size()+")");
        }

        List<Note> noteList = db.noteDao().filterNotes(new SimpleSQLiteQuery(query.toString()));
        noteListAdapter.setNoteList(noteList);
    }

    public void clickedNote(int noteID) {
        Log.w("Debugging", "Clicked Note");
        TagList.clear();
        Intent intent = new Intent(SearchActivity.this, NoteActivity.class);
        intent.putExtra("noteID", noteID);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100) {
            loadNoteList();
            loadTagList();
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

    void searchTermChanged(String input) {
        if (input.isEmpty()) {
            searchTerms = null;
        } else {
            searchTerms = input.split(", ");
        }
        loadNoteList();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    void exit() {
        TagList.clear();
        finish();
    }
}
