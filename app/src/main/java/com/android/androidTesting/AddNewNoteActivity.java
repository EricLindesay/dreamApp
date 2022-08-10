package com.android.androidTesting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;

public class AddNewNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        final TextView dateInput =  findViewById(R.id.dateInput);
        final TextView descriptionInput =  findViewById(R.id.descriptionInput);
        Button saveButton =  findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewNote(dateInput.getText().toString(), descriptionInput.getText().toString());
            }
        });
    }

    private void saveNewNote(String date, String description) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());

        Note note = new Note();
        note.date = date;
        note.description = description;
        db.noteDao().insertNote(note);

        finish();

    }
}