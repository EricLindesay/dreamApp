package com.android.androidTesting.widgets;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Widget", "WidgetService.onGetViewFactory");
        return new DataProvider(this, intent);
    }
}