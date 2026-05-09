package com.example.alarmclockapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {

    private static final String CHANNEL_ID = "alarm_channel";
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Action check karo (Dismiss ya Snooze)
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "DISMISS":
                    stopAlarm();
                    return START_NOT_STICKY;
                case "SNOOZE":
                    snoozeAlarm();
                    return START_NOT_STICKY;
            }
        }

        // Label lo
        String label = "Alarm";
        if (intent != null && intent.getStringExtra("alarm_label") != null) {
            label = intent.getStringExtra("alarm_label");
        }

        // Notification Channel banao
        createNotificationChannel();

        // Dismiss Intent
        Intent dismissIntent = new Intent(this, AlarmService.class);
        dismissIntent.setAction("DISMISS");
        PendingIntent dismissPending = PendingIntent.getService(
                this, 0, dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Snooze Intent
        Intent snoozeIntent = new Intent(this, AlarmService.class);
        snoozeIntent.setAction("SNOOZE");
        PendingIntent snoozePending = PendingIntent.getService(
                this, 1, snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Notification banao
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("⏰ " + label)
                .setContentText("Alarm is ringing!")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .addAction(android.R.drawable.ic_media_pause, "Snooze", snoozePending)
                .addAction(android.R.drawable.ic_delete, "Dismiss", dismissPending)
                .setAutoCancel(false)
                .build();

        startForeground(1, notification);

        // Alarm Sound
        Uri alarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
        mediaPlayer = MediaPlayer.create(this, alarmUri);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        // Vibration
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 1000, 500};
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        }

        return START_STICKY;
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        stopForeground(true);
        stopSelf();
    }

    private void snoozeAlarm() {
        stopAlarm();
        android.app.AlarmManager alarmManager =
                (android.app.AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 999, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000);
        alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Alarm Notifications");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}