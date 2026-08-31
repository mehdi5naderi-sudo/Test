package com.mehdi.sunwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SunWidgetProvider extends AppWidgetProvider {
    private static final double LAT = 35.6892;
    private static final double LON = 51.3890;
    private static final TimeZone TEHRAN = TimeZone.getTimeZone("Asia/Tehran");

    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) update(context, manager, id);
    }

    private void update(Context c, AppWidgetManager m, int id) {
        Calendar now = Calendar.getInstance(TEHRAN);
        String time = new SimpleDateFormat("HH:mm", Locale.US).format(now.getTime());
        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(now.getTime());
        String[] sun = solarTimes(now.get(Calendar.YEAR), now.get(Calendar.DAY_OF_YEAR));
        RemoteViews v = new RemoteViews(c.getPackageName(), R.layout.sun_widget);
        v.setTextViewText(R.id.time, time);
        v.setTextViewText(R.id.date, "تهران  •  " + date);
        v.setTextViewText(R.id.sunrise, "☀  طلوع  " + sun[0]);
        v.setTextViewText(R.id.sunset, "☾  غروب  " + sun[1]);
        m.updateAppWidget(id, v);
    }

    private static String[] solarTimes(int year, int day) {
        double tz = 3.5;
        double gamma = 2.0 * Math.PI / 365.0 * (day - 1);
        double eq = 229.18 * (0.000075 + 0.001868*Math.cos(gamma) - 0.032077*Math.sin(gamma)
                - 0.014615*Math.cos(2*gamma) - 0.040849*Math.sin(2*gamma));
        double decl = 0.006918 - 0.399912*Math.cos(gamma) + 0.070257*Math.sin(gamma)
                - 0.006758*Math.cos(2*gamma) + 0.000907*Math.sin(2*gamma)
                - 0.002697*Math.cos(3*gamma) + 0.00148*Math.sin(3*gamma);
        double lat = Math.toRadians(LAT);
        double zenith = Math.toRadians(90.833);
        double cosH = (Math.cos(zenith)/(Math.cos(lat)*Math.cos(decl))) - Math.tan(lat)*Math.tan(decl);
        if (cosH > 1) return new String[]{"--:--", "--:--"};
        if (cosH < -1) return new String[]{"00:00", "00:00"};
        double h = Math.toDegrees(Math.acos(cosH));
        double solarNoon = (720.0 - 4.0*LON - eq + tz*60.0) / 1440.0;
        return new String[]{formatMinutes(solarNoon - 4.0*h/1440.0), formatMinutes(solarNoon + 4.0*h/1440.0)};
    }

    private static String formatMinutes(double fraction) {
        int mins = (int)Math.round(fraction * 1440.0);
        mins = ((mins % 1440) + 1440) % 1440;
        return String.format(Locale.US, "%02d:%02d", mins/60, mins%60);
    }
}
