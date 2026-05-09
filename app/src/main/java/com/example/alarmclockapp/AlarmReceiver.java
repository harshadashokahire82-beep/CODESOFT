package com.example.alarmclockapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra("alarm_label", intent.getStringExtra("alarm_label"));
        context.startForegroundService(serviceIntent);
    }
}