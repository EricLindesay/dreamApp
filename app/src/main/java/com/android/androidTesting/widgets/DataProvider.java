package com.android.androidTesting.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.text1;
import static android.R.layout.simple_list_item_1;
import static android.R.id.text2;
import static android.R.layout.simple_list_item_2;
//import android.R.layout.widget_row;

import com.android.androidTesting.MainActivity;
import com.android.androidTesting.R;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.utility.FormatNote;

public class DataProvider implements RemoteViewsService.RemoteViewsFactory {

    //    List<String> myListView = new ArrayList<>();
    Context mContext = null;
    List<Note> noteListView = new ArrayList<>();

    public DataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {  }

    @Override
    public int getCount() {
        return noteListView.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_row);
        String date = FormatNote.formatDate(noteListView.get(position).date);
        String description = noteListView.get(position).description;

        view.setTextViewText(R.id.tv1, date);
        view.setTextViewText(R.id.tv2, shortenDescription(description, 100));

        Bundle extras = new Bundle();
        extras.putInt(CollectionWidget.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        view.setOnClickFillInIntent(R.id.widget_row, fillInIntent);

        return view;
    }

    String shortenDescription(String desc, int charLimit) {
        // Go through each word in the string, if adding the new word increases the length over the
        // limit, then stop and add a "..." Otherwise just show the whole word.
        // If the first word exceeds the character limit, then print "Word too long to display".
        String ret = "";
        String[] words = desc.split(" ");  // get each word.
        for (String word : words) {
            if (ret.length()+word.length() > charLimit) {
                ret = ret.trim() + "...";
            } else {
                ret = ret + word + " ";
            }
        }
        ret = ret.trim();
        if (ret.isEmpty() || ret.equals("..."))
            return "Character limit exceeded";

        return ret;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        AppDatabase db = AppDatabase.getDbInstance(mContext.getApplicationContext());
        List<Note> noteList = db.noteDao().getAllNotes();
        noteListView = noteList;
    }
}
