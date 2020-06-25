package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.google.android.material.button.MaterialButton;

public class ScheduleActivity extends AppCompatActivity {
    Toolbar scheduleToolBar;
    MaterialButton scheduleSaveBtn;
    Spinner scheduleDaysOfWeekSpinner, scheduleHoursSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(ScheduleActivity.this);
        setContentView(R.layout.activity_schedule);
        MappingView();
        InitActionBar();
        InitSpinner();
    }

    private void InitSpinner() {
        //Set up adapter for days of week spinner
        ArrayAdapter<CharSequence> spinnerDaysOfWeekAdapter = ArrayAdapter.createFromResource(ScheduleActivity.this, R.array.days_of_week, android.R.layout.simple_spinner_dropdown_item);
        spinnerDaysOfWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scheduleDaysOfWeekSpinner.setAdapter(spinnerDaysOfWeekAdapter);

        //Set up adapter for hours spinner
        ArrayAdapter<CharSequence> spinnerHoursAdapter = ArrayAdapter.createFromResource(ScheduleActivity.this, R.array.hours, android.R.layout.simple_spinner_dropdown_item);
        spinnerHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scheduleHoursSpinner.setAdapter(spinnerHoursAdapter);
    }

    private void InitActionBar() {
        setSupportActionBar(scheduleToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        scheduleToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MappingView() {
        scheduleSaveBtn = findViewById(R.id.schedule_save_btn);
        scheduleToolBar = findViewById(R.id.schedule_toolbar);
        scheduleDaysOfWeekSpinner = findViewById(R.id.schedule_spinner_days_of_week);
        scheduleHoursSpinner = findViewById(R.id.schedule_spinner_hours);
    }
}
