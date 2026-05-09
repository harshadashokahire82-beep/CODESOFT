package com.example.alarmclockapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AlarmAdapter.OnAlarmActionListener {

    private TextView tvCurrentTime, tvCurrentDate;
    private ListView lvAlarms;
    private Button btnSetAlarm;

    private List<Alarm> alarmList;
    private AlarmAdapter alarmAdapter;
    private AlarmManager alarmManager;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Handler handler;
    private Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        lvAlarms = findViewById(R.id.lvAlarms);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        sharedPreferences = getSharedPreferences("AlarmPrefs", MODE_PRIVATE);
        gson = new Gson();
        alarmList = new ArrayList<>();

        loadAlarms();

        alarmAdapter = new AlarmAdapter(MainActivity.this, alarmList, MainActivity.this);
        lvAlarms.setAdapter(alarmAdapter);

        startClock();

        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetAlarmDialog();
            }
        });
    }

    private void startClock() {
        handler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());
                tvCurrentTime.setText(timeFormat.format(new Date()));
                tvCurrentDate.setText(dateFormat.format(new Date()));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timeRunnable);
    }

    private void showSetAlarmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_set_alarm, null);
        builder.setView(dialogView);

        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        EditText etLabel = dialogView.findViewById(R.id.etAlarmLabel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        timePicker.setIs24HourView(false);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String label = etLabel.getText().toString().trim();
                if (label.isEmpty()) label = "Alarm";

                int id = (int) System.currentTimeMillis();
                Alarm alarm = new Alarm(id, hour, minute, label, true);
                alarmList.add(alarm);
                alarmAdapter.notifyDataSetChanged();
                saveAlarms();
                scheduleAlarm(alarm);
                Toast.makeText(MainActivity.this, "Alarm set for " + alarm.getFormattedTime(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void scheduleAlarm(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra("alarm_label", alarm.getLabel());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, alarm.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent);
    }

    private void cancelAlarm(Alarm alarm) {
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, alarm.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onAlarmToggle(int position, boolean isEnabled) {
        Alarm alarm = alarmList.get(position);
        alarm.setEnabled(isEnabled);
        if (isEnabled) {
            scheduleAlarm(alarm);
            Toast.makeText(MainActivity.this, "Alarm ON ✅", Toast.LENGTH_SHORT).show();
        } else {
            cancelAlarm(alarm);
            Toast.makeText(MainActivity.this, "Alarm OFF ❌", Toast.LENGTH_SHORT).show();
        }
        saveAlarms();
    }

    @Override
    public void onAlarmDelete(int position) {
        cancelAlarm(alarmList.get(position));
        alarmList.remove(position);
        alarmAdapter.notifyDataSetChanged();
        saveAlarms();
        Toast.makeText(MainActivity.this, "Alarm deleted 🗑️", Toast.LENGTH_SHORT).show();
    }

    private void saveAlarms() {
        String json = gson.toJson(alarmList);
        sharedPreferences.edit().putString("alarms", json).apply();
    }

    private void loadAlarms() {
        String json = sharedPreferences.getString("alarms", null);
        if (json != null) {
            Type type = new TypeToken<List<Alarm>>() {}.getType();
            alarmList = gson.fromJson(json, type);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(timeRunnable);
        }
    }
}