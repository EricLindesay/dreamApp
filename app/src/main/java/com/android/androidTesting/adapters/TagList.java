package com.android.androidTesting.adapters;

import com.android.androidTesting.db.Tag;

import java.util.ArrayList;

public class TagList {
    static TagList instance = null;
    public ArrayList<Tag> tags;
    public ArrayList<Boolean> selected = new ArrayList<>();

    public static TagList getInstance() {
        // TagList is a singleton so implement the design pattern
        if (instance == null) {
            instance = new TagList();
        }
        return instance;
    }

    public void initialiseTagList(ArrayList<Tag> tagList) {
        // Set the tagList to be the arraylist of tags passed through
        tags = tagList;
        for (int i=0; i<tagList.size(); i++) {  // for a new note, they will all be false
            selected.add(false);
        }
    }

    public void initialiseTagList(ArrayList<Tag> tagList, ArrayList<String> noteTags) {
        // tagList - an arraylist containing all tags
        // noteTags - an arraylist containing all selected tags
        tags = tagList;
        for (int i=0; i<tagList.size(); i++) {  // for a new note, they will all be false
            if (noteTags.contains(tags.get(i).tid)) {
                selected.add(true);
            } else {
                selected.add(false);
            }
        }
    }

    public ArrayList<String> tagsAsString() {
        // Get the list of tags as a list of strings
        ArrayList<String> ret = new ArrayList<>();
        for (Tag tag : tags) {
            ret.add(tag.tid);
        }
        return ret;
    }

    public void addTag(Tag tag) {
        // Add the tag, sorted into the list.

        // Find the position it should occupy
        int pos;
        for (pos=0; pos<tags.size(); pos++) {
            Tag currentTag = tags.get(pos);
            if (currentTag.tid.compareTo(tag.tid) > 0) {  // currrentTag > tag
                break;
            }
        }

        // Add it into that position
        tags.add(pos, tag);
        selected.add(pos, true);    // if the user wants to create a new tag,
                                            // it should be default be true for that note
    }

    public void removeTag(Tag tag) {
        // Remove the passed through tag from the list of tags and from the selected list.
        int i;
        for (i=0; i<tags.size(); i++) {
            if (tags.get(i).tid.equals(tag.tid)) {
                break;
            }
        }
        tags.remove(i);
        selected.remove(i);
    }

    public boolean isSelected(Tag tag) {
        // Gets whether the tag is selected or not
        for (int i=0; i<tags.size(); i++) {
            if (tags.get(i).tid.equals(tag.tid)) {
                return selected.get(i);
            }
        }
        return false;
    }

    public boolean isSelected(String tag) {
        // Gets whether the string representation of the tag is selected or not
        for (int i=0; i<tags.size(); i++) {
            if (tags.get(i).tid.equals(tag)) {
                return selected.get(i);
            }
        }
        return false;
    }

    public void setSelected(Tag tag, boolean selected) {
        // Set the selected tag to be whatever the boolean passed through is.
        for (int i=0; i<tags.size(); i++) {
            if (tags.get(i).tid.equals(tag.tid)) {
                this.selected.set(i, selected);
                break;
            }
        }
    }

    public void clear() {
        // Clear the tags and selected array lists
        if (tags != null && !tags.isEmpty()) tags.clear();
        if (selected != null && !selected.isEmpty()) selected.clear();
    }

    public ArrayList<String> getSelected() {
        // Gets all of the selected tags as an array list of strings.
        ArrayList<String> ret = new ArrayList<>();
        for (int i=0; i<selected.size(); i++) {
            if (selected.get(i)) {
                ret.add(tags.get(i).tid);
            }
        }
        return ret;
    }

    public ArrayList<String> whichSelected(ArrayList<String> tags) {
        // Receives a list of strings representing tags and returns a new array list of strings.
        // This new array list contains which of the passed through tags have been selected.
        ArrayList<String> ret = new ArrayList<>();
        for (String tag : tags) {
            if (isSelected(tag)) {
                ret.add(tag);
            }
        }
        return ret;
    }
}
