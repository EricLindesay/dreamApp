package com.android.androidTesting.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.androidTesting.MainActivity;
import com.android.androidTesting.NoteActivity;
import com.android.androidTesting.R;


//Extend from the AppWidgetProvider class//

public class CollectionWidget extends AppWidgetProvider {
    public static final String EXTRA_ITEM = "com.android.androidTesting.widgets.EXTRA_ITEM";
    public static final String CLICK_WIDGET_LIST_VIEW = "com.android.androidTesting.widgets.CLICK_WIDGET_LIST_VIEW";
    public static final String REFRESH_WIDGET = "com.android.androidTesting.widgets.REFRESH_WIDGET";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//Load the layout resource file into a RemoteViews object//

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
        setRemoteAdapter(context, views);

//Inform AppWidgetManager about the RemoteViews object//
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("Widget", "On Update");
//        Toast.makeText(context,"onUpdate called", Toast.LENGTH_LONG).show();

        RemoteViews views = null;
        for (int appWidgetId : appWidgetIds) {
            Log.d("Widget", "App Widget ID "+appWidgetId);
//            updateAppWidget(context, appWidgetManager, appWidgetId);
            views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            setRemoteAdapter(context, views);

            clickListView(context, views, appWidgetId);

//            Inform AppWidgetManager about the RemoteViews object//
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
//        updateAppWidget(context, appWidgetManager, appWidgetIds);

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d("Widget", "widget header click");
        clickWidgetHeader(context, views);
        Log.d("Widget", "add new click");
        clickWidgetAddNew(context, views);

        Log.d("Widget", "before updatea pp widget");

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    void clickListView(Context context, RemoteViews views, int appWidgetId) {
        // When you click on the list view, broadcast CLICK_WIDGET_LIST_VIEW
        Intent intent = new Intent(context, WidgetService.class);
        Intent toastIntent = new Intent(context, CollectionWidget.class);
        toastIntent.setAction(CollectionWidget.CLICK_WIDGET_LIST_VIEW);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        views.setPendingIntentTemplate(R.id.listView, toastPendingIntent);
    }

    void clickWidgetHeader(Context context, RemoteViews views) {
        // When you click on an empty part of the widget or on the header, take you to the main
        // screen
        Intent configIntent = new Intent(context, MainActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.dreamsHeader, configPendingIntent);  // clicking the header takes you to main screen
        views.setOnClickPendingIntent(R.id.widgetID, configPendingIntent);  // clicking somewhere undefined takes you to main screen
    }

    void clickWidgetAddNew(Context context, RemoteViews views) {
        // When you click the "+" button on the widget, go to the add new note screen.
        Log.d("Widget", "Add new note pending intent");
        Intent configIntent = new Intent(context, MainActivity.class);
        configIntent.setData(Uri.parse("addNewNote"));
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_MUTABLE);
        configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_MUTABLE);
        views.setOnClickPendingIntent(R.id.addDream, configPendingIntent);  // clicking the header takes you to main screen
    }

    public static void sendRefreshBroadcast(Context context) {
        // Send a broadcast to the widget, telling it to refresh itself
        Intent intent = new Intent(REFRESH_WIDGET);
        intent.setComponent(new ComponentName(context, CollectionWidget.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d("Widget", "On Enabled");
//        Toast.makeText(context,"onEnabled called", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisabled(Context context) {
        Log.d("Widget", "On Disabled");
//        Toast.makeText(context,"onDisabled called", Toast.LENGTH_LONG).show();
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.listView,
                new Intent(context, WidgetService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"Refresh Widget", Toast.LENGTH_LONG).show();
        Log.d("Widget", "Received Broadcast");
        if (intent.getAction().equals(CLICK_WIDGET_LIST_VIEW)) {
            receiveClickListView(context, intent);
        }
        else if (intent.getAction().equals(REFRESH_WIDGET)) {
            receiveRefreshWidget(context);
        }
        super.onReceive(context, intent);
    }

    void receiveClickListView(Context context, Intent intent) {
        // Start an activity, loading the clicked list view on the app.
        Log.d("Widget", "Intent: "+intent.getExtras());
        Log.d("Widget", "Intent: "+intent.getIntExtra(CollectionWidget.EXTRA_ITEM, -1));
        int viewIndex = intent.getIntExtra(CollectionWidget.EXTRA_ITEM, -1);
        Log.d("Widget", "View index: "+viewIndex);
        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.setData(Uri.parse("editNote:"+viewIndex));
        context.startActivity(newIntent);
    }

    void receiveRefreshWidget(Context context) {
        // Refresh the widget list view
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, CollectionWidget.class);
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listView);
    }
}