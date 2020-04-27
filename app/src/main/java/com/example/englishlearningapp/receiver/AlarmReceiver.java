package com.example.englishlearningapp.receiver;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.AlarmPropsManager;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.GlobalVariable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    NotificationManager notificationManager;
    NotificationCompat.Builder notiBuilder;
    public ArrayList<Word> alarmWords;
    DatabaseAccess db;
    public static final String NOTIFICATION_CHANNEL_ID = "4655";
    public static final String NOTIFICATION_CHANNEL_NAME = "my_channel";
    AlarmManager alarmManager;
    AlarmPropsManager alarmPropsManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        db = DatabaseAccess.getInstance(context);
        db.open();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmPropsManager = new AlarmPropsManager(context);
        //Create the same pending intent of alarm manager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Get start and end hours from Shared Preference
        int endHour = alarmPropsManager.getEndHour();
        int startHour = alarmPropsManager.getStartHour();
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Calendar nextDayCalendar = GetTheNextDayCalendar(startHour);

        //Get number of words to repeat
        int numberOfWords = alarmPropsManager.getWordNo();
        Log.d(TAG, "number of words: " + numberOfWords);

        //Get alarm word from database
        int alarmId = alarmPropsManager.getAlarmType();
        alarmWords = getAlarmWords(alarmId);

        //Get random index
        int arrayIndex = randomIndexWithoutDuplicate(alarmWords.size(), context);
        Log.d(TAG, "array index: " + arrayIndex);

        //Get alarm count
        int alarmCount = alarmPropsManager.getAlarmCount();

        if(alarmWords == null){
            alarmManager.cancel(pendingIntent);
            startTomorrowAlarm(alarmManager, pendingIntent, nextDayCalendar, context);
            return;
        }

        if((arrayIndex >= alarmWords.size()) || (alarmCount >= numberOfWords)){
            Log.d(TAG, "onReceive: stop alarm");
            alarmManager.cancel(pendingIntent);
            startTomorrowAlarm(alarmManager, pendingIntent, nextDayCalendar, context);
            return;
        }else{
            //Get an instance of notification manager
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String html = alarmWords.get(arrayIndex).getHtml();
            String word = alarmWords.get(arrayIndex).getWord();
            String mean = alarmWords.get(arrayIndex).getDescription();
            int id = alarmWords.get(arrayIndex).getId();

            //Implement notification channel
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(notificationChannel);
            }

            //Start Meaning activity when click on notification
            Intent meaningIntent = new Intent(context.getApplicationContext(), MeaningActivity.class);
            meaningIntent.putExtra("id", id);
            meaningIntent.putExtra("html", html);
            meaningIntent.putExtra("word", word);
            meaningIntent.putExtra("description", mean);
            meaningIntent.putExtra("show_popup", 1);
            meaningIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            meaningIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent meaningPendingIntent = PendingIntent.getActivity(context.getApplicationContext(), id, meaningIntent
                    , PendingIntent.FLAG_UPDATE_CURRENT);

            //Build notification
            notiBuilder = new NotificationCompat.Builder(context.getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(word)
                    .setContentIntent(meaningPendingIntent)
                    .setContentText(mean)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);
            notificationManager.notify(id, notiBuilder.build());

            //Increase alarm count
            alarmCount++;
            alarmPropsManager.setAlarmCount(alarmCount);

            //Add reminded word to database
            if(isRemindedSave(id)){
                db.addRemindedWordDate(id, System.currentTimeMillis());
            }else{
                db.addRemindedWord(id);
                db.addRemindedWordDate(id, System.currentTimeMillis());
            }

            //Turn off notification if meet end hour
            if(currentHour >= endHour){
                Log.d(TAG, "onReceive: meet end hour, stop alarm");
                alarmManager.cancel(pendingIntent);
                startTomorrowAlarm(alarmManager, pendingIntent, nextDayCalendar, context);
                return;
            }
        }
    }

    public boolean isRemindedSave(int wordId){
        if(db.getRemindedWordByWordId(wordId).getId() > 0){
            return true;
        }else{
            return false;
        }
    }

    private Calendar GetTheNextDayCalendar(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    private void startTomorrowAlarm(AlarmManager alarmManager
                                    , PendingIntent pendingIntent
                                    , Calendar startCalendar, Context context){
        Log.d(TAG, "startTomorrowAlarm: " + startCalendar.getTimeInMillis());
        GlobalVariable globalHashSet = GlobalVariable.getInstance(context);
        globalHashSet.clearHashSet();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startCalendar.getTimeInMillis(), 3000, pendingIntent);
    }

    private ArrayList<Word> getAlarmWords(int pAlarmId){
        ArrayList<Word> wordArrayList = null;
        if(pAlarmId == DatabaseContract.ALARM_HISTORY){
            wordArrayList = db.getHistoryWordsToAlarm();
        }else if(pAlarmId == DatabaseContract.ALARM_FAVORITE){
            wordArrayList = db.getFavoriteWordsToAlarm();
        }else{
            wordArrayList = db.getWordAlarmByTopicId(pAlarmId);
        }
        return  wordArrayList;
    }

    private int randomIndexWithoutDuplicate(int arraySize, Context context){
        GlobalVariable globalHashSet = GlobalVariable.getInstance(context);
        Log.d(TAG, "Hash set size: " + globalHashSet.getHashSetSize());
        Random randomNumGenerator = new Random();
        while(globalHashSet.getHashSetSize() < arraySize){
            int temp = randomNumGenerator.nextInt(arraySize);
            if(globalHashSet.addToHashSet(temp)){
                return temp;
            }
        }
        return arraySize + 1;
    }
}
