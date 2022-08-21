package com.android.androidTesting.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import com.android.androidTesting.MainActivity;
import com.android.androidTesting.R;
import com.android.androidTesting.SearchActivity;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.db.Tag;
import com.android.androidTesting.utility.CreateDialogBox;
import com.android.androidTesting.utility.FormatNote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {

    private Context context;
    private MainActivity main;
    private SearchActivity search;
    private List<Note> noteList;

    public NoteListAdapter(Context context, MainActivity main) {
        this.context = context;
        this.main = main;
    }

    public NoteListAdapter(Context context, SearchActivity search) {
        this.context = context;
        this.search = search;
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
    public void onBindViewHolder(@NonNull NoteListAdapter.MyViewHolder holder, int position) {
        final Note note = this.noteList.get(position);
        holder.tvDate.setText(FormatNote.formatDate(note.date));
        holder.tvDescription.setText(note.description);

        AppDatabase db = AppDatabase.getDbInstance(this.context);
        List<String> tagList = db.linkTableDao().getAllTagsForNote(note.nid);
        StringBuilder tagsString = new StringBuilder("tags: ");
        int tagsBeforeDots = 6;
        for (int i=0; i<tagsBeforeDots && i<tagList.size(); i++) {
            String tag = tagList.get(i);
            tagsString.append(tag+", ");
        }
        tagsString.deleteCharAt(tagsString.length()-1); // remove trailing ', '
        tagsString.deleteCharAt(tagsString.length()-1);
        if (tagList.size() > tagsBeforeDots) {
            tagsString.append("...");
        }
        holder.tvTagList.setText(tagsString.toString());

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (main != null) {
                    main.clickedNote(note.nid);
                } else if (search != null) {
                    search.clickedNote(note.nid);
                }
            }
        });

        final DialogInterface.OnClickListener dialogClickListener = CreateDialogBox.create(() -> {main.deleteNote(note); return null;}, () -> {return null;} );
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete note "+ FormatNote.formatDate(note.date)+"?").setPositiveButton("Yes", dialogClickListener)
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
