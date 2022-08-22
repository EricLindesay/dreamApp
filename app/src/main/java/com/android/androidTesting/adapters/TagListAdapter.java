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
                allTags.setSelected(thisTag, isChecked);
            }
        });

        // get occurrences of tag
        AppDatabase db = AppDatabase.getDbInstance(context);
        final int occurrences = db.linkTableDao().tagOccurrences(thisTag.tid);

        holder.usesText.setText("uses: "+occurrences);

        final DialogInterface.OnClickListener dialogClickListener = CreateDialogBox.create(() -> {addTagsActivity.deleteTag(thisTag); addTagsActivity.refreshTagList(); return null;}, () -> {return null;} );
        holder.deleteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete \""+thisTag.tid+"\"?\nIt is used in "+occurrences+" notes").setPositiveButton("Yes", dialogClickListener)
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
