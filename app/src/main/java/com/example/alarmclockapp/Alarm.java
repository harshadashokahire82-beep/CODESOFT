package com.example.alarmclockapp;

public class Alarm {
    private int id;
    private int hour;
    private int minute;
    private String label;
    private boolean isEnabled;

    public Alarm(int id, int hour, int minute, String label, boolean isEnabled) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.label = label;
        this.isEnabled = isEnabled;
    }

    public int getId() { return id; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public String getLabel() { return label; }
    public boolean isEnabled() { return isEnabled; }

    public void setId(int id) { this.id = id; }
    public void setHour(int hour) { this.hour = hour; }
    public void setMinute(int minute) { this.minute = minute; }
    public void setLabel(String label) { this.label = label; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public String getFormattedTime() {
        String period = hour >= 12 ? "PM" : "AM";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;
        return String.format("%02d:%02d %s", displayHour, minute, period);
    }
}