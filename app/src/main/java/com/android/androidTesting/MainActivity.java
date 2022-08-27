package com.android.androidTesting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.androidTesting.adapters.NoteListAdapter;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NoteListAdapter noteListAdapter;
    public Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        Button addNewUserButton = findViewById(R.id.addNewNoteButton);
        addNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewNote();
            }
        });

        initRecyclerView();
        loadNoteList();
        Log.d("Eric", "User List loaded");

        Intent intent = getIntent();
        if (intent != null) {
            Log.d("Eric", "intent got "+intent.toString());
            Uri intentData = intent.getData();
            if (intentData != null) {
                if (intentData.toString().equals("addNewNote")) {
                    addNewNote();
                }
            }
        }
    }

    private void addNewNote() {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("noteID", -1);
        startActivityForResult(intent, 100);
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
        List<Note> noteList =  db.noteDao().getAllNotes();
        noteListAdapter.setNoteList(noteList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100) {
            loadNoteList();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clickedNote(int noteID) {
        Log.w("Debugging", "Clicked Note");
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("noteID", noteID);
        startActivityForResult(intent, 100);
    }

    public void deleteNote(Note note) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        db.linkTableDao().deleteLinksToNote(note.nid);
        db.noteDao().delete(note);
        loadNoteList();
    }
}