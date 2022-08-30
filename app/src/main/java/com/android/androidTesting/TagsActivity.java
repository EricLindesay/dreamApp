package com.android.androidTesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    TagList tagList = TagList.getInstance();
    EditText searchBar;
    TextView addTagTV;
    int noteID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_tags_menu);

        // You need to get the note so that you can see which tags have been selected (if you are
        // editing the note).
        Bundle extras = getIntent().getExtras();
        noteID = extras.getInt("noteID");

        tags = tagList.tags;  // default the tags to show all of them.

        initialiseBackButton();
        initialiseSearchBar();
        initialiseAddTag();
        initRecyclerView();

        // The 'add tag' button shouldn't always be shown. So see if it should be shown or not.
        changeAddTagVisibility();
    }

    void initialiseBackButton() {
        // Initialise the back button. When you click on it, end the activity.
        ImageView backButton = findViewById(R.id.toolbarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backPress();
            }
        });
    }

    void initialiseSearchBar() {
        // When the search bar is edited, we want to change what tags are shown to reflect what was
        // searched for. We also need to see if we should add the 'add tag' button.
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

        // When the user clicks Enter button, it automatically creates that tag
        TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    addTag();
                }
                return true;
            }
        };
        searchBar.setOnEditorActionListener(exampleListener);

    }

    void initialiseAddTag() {
        // When the user clicks on the add tag button, if the search bar is empty then don't do anything
        // we don't want to create an empty tag because that will never need to be used.
        // Otherwise, you need to create the tag and refresh the tag list to show the new one.

        addTagTV = findViewById(R.id.addTag);
        addTagTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBar.getText().toString().isEmpty()) {
                    // don't create an empty tag
                    // could display an error message/toast
                } else {
                    // create new tag and reload the tag list.
                    addTag();
                }
            }
        });
    }

    void addTag() {
        // Add the tag in the search bar
        String tag = searchBar.getText().toString().trim();
        if (tag.isEmpty()) {
            Toast.makeText(this, "Cannot save tag containing only white space", Toast.LENGTH_SHORT).show();
        } else if (createTag(tag)) {
            loadTagList(tag);
            changeAddTagVisibility();
            Toast.makeText(this, "Tag '" + tag + "' added", Toast.LENGTH_SHORT).show();
        }
    }

    void changeAddTagVisibility() {
        // We only want to show the 'add tag' button on certain conditions.
        // If no tags exist, then we want to show the add tag button.
        // If there is something in the search bar we also want to show it because the user may want
        // to create a tag with that name.
        // If the search bar is empty we also want to show it because it tells the user they can add
        // tags in this menu.
        // Otherwise, we want to hide the add tag button (if the user already has added tags and
        // the search bar is empty).
        if (tags.size() <= 0 || !searchBar.getText().toString().isEmpty()) {
            addTagTV.setVisibility(View.VISIBLE);
            addTagTV.setHint("+ Add tag: \'"+searchBar.getText().toString()+"\'");
        } else {
            addTagTV.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView() {
        // Initialise the recyclerView, showing the notes as a list
        recyclerView = findViewById(R.id.tagList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        tagListAdapter = new TagListAdapter(tags, this, tagList, this);
        recyclerView.setAdapter(tagListAdapter);
    }

    private void loadTagList(String tagName) {
        // We want to load/reload the tag list.
        // If there is thing in the search bar then load any tags which contain that phrase.
        // Otherwise, load all of the tags.
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Tag> tagList;
        if (tagName.isEmpty()) tagList = db.tagDao().getAllTags();
        else tagList = db.tagDao().getTagsByName("%"+tagName+"%");
        tags = new ArrayList<>(tagList);
        tagListAdapter = new TagListAdapter(tags, this, this.tagList, this);
        recyclerView.setAdapter(tagListAdapter);
    }

    boolean createTag(String tagName) {
        // This is called when the user clicks the 'add tag' button.
        // Create a new tag object
        Tag tag = new Tag();
        tag.tid = tagName;
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());

        // See if already in database
        Tag exists = db.tagDao().getTagById(tagName);
        if (exists != null) {
            // If it does then return false, tag not created
            return false;
        }

        // insert it into the database
        db.tagDao().insert(tag);

        // Add the tag to the list of tags
        tagList.addTag(tag);
        return true;
    }

    public void deleteTag(Tag tag) {
        // This gets called from the tag list adapter.
        // If the user clicks the delete button on the tag row then delete any links that this tag
        // has to any other note.
        // Then delete the tag itself.
        // Then remove the tag from the local list of tags. (local as in not in database).
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        db.linkTableDao().deleteLinksToTag(tag.tid);
        db.tagDao().delete(tag);
        tagList.removeTag(tag);
    }

    public void refreshTagList() {
        // Refresh the tag list.
        // Load all the tags with whatever is searched for in the search bar.
        // Check whether you should load the 'add tag' button.
        loadTagList(searchBar.getText().toString());
        changeAddTagVisibility();
    }

    @Override
    public void onBackPressed() {
        // if there is stuff in the search box, clear it. Otherwise, finish.
        // This is because it makes more sense to me for the search bar to be cleared on first back
        // press before you exit the program.
        backPress();
    }

    void backPress() {
        // When you press one of the buttons to get back to the main menu this is called
        if (searchBar.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tags saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            searchBar.setText("");  // empty the search bar.
        }
    }
}