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

import com.android.androidTesting.adapters.NoteListAdapter;
import com.android.androidTesting.adapters.TagList;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.db.Tag;
import com.android.androidTesting.utility.CalendarClass;
import com.android.androidTesting.utility.Format;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ShowsNotes {
    EditText startDate, endDate, searchInput;
    NoteListAdapter noteListAdapter;
    long startDateLong = -1;
    long endDateLong = -1;
    String[] searchTerms = null;
    TagList tagList = TagList.getInstance();

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

        initialiseBackButton();
        initialiseSearchInput();

        startDate = findViewById(R.id.startDateInput);
        initialiseDate(startDate, true);
        endDate = findViewById(R.id.endDateInput);
        initialiseDate(endDate, false);

        initialiseClearDateButton(R.id.clearStartDate, startDate);
        initialiseClearDateButton(R.id.clearEndDate, endDate);

        initialiseTagButton();

        initRecyclerView();
        refreshData();
        Log.d("Eric", "Search screen loaded");
    }

    void initialiseBackButton() {
        // When the back button is pressed exit and return to the main screen
        ImageView backButton = findViewById(R.id.toolbarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
    }

    void initialiseSearchInput() {
        // When the user types something in the search bar, update the noteList and filter with the
        // new term
        searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadNoteList();
            }

            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    void initialiseDate(EditText date, boolean isStartDate) {
        // When the date EditText is clicked, pop up a calendar allowing the user to click a date.
        // Also when the date is selected the text changes, calling a function to filter out
        // the incorrect notes.
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarClass(SearchActivity.this, date);
            }
        });
        date.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadNoteList();  // when the date is changed, update the list of notes
            }

            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    void initialiseClearDateButton(int id, EditText date) {
        // When the clear date button is selected, clear the appropriate date's text.
        // Changing the text to blank then calls the date's onTextChanged function.
        ImageView clearDate = findViewById(id);
        clearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setText("");
            }
        });
    }

    void initialiseTagButton() {
        // WHen the tag button is pressed, open the tag list.
        Button tagButton = findViewById(R.id.tagsButton);
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTagList();
            }
        });
    }

    private void initRecyclerView() {
        // Initialise the recyclerView, showing the notes as a list
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        noteListAdapter = new NoteListAdapter(this, this);
        recyclerView.setAdapter(noteListAdapter);
    }

    private void openTagList() {
        // Opens the tag list activity
        Intent intent = new Intent(SearchActivity.this, TagsActivity.class);
        intent.putExtra("noteID", -1);
        startActivityForResult(intent, 100);
    }

    private void loadTagList() {
        // Load the tag list with all of the tags
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        ArrayList<Tag> tags = new ArrayList<>(db.tagDao().getAllTags());
        tagList.initialiseTagList(tags);
    }

    private void loadNoteList() {
        // Load the correct notes.
        // The notes have to be filtered a lot.
        // Filter it by date, by description search term and by selected tags
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM note WHERE 1 == 1");
        query.append(dateFilter());  // filter by date
        query.append(searchFilter());  // filter by search term
        query.append(tagFilter());  // filter by tags
        query.append(" ORDER BY date DESC");

        List<Note> noteList = db.noteDao().filterNotes(new SimpleSQLiteQuery(query.toString()));
        noteListAdapter.setNoteList(noteList);
    }

    String dateFilter() {
        // Filter dates
        String ret = "";
        startDateLong = dateToLong(startDate.getText().toString());  // convert the dates from text to long
        endDateLong = dateToLong(endDate.getText().toString());

        if (startDateLong == -1 && endDateLong == -1) {
            // neither of the dates have any inputs, so don't filter for date
        }
        else if (startDateLong != -1 && endDateLong == -1) {  // start date exists but end doesn't
            ret = " AND date >= "+startDateLong;
        }
        else {
            // If the start date doesn't exist, startDateLong = -1, so we don't need to do another
            // condition for it.
            ret = " AND date BETWEEN "+startDateLong+" AND "+endDateLong;
        }
        return ret;
    }

    String searchFilter() {
        // Filter for search terms
        String ret = "";
        String searchString = searchInput.getText().toString();

        if (!searchString.isEmpty()) {
            // Each term is split by "," and trimmed
            for (String term : searchString.split(",")) {
                // really should make sure the input is clean but idc
                ret = " AND description LIKE '%"+term.trim()+"%'";
            }
        }
        return ret;
    }

    String tagFilter() {
        // Filter tags
        String ret = "";
        List<String> selectedTags = tagList.getSelected();  // tags to filter on

        if (!selectedTags.isEmpty()) {
            String tags = "\'"+String.join("\',\'", selectedTags)+"\'";  // a string in the form 'tag1','tag2','tag3'

            // Select the notes in the linktable which have tags in the selectedTags list. This will
            // give duplicate note ids since multiple tags can belong to the same note.
            // So count how many of these duplicates there are and it should be equal to the number
            // of tags that you are searching for.
            ret = " AND nid IN (SELECT lt.nid FROM linktable lt WHERE lt.tid IN ("+tags+") GROUP BY lt.nid HAVING COUNT(*) >= "+selectedTags.size()+")";
        }
        return ret;
    }

    @Override
    public void clickedNote(Note note) {
        // When you click a note, go into 'edit note' mode.
        int noteID = note.nid;
        Log.w("Debugging", "Clicked Note");
        tagList.clear();
        Intent intent = new Intent(SearchActivity.this, NoteActivity.class);
        intent.putExtra("noteID", noteID);
        startActivityForResult(intent, 100);
    }

    @Override
    public void deleteNote(Note note) {
        // Remove the note from the database.
        // Must remove all links to the note in the LinkTable.
        // Then remove the note itself
        // Then update the
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        db.linkTableDao().deleteLinksToNote(note.nid);
        db.noteDao().delete(note);
        refreshData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100) {
            // When the activity returns here, reload the note list and reload the tags
            refreshData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void refreshData() {
        loadNoteList();
        loadTagList();
    }

    long dateToLong(String date) {
        // Convert a date from string to long.
        if (date.isEmpty()) return -1;
        return Format.date(date);
    }

    @Override
    public void onBackPressed() {
        // When the user presses the inbuilt back button, exit this activity and go back to main.
        exit();
    }

    void exit() {
        // When you exit the activity, first clear the tag list, then finish the activity.
        tagList.clear();
        finish();
    }
}
