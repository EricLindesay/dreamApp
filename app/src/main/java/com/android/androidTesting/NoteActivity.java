package com.android.androidTesting;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.androidTesting.adapters.TagList;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.LinkTable;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.db.Tag;
import com.android.androidTesting.utility.CalendarClass;
import com.android.androidTesting.utility.CreateDialogBox;
import com.android.androidTesting.utility.FormatNote;

public class NoteActivity extends AppCompatActivity {
    TextView dateInput;
    TextView descriptionInput;
    int noteid;
    String originalDate = "";
    String originalDescription = "";
    boolean changedTags = false;
    ArrayList<String> originalTags = null;
    TagList allTagList = new TagList();
    // if they open the tag list, just say they changed tags

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        dateInput =  findViewById(R.id.dateInput);
        descriptionInput =  findViewById(R.id.descriptionInput);

        Bundle extras = getIntent().getExtras();
        noteid = extras.getInt("noteid");
        if (noteid != -1) {
            updateContent(dateInput, descriptionInput);
            originalDate = dateInput.getText().toString();
            originalDescription = descriptionInput.getText().toString();
            originalTags = getTags();
        }
        loadTagList();

        ImageView backButton = findViewById(R.id.toolbarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitWithoutSaving();
            }
        });

        // on below line we are adding click listener
        // for our pick date button
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CalendarClass(NoteActivity.this, dateInput);
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

        Button saveButton = findViewById(R.id.saveButton);
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
                changedTags = true;
                openTagList();
            }
        });
    }

    private void loadTagList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Tag> tagList = db.tagDao().getAllTags();
        ArrayList<Tag> tags = new ArrayList<Tag>(tagList);
        if (noteid == -1) {
            allTagList.initialiseTagList(tags);
        }
        if (noteid != -1) {
            List<String> stringList = db.linkTableDao().getAllTagsForNote(noteid);
            allTagList.initialiseTagList(tags, new ArrayList<String>(stringList));
        }
    }

    private void openTagList() {
        Intent intent = new Intent(NoteActivity.this, TagsActivity.class);
        intent.putExtra("noteid", noteid);
        startActivityForResult(intent, 100);
    }

    private void saveNewNote(String date, String description) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        boolean date_is_empty = date.isEmpty();  // an empty date defaults to today so we need to manually check.

        Note note = new Note();
        note.date = FormatNote.formatDate(date);
        note.description = FormatNote.formatDescription(description);

        // need to see if it is in edit or create
        // if its in create, add a new one
        // otherwise update
        // for the tags, update the tag list with the ones which haven't been added yet.
        // the tags link table have onConflict ignore so we don't need to worry
        if (!note.description.isEmpty() && !date_is_empty) {
            int nid;
            if (noteid == -1) {
                nid = (int) db.noteDao().insertNote(note);
            } else {
                nid = noteid;
                note.nid = noteid;
                db.noteDao().update(note);
            }

            saveNoteTags(nid);  // add the note's tag in the link table
            exit();
        } else {
            // couldn't save correctly
            exit();
        }
    }

    private void saveNoteTags(int nid) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        ArrayList<String> tagList = TagList.selectedTags();

        db.linkTableDao().deleteLinksToNote(nid);  // remove all the tags currently assigned to the note
        // and re assign all of them again
        // Better to see the differences and only add those but it doesn't matter
        for (String tag : tagList) {
            LinkTable lt = new LinkTable();
            lt.nid = nid;
            lt.tid = tag;
            db.linkTableDao().insertLink(lt);
        }
    }

    @Override
    public void onBackPressed() {
        exitWithoutSaving();
    }

    void exitWithoutSaving() {
        if (hasChanged(dateInput, originalDate) || hasChanged(descriptionInput, originalDescription) || hasChanged(originalTags)) {
            final DialogInterface.OnClickListener dialogClickListener = CreateDialogBox.create(() -> {exit(); return null;}, () -> {return null;} );
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit without saving?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } else {
            exit();
        }
    }

    boolean hasChanged(TextView one, String two) {
        return !one.getText().toString().equals(two);
    }

    boolean hasChanged(ArrayList<String> tagList) {
        // if taglist == null adn there are things in the new one, then we must save
        if (tagList == null) {
            ArrayList<String> selectedTags = TagList.selectedTags();
            return !selectedTags.isEmpty();
        }
        // load the tag list then do this.
        ArrayList<String> newTags = TagList.tagsAsString();
        ArrayList<Boolean> selected = TagList.selected;
        Log.d("Eric", "old; "+tagList.toString());
        Log.d("Eric", "new; "+newTags.toString());
        // check that each tag in tagList is in newTags
        // also make sure that the tag is not deleted. If it is deleted, then just skip over, assume no change
        for (String tag : tagList) {
            if (newTags.contains(tag)) {
                int i = newTags.indexOf(tag);
                if (!selected.get(i)) {
                    return true;
                }
            }
        }

        return false;
    }

    void exit() {
        TagList.clear();
        finish();
    }

    void updateContent(TextView date, TextView description) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        Note note = db.noteDao().getNoteById(noteid);
        date.setText(FormatNote.formatDate(note.date));
        description.setText(note.description);
    }

    private ArrayList<String> getTags() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        return new ArrayList<>(db.linkTableDao().getAllTagsForNote(noteid));
    }
}