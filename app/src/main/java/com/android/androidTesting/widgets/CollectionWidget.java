package com.android.androidTesting.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.androidTesting.MainActivity;
import com.android.androidTesting.NoteActivity;
import com.android.androidTesting.R;


//Extend from the AppWidgetProvider class//

public class CollectionWidget extends AppWidgetProvider {
    public static final String EXTRA_ITEM = "com.android.androidTesting.widgets.EXTRA_ITEM";
    public static final String TOAST_ACTION = "com.android.androidTesting.widgets.TOAST_ACTION";

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
        RemoteViews views = null;
        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
            views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            setRemoteAdapter(context, views);

            Intent intent = new Intent(context, WidgetService.class);
            Intent toastIntent = new Intent(context, CollectionWidget.class);
            toastIntent.setAction(CollectionWidget.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.listView, toastPendingIntent);
//
//            Inform AppWidgetManager about the RemoteViews object//
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
//        updateAppWidget(context, appWidgetManager, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent configIntent = new Intent(context, MainActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.dreamsHeader, configPendingIntent);  // clicking the header takes you to main screen
        views.setOnClickPendingIntent(R.id.widgetID, configPendingIntent);  // clicking somewhere undefined takes you to main screen

        configIntent = new Intent(context, MainActivity.class);
        configIntent.setData(Uri.parse("addNewNote"));
        configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.addDream, configPendingIntent);  // clicking the header takes you to main screen

        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }

    @Override
    public void onEnabled(Context context) {
        Toast.makeText(context,"onEnabled called", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisabled(Context context) {
        Toast.makeText(context,"onDisabled called", Toast.LENGTH_LONG).show();
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.listView,
                new Intent(context, WidgetService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Intent newIntent = new Intent(context, MainActivity.class);
//            newIntent.putExtra("editNote", viewIndex);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setData(Uri.parse("editNote:"+viewIndex));
            context.startActivity(newIntent);
        }
        super.onReceive(context, intent);
    }
}