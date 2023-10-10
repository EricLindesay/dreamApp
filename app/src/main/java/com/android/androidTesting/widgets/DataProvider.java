package com.android.androidTesting.widgets;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.androidTesting.R;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.utility.Format;

import java.util.ArrayList;
import java.util.List;

public class DataProvider implements RemoteViewsService.RemoteViewsFactory {

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
        Log.d("Widget", "Get view At ("+position+")");
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_row);

        // Set the text for date and description
        String date = Format.date(noteListView.get(position).date);
        String description = noteListView.get(position).description;

        view.setTextViewText(R.id.tv1, date);
        view.setTextViewText(R.id.tv2, Format.shortenString(description, 100));

        // When you click on the widget list, create an intent to tell it the note's position id.
//        Bundle extras = new Bundle();
//        extras.putInt(CollectionWidget.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
//        fillInIntent.putExtras(extras);
        fillInIntent.putExtra(CollectionWidget.EXTRA_ITEM, position);


        Log.d("DataProvider", "Position in intent "+fillInIntent.getIntExtra(CollectionWidget.EXTRA_ITEM, -1));
        view.setOnClickFillInIntent(R.id.widget_row, fillInIntent);

        return view;
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
        // initialise the data to display
        AppDatabase db = AppDatabase.getDbInstance(mContext.getApplicationContext());
        List<Note> noteList = db.noteDao().getAllNotes();
        noteListView = noteList;
    }
}
