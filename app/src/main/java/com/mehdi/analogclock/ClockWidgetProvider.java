package com.mehdi.analogclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.SystemClock;
import android.widget.RemoteViews;

public class ClockWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_TICK = "com.mehdi.analogclock.TICK";
    @Override public void onUpdate(Context c, AppWidgetManager m, int[] ids) {
        for (int id: ids) update(c,m,id);
        schedule(c);
    }
    @Override public void onReceive(Context c, Intent i) {
        super.onReceive(c,i);
        if (ACTION_TICK.equals(i.getAction())) {
            AppWidgetManager m=AppWidgetManager.getInstance(c);
            int[] ids=m.getAppWidgetIds(new android.content.ComponentName(c,ClockWidgetProvider.class));
            for(int id:ids) update(c,m,id);
            schedule(c);
        }
    }
    private void update(Context c, AppWidgetManager m, int id) {
        RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.clock_widget);
        v.setImageViewBitmap(R.id.clock, drawClock(300,300));
        m.updateAppWidget(id,v);
    }
    private Bitmap drawClock(int w,int h) {
        Bitmap b=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888); Canvas c=new Canvas(b);
        float cx=w/2f, cy=h/2f, r=Math.min(w,h)/2f-10;
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); p.setStyle(Paint.Style.FILL);
        p.setShader(new LinearGradient(0,0,w,h,Color.rgb(30,34,45),Color.rgb(8,10,16),Shader.TileMode.CLAMP)); c.drawCircle(cx,cy,r+6,p);
        p.setShader(null); p.setColor(Color.rgb(235,238,242)); c.drawCircle(cx,cy,r,p);
        p.setColor(Color.rgb(35,40,50)); c.drawCircle(cx,cy,r-7,p);
        p.setColor(Color.rgb(220,224,230)); p.setTextAlign(Paint.Align.CENTER); p.setTypeface(Typeface.create("sans",Typeface.BOLD)); p.setTextSize(r*.17f);
        for(int n=1;n<=12;n++){ double a=Math.toRadians(n*30-90); float x=cx+(float)Math.cos(a)*r*.72f, y=cy+(float)Math.sin(a)*r*.72f-(p.ascent()+p.descent())/2; c.drawText(String.valueOf(n),x,y,p); }
        p.setStrokeCap(Paint.Cap.ROUND); p.setStyle(Paint.Style.STROKE);
        for(int i=0;i<60;i++){ double a=Math.toRadians(i*6-90); float rr=i%5==0?r*.83f:r*.88f; p.setStrokeWidth(i%5==0?5:2); p.setColor(i%5==0?Color.WHITE:Color.rgb(120,128,140)); c.drawLine(cx+(float)Math.cos(a)*rr,cy+(float)Math.sin(a)*rr,cx+(float)Math.cos(a)*(r*.91f),cy+(float)Math.sin(a)*(r*.91f),p); }
        java.util.Calendar now=java.util.Calendar.getInstance(); float sec=now.get(java.util.Calendar.SECOND); float min=now.get(java.util.Calendar.MINUTE)+sec/60f; float hour=(now.get(java.util.Calendar.HOUR)%12)+min/60f;
        hand(c,cx,cy,r*.48f,hour*30-90,10,Color.WHITE); hand(c,cx,cy,r*.66f,min*6-90,7,Color.WHITE); hand(c,cx,cy,r*.72f,sec*6-90,3,Color.rgb(230,70,70));
        p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(230,70,70)); c.drawCircle(cx,cy,8,p); p.setColor(Color.WHITE); c.drawCircle(cx,cy,4,p);
        return b;
    }
    private void hand(Canvas c,float cx,float cy,float len,float deg,float sw,int color){ Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); p.setColor(color); p.setStrokeWidth(sw); p.setStrokeCap(Paint.Cap.ROUND); double a=Math.toRadians(deg); c.drawLine(cx,cy,cx+(float)Math.cos(a)*len,cy+(float)Math.sin(a)*len,p); }
    private void schedule(Context c){ AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE); Intent i=new Intent(c,ClockWidgetProvider.class); i.setAction(ACTION_TICK); PendingIntent pi=PendingIntent.getBroadcast(c,7,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE); long t=SystemClock.elapsedRealtime()+1000; a.setRepeating(AlarmManager.ELAPSED_REALTIME,t,60000,pi); }
}
