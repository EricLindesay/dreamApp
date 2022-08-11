package com.android.androidTesting;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReadNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_note);

        // get the textviews to display on
        TextView dateOutput = findViewById(R.id.dateOutput);
        TextView descriptionOutput = findViewById(R.id.descriptionOutput);
        // get the note's id
        Bundle extras = getIntent().getExtras();
        int noteid = extras.getInt("noteid");
        // get the note
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        Note note =  db.noteDao().getNoteById(noteid);

        // print the note on the screen
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        dateOutput.setText(df.format(new Date(note.date)));
        descriptionOutput.setText(note.description);

        Button returnButton =  findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}