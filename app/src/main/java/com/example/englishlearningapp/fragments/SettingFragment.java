package com.example.englishlearningapp.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MainActivity;
import com.example.englishlearningapp.activity.MainHomeActivity;
import com.example.englishlearningapp.activity.ScheduleActivity;
import com.example.englishlearningapp.interfaces.MyListener;
import com.example.englishlearningapp.navigation_bottom_fragments.HomeFragment;
import com.example.englishlearningapp.receiver.AlarmReceiver;
import com.example.englishlearningapp.receiver.CancelAlarmReceiver;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.sql.Time;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Setting Fragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DatabaseAccess db;
    SharedPreferences prefs;
    AlarmManager alarmManager;
    Spinner spinnerStartHour, spinnerEndHour;
    HomeFragment homeFragment;
    Switch swtReminder;
    public SharedPreferences sharedPreferences;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = DatabaseAccess.getInstance(getActivity());
        prefs = getActivity().getSharedPreferences("historyIndex", Context.MODE_PRIVATE);
        sharedPreferences = getActivity().getSharedPreferences("switch", Context.MODE_PRIVATE);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
    }

    int startHour = 0;
    int endHour = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);
        spinnerStartHour = view.findViewById(R.id.spinner_start_hour);
        spinnerEndHour = view.findViewById(R.id.spinner_end_hour);
        swtReminder = view.findViewById(R.id.switchReminder);
        swtReminder.setChecked(sharedPreferences.getBoolean("checked", false));
        homeFragment = new HomeFragment();
        InitSpinner();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HandleSpinnerEvent();
        HandleSwitchEvent();
    }

    private void HandleSwitchEvent() {
        swtReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("checked", true);
                    editor.apply();
                    SharedPreferences.Editor indexEditor = prefs.edit();
                    indexEditor.putInt("index", 0);
                    indexEditor.apply();
                    long timeInMillis = 1000; //1 second
                    setRepeatAlarm(timeInMillis, startHour, endHour);
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("checked", false);
                    editor.apply();
                    Intent receiverIntent = new Intent(getActivity(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }
            }
        });
    }

    private void HandleSpinnerEvent() {
        spinnerStartHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startHour = position;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("startHour", startHour);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerEndHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endHour = position;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("endHour", endHour);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void InitSpinner(){
        ArrayAdapter<CharSequence> spinnerHoursAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.hours,
                android.R.layout.simple_spinner_dropdown_item);
        spinnerHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartHour.setAdapter(spinnerHoursAdapter);
        spinnerEndHour.setAdapter(spinnerHoursAdapter);
        int prefsStartHour = sharedPreferences.getInt("startHour", 0);
        int prefsEndHour = sharedPreferences.getInt("endHour", 23);
        spinnerStartHour.setSelection(prefsStartHour);
        spinnerEndHour.setSelection(prefsEndHour);
    }

    public void setRepeatAlarm(long timeInMillis, int startHour, int endHour) {
        db.open();
        if(db.getHistoryWords().size() > 0){
            Intent receiverIntent = new Intent(getActivity(), AlarmReceiver.class);
            receiverIntent.putExtra("startHour", startHour);
            receiverIntent.putExtra("endHour", endHour);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Set startHour
            Calendar calStart = Calendar.getInstance();
            calStart.set(Calendar.HOUR_OF_DAY, startHour);
            calStart.set(Calendar.MINUTE, 0);
            calStart.set(Calendar.SECOND, 0);

            //Fire the alarm
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calStart.getTimeInMillis(), timeInMillis, pendingIntent);
        }else{
            return;
        }
    }
}
