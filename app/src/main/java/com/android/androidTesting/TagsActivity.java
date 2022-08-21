package com.android.androidTesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.androidTesting.adapters.TagList;
import com.android.androidTesting.adapters.TagListAdapter;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagsActivity extends AppCompatActivity {
    private TagListAdapter tagListAdapter;
    private ArrayList<Tag> tags;
    RecyclerView recyclerView;
    TagList allTagList = new TagList();
    EditText searchBar;
    TextView addTagTV;
    int noteID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_tags_menu);

        ImageView backButton = findViewById(R.id.toolbarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        noteID = extras.getInt("noteID");

        searchBar = findViewById(R.id.searchTag);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadTagList(searchBar.getText().toString());
                changeAddTagVisibility();
            }
        });

        tags = TagList.allTags;
        initRecyclerView();

        addTagTV = findViewById(R.id.addTag);  // starts as "GONE
        addTagTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBar.getText().toString().isEmpty()) {
                    // don't create an empty tag
                    // display an error message
                } else {
                    // create new tag and add it
                    createTag(searchBar.getText().toString());
                    loadTagList(searchBar.getText().toString());
                    changeAddTagVisibility();
                }
            }
        });
        changeAddTagVisibility();
    }

    void changeAddTagVisibility() {
        if (tags.size() <= 0 || !searchBar.getText().toString().isEmpty()) {
            addTagTV.setVisibility(View.VISIBLE);
            //recyclerView.setVisibility(View.GONE);
        } else {
            addTagTV.setVisibility(View.GONE);
            //recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.tagList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        tagListAdapter = new TagListAdapter(tags, this, allTagList, this);
        recyclerView.setAdapter(tagListAdapter);
    }

    private void loadTagList(String tagname) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Tag> tagList;
        if (tagname.isEmpty()) tagList = db.tagDao().getAllTags();
        else tagList = db.tagDao().getTagsByName("%"+tagname+"%");
        tags = new ArrayList<Tag>(tagList);
        tagListAdapter = new TagListAdapter(tags, this, allTagList, this);
        recyclerView.setAdapter(tagListAdapter);
    }

    private void createTag(String tagName) {
        Tag tag = new Tag();
        tag.tid = tagName;
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());

        db.tagDao().insertTag(tag);

        // Add the tag to the list of tags
        allTagList.addTag(tag);
    }

    public void deleteTag(Tag tag) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        db.linkTableDao().deleteLinksToTag(tag.tid);
        db.tagDao().delete(tag);
        allTagList.removeTag(tag);
    }

    public void refreshTagList() {
        loadTagList(searchBar.getText().toString());
        changeAddTagVisibility();
    }

    @Override
    public void onBackPressed() {
        // if there is stuff in the search box, clear it.
        // otherwise, finish.
        String contents = searchBar.getText().toString();
        if (contents.isEmpty()) {
            finish();
        } else {
            searchBar.setText("");
        }
    }
}