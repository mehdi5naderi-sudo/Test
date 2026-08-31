package com.example.analogclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import java.util.Calendar;

public class ClockWidgetProvider extends AppWidgetProvider {
    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) update(context, manager, id);
    }
    private void update(Context c, AppWidgetManager m, int id) {
        Calendar now = Calendar.getInstance();
        RemoteViews v = new RemoteViews(c.getPackageName(), R.layout.clock_widget);
        v.setTextViewText(R.id.hour, String.format("%02d", now.get(Calendar.HOUR_OF_DAY)));
        v.setTextViewText(R.id.minute, String.format("%02d", now.get(Calendar.MINUTE)));
        v.setTextViewText(R.id.second, String.format("%02d", now.get(Calendar.SECOND)));
        m.updateAppWidget(id, v);
    }
}
