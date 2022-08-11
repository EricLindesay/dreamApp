package com.android.androidTesting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import com.android.androidTesting.db.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {

    private Context context;
    private MainActivity main;
    private List<Note> noteList;

    public NoteListAdapter(Context context, MainActivity main) {
        this.context = context;
        this.main = main;
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

    public String millis_to_string(long millis) {
        Date date = new Date(millis);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.MyViewHolder holder, int position) {
        holder.tvDate.setText(millis_to_string(this.noteList.get(position).date));
        holder.tvDescription.setText(this.noteList.get(position).description);

        final int noteid = this.noteList.get(position).nid;
        holder.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.clickedNote(noteid);
            }
        });
        holder.tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.clickedNote(noteid);
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

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.dateInput);
            tvDescription = view.findViewById(R.id.descriptionInput);
        }
    }
}
