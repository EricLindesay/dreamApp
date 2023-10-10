package com.android.androidTesting.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.androidTesting.MainActivity;
import com.android.androidTesting.R;
import com.android.androidTesting.SearchActivity;
import com.android.androidTesting.ShowsNotes;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.utility.CreateDialogBox;
import com.android.androidTesting.utility.Format;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {

    private Context context;
    private MainActivity main;
    private SearchActivity search;
    private List<Note> noteList;
    private ShowsNotes activity;

    // The note list adapter is used both in the main menu and search menu so both of them implement
    // the ShowsNotes interface which defines what to do when the note is clicked or deleted.
    public NoteListAdapter(Context context, ShowsNotes activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_menu_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // This runs for each row when it is created.

        // Get the note it should load
        final Note note = this.noteList.get(position);

        // Display the correct date and description for this row so it reflects the note it is attached to.
        holder.tvDate.setText(Format.date(note.date));
        holder.tvDescription.setText(Format.shortenString(note.description, 200));

        // Get the tags to display as italics underneath the note.
        String tagsString = getTagsString(note);
        if (tagsString.isEmpty()) {  // if there are no tags, don't display anything
            holder.tvTagList.setVisibility(View.GONE);
        } else {
            holder.tvTagList.setVisibility(View.VISIBLE);
        }
        holder.tvTagList.setText(tagsString);  // display the tags

        // When you click on the row, run the 'clickedNote' function for the correct activity.
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickedNote(note);
            }
        });

        initDeleteButton(holder, note);
    }

    String getTagsString(Note note) {
        // Underneath the note you want to display a list of notes.
        // However, you don't want to show all of them if there are too many, so limit the number
        // of characters.
        AppDatabase db = AppDatabase.getDbInstance(this.context);
        List<String> tagList = db.linkTableDao().getAllTagsForNote(note.nid);  // get the tags for this note
        if (tagList.size() <= 0) {
            return "";
        } else {
            String tagsString = String.join(", ", tagList);  // format them, separated by ", "
            return Format.shortenString(tagsString, 40);  // shorten the string
        }
    }

    void initDeleteButton(MyViewHolder holder, Note note) {
        // Initialise the delete note button.
        // When the button is pressed, run the ShowsNote.deleteNote function for the ShowsNote object.
        final DialogInterface.OnClickListener dialogClickListener = CreateDialogBox.create(() -> {activity.deleteNote(note); return null;}, () -> {return null;} );
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete note "+ Format.date(note.date)+"?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return  this.noteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate;
        TextView tvDescription;
        ImageView deleteButton;
        ConstraintLayout row;
        TextView tvTagList;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.dateInput);
            tvDescription = view.findViewById(R.id.descriptionInput);
            deleteButton = view.findViewById(R.id.deleteButton);
            row = view.findViewById(R.id.noteRow);
            tvTagList = view.findViewById(R.id.tags);
        }
    }
}
