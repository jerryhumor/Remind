package com.hdu.hanjianhao.remind;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.Calendar;

/**
 * Created by hanjianhao on 17/4/15.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        int reminderId = Integer.parseInt(intent.getStringExtra("reminderId"));
        Reminder reminder = DataSupport.find(Reminder.class, reminderId);
        String name = reminder.getName();

        Intent detailIntent = new Intent(context, RemindThingDetailActivity.class);
        detailIntent.putExtra("id", reminderId);
        PendingIntent mClick = PendingIntent.getActivity(context, reminderId, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_remind))
                .setSmallIcon(R.drawable.ic_access_time_grey600_24dp)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setTicker("你有东西要过期啦")
                .setContentText(name+"即将过期")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(mClick)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(reminderId, mBuilder.build());
    }

    public void setAlarm(Context context, Calendar calendar, int ID, int advancedHour) {

        String tag = "setAlarm";

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminderId", Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime - advancedHour*3600*1000;

        Log.d(tag, "year: " + calendar.get(Calendar.YEAR));
        Log.d(tag, "advanced hour: " + advancedHour);
        Log.d(tag, "diffTime: " + diffTime);

        // Start alarm using notification time
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);
    }

    public void cancelAlarm(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);
    }
}
