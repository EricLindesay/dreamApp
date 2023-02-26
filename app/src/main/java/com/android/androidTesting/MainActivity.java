package com.android.androidTesting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.androidTesting.adapters.NoteListAdapter;
import com.android.androidTesting.adapters.TagList;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.widgets.CollectionWidget;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ShowsNotes {
    private NoteListAdapter noteListAdapter;
    public Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the display
        setContentView(R.layout.activity_main);

        // Initialise the buttons
        initialiseAddNewNoteButton();
        initialiseSearchButton();
        initialiseBackButton();

        // Initialise the recycler view and populate it
        initRecyclerView();
        loadNoteList();

        Intent intent = getIntent();
        if (intent != null) {
            // Widget started activity so go to the correct next activity.
            nextActivity(intent);
        }
    }

    void initialiseAddNewNoteButton() {
        // If the user clicks on the 'add new note' button, take them to the add note screen
        Button addNewUserButton = findViewById(R.id.addNewNoteButton);
//        TextView addNewUserButton = findViewById(R.id.addNewNoteButton);
        addNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewNote();
            }
        });
    }

    void addNewNote() {
        // If the user wants to add a new note, send them to the note activity with the note ID -1
        // which signifies it is a new note.
        TagList tagList = TagList.getInstance();
        tagList.clear();
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("noteID", -1);
        startActivityForResult(intent, 100);
    }

    void initialiseSearchButton() {
        // If the user clicks on the search button, take them to the search screen
        ImageView searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Run the search activity
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, 100);
            }
        });

//        searchButton.setColorFilter(null);
//        searchButton.setColorFilter(context.getColor(R.color.darkModeText));
    }

    void initialiseBackButton() {
        // If the user clicks the back button, just exit the current activity and close the app
        ImageView backButton = findViewById(R.id.toolbarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initRecyclerView() {
        // Initialise the recycler view, showing a list of notes on the main screen.
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        noteListAdapter = new NoteListAdapter(this, this);
        recyclerView.setAdapter(noteListAdapter);
    }

    private void loadNoteList() {
        // Load the list of notes to show on the screen.
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Note> noteList = db.noteDao().getAllNotes();
        noteListAdapter.setNoteList(noteList);
        Log.d("Eric", "Note List loaded");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100) {
            // If the activity has code 100, refresh the recycler view and update the home screen widget
            refreshData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void clickedNote(Note note) {
        // Start the NoteActivity activity, passing through the noteID.
        int noteID = note.nid;
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
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

    void refreshData() {
        // Refresh the recycler data and refresh the home screen widget.
        loadNoteList();
        Log.d("RefreshWidget", "Sent Broadcast");
        CollectionWidget.sendRefreshBroadcast(context);
    }

    void nextActivity(Intent intent) {
        // If the user clicks on a widget thing, they may not want to go just to the main screen
        // so see where they want to go and take them there.
        Uri intentData = intent.getData();
        if (intentData != null) {
            if (intentData.toString().equals("addNewNote")) {  // if the user wants to add a new note through widget
                addNewNote();
            } else if (intentData.toString().contains("editNote")) {  // if the user wants to edit a note through the widget
                editNoteThroughWidget(intentData);
            }
        }
    }

    void editNoteThroughWidget(Uri intentData) {
        // The intent data is in the form "editNote:{number}"
        // So split the intentData by : and find the number
        TagList tagList = TagList.getInstance();
        tagList.clear();

        String[] sections = intentData.toString().split(":");
        int notePos = Integer.parseInt(sections[1]);

        // notePos means it is the n'th note in the list, not note with ID n.
        // so find the note ID for note in position n. Since we know how it is sorted, this is easy.
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<Note> allNotes = db.noteDao().getAllNotes();
        Note note = allNotes.get(notePos);
        clickedNote(note);
    }
}
