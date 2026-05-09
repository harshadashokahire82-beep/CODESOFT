package com.example.alarmclockapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class AlarmAdapter extends ArrayAdapter<Alarm> {

    private Context context;
    private List<Alarm> alarmList;
    private OnAlarmActionListener listener;

    public interface OnAlarmActionListener {
        void onAlarmToggle(int position, boolean isEnabled);
        void onAlarmDelete(int position);
    }

    public AlarmAdapter(Context context, List<Alarm> alarmList, OnAlarmActionListener listener) {
        super(context, 0, alarmList);
        this.context = context;
        this.alarmList = alarmList;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.alarm_item, parent, false);
        }

        Alarm alarm = alarmList.get(position);

        // Views
        TextView tvAlarmTime = convertView.findViewById(R.id.tvAlarmTime);
        TextView tvAlarmLabel = convertView.findViewById(R.id.tvAlarmLabel);
        Switch switchAlarm = convertView.findViewById(R.id.switchAlarm);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        // Data set karo
        tvAlarmTime.setText(alarm.getFormattedTime());
        tvAlarmLabel.setText(alarm.getLabel());
        switchAlarm.setChecked(alarm.isEnabled());

        // Toggle listener
        switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onAlarmToggle(position, isChecked);
            }
        });

        // Delete listener
        btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlarmDelete(position);
            }
        });

        return convertView;
    }
}