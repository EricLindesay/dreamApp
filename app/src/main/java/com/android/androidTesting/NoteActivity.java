package com.android.androidTesting;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.androidTesting.adapters.TagList;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.LinkTable;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.db.Tag;
import com.android.androidTesting.utility.CalendarClass;
import com.android.androidTesting.utility.CreateDialogBox;
import com.android.androidTesting.utility.Format;

public class NoteActivity extends AppCompatActivity {
    TextView dateInput;
    TextView descriptionInput;
    int noteID;
    String originalDate = "";
    String originalDescription = "";
    ArrayList<String> originalTags = null;
    TagList tagList = TagList.getInstance();
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the display
        setContentView(R.layout.activity_note);

        // Initialise the date and description text views
        dateInput = findViewById(R.id.dateInput);
        descriptionInput = findViewById(R.id.descriptionInput);

        // Get the information about whether the note is new (-1) or not.
        Bundle extras = getIntent().getExtras();
        noteID = extras.getInt("noteID");
        if (noteID != -1) {
            updateContent(dateInput, descriptionInput);
            setOriginalValues();
        }
        loadTagList();

        // Initialise the buttons and stuff
        initialiseBackButton();
        initialiseDateInput();
        initialiseTodayButton();
        initialiseSaveButton();
        initialiseTagButton();
        initialiseCopyButton();
    }

    void setOriginalValues() {
        // Sets the original values so you know if something has changed or not.
        originalDate = dateInput.getText().toString();
        originalDescription = descriptionInput.getText().toString();
        originalTags = getTags();
    }

    void initialiseBackButton() {
        // When back button is pressed, finish the current task. Exit without saving
        ImageView backButton = findViewById(R.id.toolbarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitWithoutSaving();
            }
        });
    }

    void initialiseDateInput() {
        // When pressed, show the calendar pop up to select a date
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteID == -1) {
                    new CalendarClass(NoteActivity.this, dateInput);
                } else {
                    String date = dateInput.getText().toString();
                    new CalendarClass(NoteActivity.this, dateInput, date);
                }
            }
        });
    }

    void initialiseTodayButton() {
        // When pressed, set the value of the dateInput to be today's date
        Button todayButton = findViewById(R.id.todayButton);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();  // get current date
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                dateInput.setText(df.format(now));
            }
        });
    }

    void initialiseSaveButton() {
        // When pressed, save the note
        Button saveButton = findViewById(R.id.saveButton);
//        ImageView saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewNote(dateInput.getText().toString(), descriptionInput.getText().toString());
            }
        });
    }

    void initialiseTagButton() {
        // When pressed, open the tag list
        Button tagButton = findViewById(R.id.tagsButton);
//        ImageView tagButton = findViewById(R.id.tagsButton);
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTagList();
            }
        });
    }

    void initialiseCopyButton() {
        // When pressed, copy the data in DescriptionInput into clipboard
        ImageView copyButton = findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Dream", descriptionInput.getText().toString());
                clipboard.setPrimaryClip(clip);
//                Snackbar.make(view, "Text Copied!", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
                Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTagList() {
        // Load the list of tags into tagList
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Tag> dbTagList = db.tagDao().getAllTags();
        ArrayList<Tag> tags = new ArrayList<>(dbTagList);
        if (noteID == -1) {
            tagList.initialiseTagList(tags);
        } else {
            List<String> tagsForNote = db.linkTableDao().getAllTagsForNote(noteID);
            tagList.initialiseTagList(tags, new ArrayList<>(tagsForNote));
        }
    }

    private void openTagList() {
        // Starts the tags activity, opening the list of tags for the user.
        Intent intent = new Intent(NoteActivity.this, TagsActivity.class);
        intent.putExtra("noteID", noteID);
        startActivityForResult(intent, 100);
    }

    private void saveNewNote(String date, String description) {
        // Saves a note
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());

        // Create a note, setting the required data
        Note note = new Note();
        note.date = Format.date(date);
        note.description = Format.description(description);

        // need to see if it is in edit or create
        // if its in create, add a new one
        // otherwise update
        // for the tags, update the tag list with the ones which haven't been added yet.
        // the tags link table have onConflict ignore so we don't need to worry
        if (!note.description.isEmpty() && !date.isEmpty()) {
            int nid;
            if (noteID == -1) {  // note id = -1 so create a new one
                nid = (int) db.noteDao().insertNote(note);
            } else {
                // edit the existing tag
                nid = noteID;
                note.nid = noteID;
                db.noteDao().update(note);
            }

            saveNoteTags(nid);  // add the note's tag in the link table
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            exit();
        } else {
            // couldn't save correctly. Warn them that one of the fields is empty
            exitWarnOneEmpty();
        }
    }

    private void saveNoteTags(int nid) {
        // Save the tags to the note in the link table
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        ArrayList<String> tagList = this.tagList.getSelected();

        // remove all the tags currently assigned to the note
        db.linkTableDao().deleteLinksToNote(nid);
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
        // Make sure that something has changed. If nothing has changed, just exit.
        // If something has changed, then pop up a box asking them whether they are sure.
        if (hasChanged(dateInput, originalDate) || hasChanged(descriptionInput, originalDescription) || hasChanged(originalTags)) {
            final DialogInterface.OnClickListener dialogClickListener = CreateDialogBox.create(() -> {exit(); return null;}, () -> {return null;} );
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit without saving?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } else {
            exit();
        }
    }

    void exitWarnOneEmpty() {
        // The user has pressed the save button.
        if (dateInput.getText().toString().isEmpty() && descriptionInput.getText().toString().isEmpty()) {
            // If both fields are empty, just exit to main menu.
            exit();
            return;
        }
        // Otherwise, see which one is empty and warn the user that it is empty.
        // Tell the user it will not be saved
        String comment = "";
        if (dateInput.getText().toString().isEmpty()) {
            comment = "Date";
        } else {
            comment = "Description";
        }

        // Do the dialogue pop up
        final DialogInterface.OnClickListener dialogClickListener = CreateDialogBox.create(() -> {exit(); return null;}, () -> {return null;} );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(comment+" is empty. Are you sure you want to continue? This will not save").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show();
    }

    boolean hasChanged(TextView one, String two) {
        return !one.getText().toString().equals(two);
    }

    boolean hasChanged(ArrayList<String> tagList) {
        // see if the tag list has changed.
        // If it is a new note, tagList is null so just return whether the user added any.
        if (tagList == null) {
            ArrayList<String> selectedTags = this.tagList.getSelected();
            return !selectedTags.isEmpty();
        }

        // Otherwise we need to see if the previous tags are the same as the current.
        // So get all of the new tags.
        ArrayList<String> newTags = this.tagList.tagsAsString();
        ArrayList<Boolean> selected = this.tagList.selected;

        // If the user creates a new tag but doesn't select it, don't ask for without saving.
        // If they create a tag AND select it, then do ask.
        if (newTagSelected(newTags)) {
            return true;
        }

        // check that each tag in tagList is in the newTags
        // also make sure that the tag is not deleted. If it is deleted, then just skip over, assume no change
        for (String tag : tagList) {
            if (newTags.contains(tag)) {
                int i = newTags.indexOf(tag);
                if (!selected.get(i)) {  // if this one is not selected but it should be, a change occurred
                    return true;
                }
            }
        }
        return false;  // no changes have occurred
    }

    boolean newTagSelected(ArrayList<String> newTags) {
        newTags = tagList.whichSelected(newTags);

        Collections.sort(newTags);
        Collections.sort(originalTags);

        if (newTags == null || originalTags == null) {
            return false;
        }

        // go through both of them. Find the differences between the two lists.
        // Then go through these differences and see if they have been selected.
        // If they have then return true. Otherwise return false.
        ArrayList<String> differences = new ArrayList<>();

        int i=0, j=0;
        while (i < newTags.size() && j < originalTags.size()) {
            if (stringsEqual(newTags.get(i), originalTags.get(j))) {
                i++;
                j++;
            } else if (string1Greater(newTags.get(i), originalTags.get(j))) {
                differences.add(originalTags.get(j));
                j++;
            } else if (string2Greater(newTags.get(i), originalTags.get(j))) {
                differences.add(newTags.get(i));
                i++;
            }
        }

        for (int k=i; k<newTags.size(); k++) {
            differences.add(newTags.get(k));
        }
        for (int k=j; k<originalTags.size(); k++) {
            differences.add(originalTags.get(k));
        }

        for (String str : differences) {
            if (tagList.isSelected(str)) {
                return true;
            }
        }

        return false;
    }

    boolean stringsEqual(String str1, String str2) { return str1.compareTo(str2) == 0; }
    boolean string1Greater(String str1, String str2) { return str1.compareTo(str2) > 0; }
    boolean string2Greater(String str1, String str2) { return str1.compareTo(str2) < 0; }

    void exit() {
        // Exit the program, we need to clear the TagList object and finish the activity.
        this.tagList.clear();
        finish();
    }

    void updateContent(TextView date, TextView description) {
        // The note is not an original one, you are editing an old one.
        // Get the note and set the default date and description of the inputFields to be
        // the values in the database.
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        Note note = db.noteDao().getNoteById(noteID);
        date.setText(Format.date(note.date));
        description.setText(note.description);
    }

    private ArrayList<String> getTags() {
        // Get a list of the names of the tags.
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        return new ArrayList<>(db.linkTableDao().getAllTagsForNote(noteID));
    }
}