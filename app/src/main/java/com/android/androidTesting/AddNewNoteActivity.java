package com.android.androidTesting;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatWidthException;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.androidTesting.adapters.TagList;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.LinkTable;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.utility.FormatNote;

public class AddNewNoteActivity extends AppCompatActivity {
    int noteid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        final TextView dateInput =  findViewById(R.id.dateInput);
        final TextView descriptionInput =  findViewById(R.id.descriptionInput);

        Bundle extras = getIntent().getExtras();
        noteid = extras.getInt("noteid");
        if (noteid != -1) {
            updateContent(dateInput, descriptionInput);
        }

        // on below line we are adding click listener
        // for our pick date button
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        AddNewNoteActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                monthOfYear++;
                                String monthOutput;
                                if (monthOfYear < 10) monthOutput = "0"+monthOfYear;
                                else monthOutput = ""+monthOfYear;

                                String dayOutput;
                                if (dayOfMonth < 10) dayOutput = "0"+dayOfMonth;
                                else dayOutput = ""+dayOfMonth;
                                dateInput.setText(year + "-" + monthOutput + "-" + dayOutput);
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        Button todayButton = findViewById(R.id.todayButton);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();  // get current date
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                dateInput.setText(df.format(now));
            }
        });

        Button saveButton =  findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewNote(dateInput.getText().toString(), descriptionInput.getText().toString());
            }
        });

        Button tagButton = findViewById(R.id.tagsButton);
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTagList();
            }
        });
    }

    private void openTagList() {
        Intent intent = new Intent(AddNewNoteActivity.this, AddTagsActivity.class);
        intent.putExtra("noteid", noteid);
        startActivityForResult(intent, 100);
    }

    private void saveNewNote(String date, String description) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());

        Note note = new Note();
        note.date = FormatNote.formatDate(date);
        try {
            note.description = FormatNote.formatDescription(description);
        } catch (IllegalFormatWidthException e) {
            finish();
        }

        // need to see if it is in edit or create
        // if its in create, add a new one
        // otherwise update
        // for the tags, update the tag list with the ones which haven't been added yet.
        // the tags link table have onConflict ignore so we don't need to worry
        if (!note.description.isEmpty() && note.date != 0) {
            int nid;
            if (noteid == -1) {
                nid = (int) db.noteDao().insertNote(note);
            } else {
                nid = noteid;
                note.nid = noteid;
                db.noteDao().update(note);
            }

            saveNoteTags(nid);  // add the note's tag in the link table
        }

        finish();
    }

    private void saveNoteTags(int nid) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        ArrayList<String> tagList = TagList.selectedTags();

        db.linkTableDao().removeLinksToNote(nid);
        for (String tag : tagList) {
            LinkTable lt = new LinkTable();
            lt.nid = nid;
            lt.tid = tag;
            db.linkTableDao().insertLink(lt);
        }
        TagList.clear();
    }

    @Override
    public void onBackPressed() {
        TagList.clear();
        finish();
    }

    void updateContent(TextView date, TextView description) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        Note note = db.noteDao().getNoteById(noteid);
        date.setText(FormatNote.formatDate(note.date));
        description.setText(note.description);
    }
}