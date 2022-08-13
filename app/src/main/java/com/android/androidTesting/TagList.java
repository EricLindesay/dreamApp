package com.android.androidTesting;

import android.util.Log;

import com.android.androidTesting.db.Tag;

import java.util.ArrayList;

public class TagList {
    static ArrayList<Tag> allTags;
    static ArrayList<Boolean> selected = new ArrayList<>();

    public static void initialiseTagList(ArrayList<Tag> tagList) {
        allTags = tagList;
        for (int i=0; i<tagList.size(); i++) {  // for a new note, they will all be false
            selected.add(false);
        }
    }

    public static void initialiseTagList(ArrayList<Tag> tagList, ArrayList<String> noteTags) {
        allTags = tagList;
        for (int i=0; i<tagList.size(); i++) {  // for a new note, they will all be false
            if (noteTags.contains(allTags.get(i).tid)) {
                selected.add(true);
            } else {
                selected.add(false);
            }
        }
    }

    public void addTag(Tag tag) {
        allTags.add(tag);
        selected.add(true);  // if the user wants to create a new tag,
                             // it should be default be true for that note
    }

    public boolean isSelected(Tag tag) {
        for (int i=0; i<allTags.size(); i++) {
            if (allTags.get(i).tid.equals(tag.tid)) {
                return selected.get(i);
            }
        }
        return false;
    }

    public void setSelected(Tag tag, boolean selected) {
        for (int i=0; i<allTags.size(); i++) {
            if (allTags.get(i).tid.equals(tag.tid)) {
                this.selected.set(i, selected);
                break;
            }
        }
    }

    public static void clear() {
        allTags.clear();
        selected.clear();
    }

    public static ArrayList<String> selectedTags() {
        ArrayList<String> ret = new ArrayList<>();
        for (int i=0; i<selected.size(); i++) {
            if (selected.get(i)) {
                ret.add(allTags.get(i).tid);
            }
        }
        return ret;
    }
}
