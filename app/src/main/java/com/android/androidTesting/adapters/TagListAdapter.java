package com.android.androidTesting.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import com.android.androidTesting.MainActivity;
import com.android.androidTesting.R;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.db.Tag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.MyViewHolder> {

    private Context context;
    private MainActivity main;
    private List<Tag> tagList;

    public TagListAdapter(Context context) {
        this.context = context;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagListAdapter.MyViewHolder holder, int position) {
        Tag thisTag = this.tagList.get(position);
        holder.tagName.setText(thisTag.tid);
    }

    @Override
    public int getItemCount() {
        return  this.tagList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox tagName;

        public MyViewHolder(View view) {
            super(view);
            tagName = view.findViewById(R.id.checkBox);
        }
    }
}
