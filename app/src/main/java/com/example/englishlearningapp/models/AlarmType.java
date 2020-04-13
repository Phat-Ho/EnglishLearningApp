package com.example.englishlearningapp.models;

public class AlarmType {
    public int alarmId;
    public String alarmName;
    public boolean isChecked;

    public AlarmType(int alarmId, String alarmName, boolean isChecked) {
        this.alarmId = alarmId;
        this.alarmName = alarmName;
        this.isChecked = isChecked;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
