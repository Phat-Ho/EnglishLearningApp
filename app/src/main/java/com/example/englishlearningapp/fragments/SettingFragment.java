package com.example.englishlearningapp.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

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
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.Calendar;
import java.util.Set;

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
    ImageButton imgBtnBackToHome;
    HomeFragment homeFragment;
    Switch swtReminder;
    public static boolean notifyIsChecked;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);
        spinnerStartHour = view.findViewById(R.id.spinner_start_hour);
        spinnerEndHour = view.findViewById(R.id.spinner_end_hour);
        imgBtnBackToHome = view.findViewById(R.id.imageButtonBackToHome);
        homeFragment = new HomeFragment();
        final MainHomeActivity mainHomeActivity = (MainHomeActivity) getContext();
        swtReminder = view.findViewById(R.id.switchReminder);
        swtReminder.setChecked(sharedPreferences.getBoolean("checked", false));
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
                    setRepeatAlarm(timeInMillis);
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

        imgBtnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainHomeActivity.showFragment(homeFragment);
            }
        });
        InitSpinner();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notifyIsChecked = sharedPreferences.getBoolean("checked", false);
    }

    private void InitSpinner(){
        ArrayAdapter<CharSequence> spinnerHoursAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.hours,
                android.R.layout.simple_spinner_dropdown_item);
        spinnerHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartHour.setAdapter(spinnerHoursAdapter);
        spinnerEndHour.setAdapter(spinnerHoursAdapter);
    }

    public void setRepeatAlarm(long timeInMillis) {
        db.open();
        if(db.getHistoryWords().size() > 0){
            Intent receiverIntent = new Intent(getActivity(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            /*calendar.set(Calendar.HOUR_OF_DAY, calendar.getTime().getHours());
            calendar.set(Calendar.MINUTE, calendar.getTime().getMinutes());
            calendar.set(Calendar.SECOND, calendar.getTime().getSeconds());*/
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), timeInMillis, pendingIntent);
        }else{
            return;
        }
    }
}
