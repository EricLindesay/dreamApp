package com.android.androidTesting;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

    boolean editable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_note);

        // get the textviews to display on
        final TextView dateTV = findViewById(R.id.dateTV);
        final TextView descriptionTV = findViewById(R.id.descriptionTV);
        final EditText dateEV = findViewById(R.id.dateEV);
        final EditText descriptionEV = findViewById(R.id.descriptionEV);
        // get the note's id
        Bundle extras = getIntent().getExtras();
        int noteid = extras.getInt("noteid");
        // get the note
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        Note note =  db.noteDao().getNoteById(noteid);

        // print the note on the screen
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        dateTV.setText(df.format(new Date(note.date)));
        descriptionTV.setText(note.description);
        dateEV.setText(df.format(new Date(note.date)));
        descriptionEV.setText(note.description);
        dateEV.setVisibility(View.GONE);
        descriptionEV.setVisibility(View.GONE);

        Button returnButton =  findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editable) {
                    dateTV.setVisibility(View.GONE);
                    descriptionTV.setVisibility(View.GONE);
                    dateEV.setVisibility(View.VISIBLE);
                    descriptionEV.setVisibility(View.VISIBLE);
                } else {
                    dateTV.setVisibility(View.VISIBLE);
                    descriptionTV.setVisibility(View.VISIBLE);
                    dateEV.setVisibility(View.GONE);
                    descriptionEV.setVisibility(View.GONE);
                }
                editable = !editable;
            }
        });
    }
}