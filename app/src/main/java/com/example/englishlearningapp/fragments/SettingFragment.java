package com.example.englishlearningapp.fragments;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.SettingListViewAdapter;
import com.example.englishlearningapp.adapters.SettingListViewPopupAdapter;
import com.example.englishlearningapp.models.AlarmType;;
import com.example.englishlearningapp.models.Topic;
import com.example.englishlearningapp.models.Word;
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
    Toolbar settingToolbar;
    Switch swtReminder;
    ListView lvSetting;
    public SharedPreferences sharedPreferences;
    SettingListViewAdapter lvAdapter;
    ArrayList<AlarmType> alarmTypeList = null;
    boolean isChecked = false;
    AlarmPropsManager alarmPropsManager;
    Dialog settingPopup;
    ArrayList<Topic> topicList = null;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);
        settingPopup = new Dialog(getActivity());
        spinnerStartHour = view.findViewById(R.id.spinner_start_hour);
        spinnerEndHour = view.findViewById(R.id.spinner_end_hour);
        spinnerNumberOfWords = view.findViewById(R.id.spinner_number_of_words);
        swtReminder = view.findViewById(R.id.switchReminder);
        lvSetting = view.findViewById(R.id.lv_setting);
        settingToolbar = view.findViewById(R.id.setting_toolbar);
        settingToolbar.setTitle("");
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingToolbar);
        SetUpListView();
        InitSpinner();
        swtReminder.setChecked(isChecked);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
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
                    alarmPropsManager.setAlarmCount(0);
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
        spinnerStartHour.setSelection(alarmPropsManager.getStartHour());
        spinnerEndHour.setSelection(alarmPropsManager.getEndHour());

        ArrayAdapter<CharSequence> spinnerNumberOfWordsAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.numberOfWords,
                android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberOfWordsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberOfWords.setAdapter(spinnerNumberOfWordsAdapter);
        spinnerNumberOfWords.setSelection(alarmPropsManager.getWordNo() - 1);
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
        if(topicList == null){
            topicList = db.getTopics();
        }
        if(alarmTypeList == null){
            alarmTypeList = new ArrayList<>();
        }
        alarmTypeList.add(new AlarmType(DatabaseContract.ALARM_HISTORY, getString(R.string.history) + " (" + db.getHistoryWordsCount() + ")", false));
        alarmTypeList.add(new AlarmType(DatabaseContract.ALARM_FAVORITE, getString(R.string.favorite) + " (" + db.getFavoriteWordsCount() + ")", false));
        if(alarmPropsManager.getAlarmType() == DatabaseContract.ALARM_HISTORY){
            alarmTypeList.get(0).setChecked(true);
        }
        if(alarmPropsManager.getAlarmType() == DatabaseContract.ALARM_FAVORITE){
            alarmTypeList.get(1).setChecked(true);
        }
        if(alarmTypeList.size() < 3 && topicList != null){
            int len = topicList.size();
            for(int i =0;i<len;i++){
                int wordCount = db.getWordCountByTopicId(topicList.get(i).getTopicId());
                int topicId = topicList.get(i).getTopicId();
                String topicName = topicList.get(i).getTopicName();
                if(alarmPropsManager.getAlarmType() == topicId){
                    alarmTypeList.add(new AlarmType(topicId, topicName + " (" +wordCount + ")", true));
                }else{
                    alarmTypeList.add(new AlarmType(topicId, topicName + " (" +wordCount + ")", false));
                }
            }
        }

        Log.d(TAG, "SetUpListView: alarm List length: " + alarmTypeList.size());
        lvAdapter = new SettingListViewAdapter(this, alarmTypeList);
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
                    int alarmId = alarmTypeList.get(position).getAlarmId();
                    alarmPropsManager.setAlarmType(alarmId);
                    //Set isChecked props of selected item to true
                    alarmTypeList.get(position).setChecked(true);
//                    reStartSwitch();
                    swtReminder.setChecked(false);
                }
                lvAdapter.notifyDataSetChanged();
            }
        });
        lvSetting.setAdapter(lvAdapter);
    }

    public void reStartSwitch(){
        if(swtReminder.isChecked()){
            swtReminder.setChecked(false);
            swtReminder.setChecked(true);
        }
    }

    public void showPopupWordList(int alarmId){
        ArrayList<Word> wordList = null;
        if(alarmId == DatabaseContract.ALARM_HISTORY){
            wordList = db.getHistoryWordsWithoutDuplicateSortByAZ();
        }else if(alarmId == DatabaseContract.ALARM_FAVORITE){
            wordList = db.getFavoriteWords();
        }else{
            wordList = db.getWordsByTopicId(alarmId);
        }

        settingPopup.setContentView(R.layout.popup_setting);
        ListView lvSettingPopup;
        TextView txtSettingPopupEmpty;
        txtSettingPopupEmpty = settingPopup.findViewById(R.id.txt_popup_setting_empty);
        lvSettingPopup = settingPopup.findViewById(R.id.popup_setting_lv_word);
        if(wordList.isEmpty()){
            txtSettingPopupEmpty.setVisibility(View.VISIBLE);
        }else{
            SettingListViewPopupAdapter adapter = new SettingListViewPopupAdapter(wordList, getActivity());
            lvSettingPopup.setAdapter(adapter);
        }
        settingPopup.show();
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
                Log.d(TAG, "onItemSelected: Word No: " + alarmPropsManager.getWordNo());
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
            alarmPropsManager.setStartHour(position);
            if(userSelect){
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
            alarmPropsManager.setEndHour(position);
            if(userSelect){
                reStartSwitch();
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
