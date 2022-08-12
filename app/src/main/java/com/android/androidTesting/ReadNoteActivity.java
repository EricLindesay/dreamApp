package com.android.androidTesting;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
        final EditText dateEV = findViewById(R.id.dateEV);
        final EditText descriptionEV = findViewById(R.id.descriptionEV);
        setClicks(dateEV, false);
        setClicks(descriptionEV, false);
        setFocuses(dateEV, false);
        setFocuses(descriptionEV, false);
        // get the note's id
        Bundle extras = getIntent().getExtras();
        int noteid = extras.getInt("noteid");
        // get the note
        final AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        final Note note =  db.noteDao().getNoteById(noteid);

        // print the note on the screen
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        dateEV.setText(df.format(new Date(note.date)));
        descriptionEV.setText(note.description);

        dateEV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Debugging", "owijfa");
                if (editable) {
                    Log.w("Debugging", "another oen");
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
                            ReadNoteActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // on below line we are setting date to our edit text.
                                    monthOfYear++;
                                    String monthOutput;
                                    if (monthOfYear < 10) monthOutput = "0" + monthOfYear;
                                    else monthOutput = "" + monthOfYear;

                                    String dayOutput;
                                    if (dayOfMonth < 10) dayOutput = "0" + dayOfMonth;
                                    else dayOutput = "" + dayOfMonth;
                                    dateEV.setText(year + "-" + monthOutput + "-" + dayOutput);
                                }
                            },
                            // on below line we are passing year,
                            // month and day for selected date in our date picker.
                            year, month, day);
                    // at last we are calling show to
                    // display our date picker dialog.
                    datePickerDialog.show();
            } else {
                    Log.w("Debugging", "calender");
                }
        }
        });

        final Button returnButton =  findViewById(R.id.returnButton);
        final ImageView editButton = findViewById(R.id.editButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editable) {  // we want to save
                    editable = false;
                    // Update the note
                    note.description = FormatNote.formatDescription(descriptionEV.getText().toString());
                    note.date = FormatNote.formatDate(dateEV.getText().toString());
                    db.noteDao().update(note);

                    // Reset the buttons
                    returnButton.setText("Return");
                    editButton.setVisibility(View.VISIBLE);

                    // Set the EditViews to unclickable and unfocusable
                    setClicks(dateEV, false);
                    setClicks(descriptionEV, false);
                    setFocuses(dateEV, false);
                    setFocuses(descriptionEV, false);

                    // Make it so the keyboard goes away
                    InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dateEV.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(descriptionEV.getWindowToken(), 0);
                } else {
                    finish();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editable = true;
                // Set the buttons
                editButton.setVisibility(View.GONE);
                returnButton.setText("Save");

                // Make the EVs unclickable and unfocusable
                setClicks(dateEV, true);
                setClicks(descriptionEV, true);
                setFocuses(descriptionEV, true);

                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dateEV.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(descriptionEV.getWindowToken(), 0);
            }
        });
    }

    void setFocuses(EditText et, boolean mode) {
        et.setFocusable(mode);
        et.setFocusableInTouchMode(mode);
    }

    void setClicks(EditText et, boolean mode) {
        et.setClickable(mode);
        et.setLongClickable(mode);
    }
}