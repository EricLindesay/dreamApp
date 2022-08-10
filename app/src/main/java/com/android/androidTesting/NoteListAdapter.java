package com.android.androidTesting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.androidTesting.db.Note;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {

    private Context context;
    private List<Note> noteList;
    public NoteListAdapter(Context context) {
        this.context = context;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.recycler_row, parent, false);

       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.MyViewHolder holder, int position) {
        holder.tvDate.setText(this.noteList.get(position).date);
        holder.tvDescription.setText(this.noteList.get(position).description);
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
