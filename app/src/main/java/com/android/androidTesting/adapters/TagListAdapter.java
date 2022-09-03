package com.android.androidTesting.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import com.android.androidTesting.TagsActivity;
import com.android.androidTesting.R;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Tag;
import com.android.androidTesting.utility.CreateDialogBox;

import java.util.ArrayList;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.MyViewHolder> {

    private Context context;
    private TagsActivity addTagsActivity;
    private ArrayList<Tag> tagList;
    private TagList allTags;

    public TagListAdapter(ArrayList<Tag> tagList, Context context, TagList allTags, TagsActivity addTagsActivity) {
        this.context = context;
        this.allTags = allTags;
        this.tagList = tagList;
        this.addTagsActivity = addTagsActivity;
    }

//    public void setTagList(ArrayList<Tag> tagList) {
//        this.tagList = tagList;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public TagListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagListAdapter.MyViewHolder holder, int position) {
        // This is run for each row of the tag list.
        final Tag tag = this.tagList.get(position);
        holder.tagName.setText(tag.tid);

        recordTagSelected(holder, tag);

        // get occurrences of tag
        int occurrences = getTagOccurrences(holder, tag);
        holder.usesText.setText("uses: "+occurrences);

        initDeleteTag(holder, tag, occurrences);
    }

    void recordTagSelected(MyViewHolder holder, Tag tag) {
        // This allows the TagList to store which tags are selected or not.
        // It also allows the default to be set. For example, if you load a note with tags 'one' and 'two'
        // both of those tags will be checked because of this function.
        holder.tagName.setOnCheckedChangeListener(null);
        holder.tagName.setChecked(allTags.isSelected(tag));
        holder.tagName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                allTags.setSelected(tag, isChecked);
            }
        });
    }

    int getTagOccurrences(MyViewHolder holder, Tag tag) {
        // Displays how many times each note occurs.
        AppDatabase db = AppDatabase.getDbInstance(context);
        final int occurrences = db.linkTableDao().tagOccurrences(tag.tid);
        return occurrences;
    }

    void initDeleteTag(MyViewHolder holder, Tag tag, int occurrences) {
        final DialogInterface.OnClickListener dialogClickListener = CreateDialogBox.create(() -> {addTagsActivity.deleteTag(tag); addTagsActivity.refreshTagList(); return null;}, () -> {return null;} );
        holder.deleteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete \""+tag.tid+"\"?\nIt is used in "+occurrences+" notes").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
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
        TextView usesText;

        public MyViewHolder(View view) {
            super(view);
            tagName = view.findViewById(R.id.checkBox);
            deleteTag = view.findViewById(R.id.deleteTag);
            usesText = view.findViewById(R.id.usesText);
        }
    }
}
