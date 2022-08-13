package com.android.androidTesting.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import com.android.androidTesting.AddTagsActivity;
import com.android.androidTesting.MainActivity;
import com.android.androidTesting.R;
import com.android.androidTesting.db.Tag;

import java.util.ArrayList;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.MyViewHolder> {

    private Context context;
    private AddTagsActivity addTagsActivity;
    private ArrayList<Tag> tagList;
    private TagList allTags;

    public TagListAdapter(ArrayList<Tag> tagList, Context context, TagList allTags, AddTagsActivity addTagsActivity) {
        this.context = context;
        this.allTags = allTags;
        this.tagList = tagList;
        this.addTagsActivity = addTagsActivity;
    }

    public void setTagList(ArrayList<Tag> tagList) {
        this.tagList = tagList;
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagListAdapter.MyViewHolder holder, int position) {
        final Tag thisTag = this.tagList.get(position);
        holder.tagName.setText(thisTag.tid);

        holder.tagName.setOnCheckedChangeListener(null);
        holder.tagName.setChecked(allTags.isSelected(thisTag));
        holder.tagName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                Log.d("Recycle", "a button was clicked");
                allTags.setSelected(thisTag, isChecked);
            }
        });

        holder.deleteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTagsActivity.deleteTag(thisTag);
                addTagsActivity.refreshTagList();
            }
        });
    }

    @Override
    public int getItemCount() {
        return  this.tagList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox tagName;
        ImageView deleteTag;

        public MyViewHolder(View view) {
            super(view);
            //this.setIsRecyclable(false);
            tagName = view.findViewById(R.id.checkBox);
            deleteTag = view.findViewById(R.id.deleteTag);
        }
    }
}
