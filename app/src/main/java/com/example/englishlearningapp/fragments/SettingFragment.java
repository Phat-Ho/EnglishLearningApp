package com.example.englishlearningapp.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.SettingListViewAdapter;
import com.example.englishlearningapp.models.AlarmType;;
import com.example.englishlearningapp.receiver.AlarmReceiver;
import com.example.englishlearningapp.utils.AlarmPropsManager;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;

import java.util.ArrayList;
import java.util.Calendar;
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
    AlarmManager alarmManager;
    Spinner spinnerStartHour, spinnerEndHour, spinnerNumberOfWords;
    Switch swtReminder;
    ListView lvSetting;
    public SharedPreferences sharedPreferences;
    SettingListViewAdapter lvAdapter;
    public ArrayList<AlarmType> alarmTypeList;
    boolean isChecked = false;
    AlarmPropsManager alarmPropsManager;

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
        db.open();
        alarmPropsManager = new AlarmPropsManager(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("switch", Context.MODE_PRIVATE);
        isChecked = sharedPreferences.getBoolean("checked", false);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmTypeList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);
        spinnerStartHour = view.findViewById(R.id.spinner_start_hour);
        spinnerEndHour = view.findViewById(R.id.spinner_end_hour);
        spinnerNumberOfWords = view.findViewById(R.id.spinner_number_of_words);
        swtReminder = view.findViewById(R.id.switchReminder);
        lvSetting = view.findViewById(R.id.lv_setting);
        SetUpListView();
        InitSpinner();
        swtReminder.setChecked(isChecked);
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
                    alarmPropsManager.setIndex(0);
                    long timeInMillis = 3000; //3 second
                    setRepeatAlarm(timeInMillis, alarmPropsManager.getStartHour());
                }else {
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
        StartSpinnerListener startSpinnerListener = new StartSpinnerListener();
        EndSpinnerListener endSpinnerListener = new EndSpinnerListener();
        NumberOfWordsSpinnerListener numberOfWordsSpinnerListener = new NumberOfWordsSpinnerListener();
        spinnerStartHour.setOnItemSelectedListener(startSpinnerListener);
        spinnerStartHour.setOnTouchListener(startSpinnerListener);
        spinnerEndHour.setOnItemSelectedListener(endSpinnerListener);
        spinnerEndHour.setOnTouchListener(endSpinnerListener);
        spinnerNumberOfWords.setOnItemSelectedListener(numberOfWordsSpinnerListener);
        spinnerNumberOfWords.setOnTouchListener(numberOfWordsSpinnerListener);
    }

    private void InitSpinner() {
        ArrayAdapter<CharSequence> spinnerHoursAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.hours,
                android.R.layout.simple_spinner_dropdown_item);
        spinnerHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartHour.setAdapter(spinnerHoursAdapter);
        spinnerEndHour.setAdapter(spinnerHoursAdapter);
        Log.d(TAG, "InitSpinner: " + alarmPropsManager.getStartHour());
        spinnerStartHour.setSelection(alarmPropsManager.getStartHour());
        spinnerEndHour.setSelection(alarmPropsManager.getEndHour());

        ArrayAdapter<CharSequence> spinnerNumberOfWordsAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.numberOfWords,
                android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberOfWordsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberOfWords.setAdapter(spinnerNumberOfWordsAdapter);
    }

    public void setRepeatAlarm(long timeInMillis, int startHour) {
        Intent receiverIntent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set startHour
        Calendar calStart = Calendar.getInstance(TimeZone.getDefault());
        calStart.set(Calendar.HOUR_OF_DAY, startHour);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        Log.d(TAG, "startHour: " + calStart.getTimeInMillis());

        //Fire the alarm
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calStart.getTimeInMillis(), timeInMillis, pendingIntent);
    }

    private void SetUpListView() {
        Log.d(TAG, "alarm Type: " + alarmPropsManager.getAlarmType());
        alarmTypeList.add(new AlarmType(DatabaseContract.ALARM_HISTORY, "Lịch sử", false));
        alarmTypeList.add(new AlarmType(DatabaseContract.ALARM_FAVORITE, "Yêu thích", false));
        if(alarmPropsManager.getAlarmType() == DatabaseContract.ALARM_HISTORY){
            alarmTypeList.get(0).setChecked(true);
        }
        if(alarmPropsManager.getAlarmType() == DatabaseContract.ALARM_FAVORITE){
            alarmTypeList.get(1).setChecked(true);
        }
        lvAdapter = new SettingListViewAdapter(getActivity(), alarmTypeList);
        lvSetting.setAdapter(lvAdapter);
        lvSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isChecked = alarmTypeList.get(position).isChecked();
                if(isChecked){
                    //do nothing
                }else{
                    //Set all isChecked props in alarmTypeList to false
                    for(int i = 0;i<alarmTypeList.size(); i++){
                        alarmTypeList.get(i).setChecked(false);
                    }
                    //update alarm type
                    alarmPropsManager.setAlarmType(position);
                    //Set isChecked props of selected item to true
                    alarmTypeList.get(position).setChecked(true);
                    reStartSwitch();
                }
                lvAdapter.notifyDataSetChanged();
            }
        });
    }

    public void reStartSwitch(){
        if(swtReminder.isChecked()){
            swtReminder.setChecked(false);
            swtReminder.setChecked(true);
        }
    }

    public class NumberOfWordsSpinnerListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener{
        boolean userSelected = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelected = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(userSelected){
                alarmPropsManager.setWordNo(position + 1);
                reStartSwitch();
                userSelected = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public class StartSpinnerListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener{
        boolean userSelect = false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(userSelect){
                alarmPropsManager.setStartHour(position);
                Log.d(TAG, "start Hour: " + position);
                reStartSwitch();
                userSelect = false;
            }else{
                Log.d(TAG, "not user select");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public class EndSpinnerListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener{
        boolean userSelect = false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(userSelect){
                alarmPropsManager.setEndHour(position);
                reStartSwitch();
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
