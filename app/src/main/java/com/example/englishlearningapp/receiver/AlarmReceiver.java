package com.example.englishlearningapp.receiver;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.core.app.NotificationCompat;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    NotificationManager notificationManager;
    NotificationCompat.Builder notiBuilder;
    public ArrayList<Word> alarmWords;
    DatabaseAccess db;
    public static final String NOTIFICATION_CHANNEL_ID = "4655";
    public static final String NOTIFICATION_CHANNEL_NAME = "my_channel";
    AlarmManager alarmManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        db = DatabaseAccess.getInstance(context);
        db.open();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Create the same pending intent of alarm manager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Get history words array index to show notification
        SharedPreferences preferences = context.getSharedPreferences("historyIndex", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int arrayIndex = preferences.getInt("index", 0);

        //Get start and end hours from Shared Preference
        int endHour = intent.getIntExtra("endHour", 23);
        int startHour = intent.getIntExtra("startHour", 0);
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Calendar nextDayCalendar = GetTheNextDayCalendar(startHour);

        //Get number of words to repeat
        int numberOfWords = intent.getIntExtra("numberOfWords", 1);

        //Get alarm word from database
        int alarmId = intent.getIntExtra("alarmId", 1);
        alarmWords = getAlarmWords(alarmId, numberOfWords);


        if(alarmWords == null){
            alarmManager.cancel(pendingIntent);
            startTomorrowAlarm(alarmManager, pendingIntent, nextDayCalendar, editor);
            return;
        }

        if(arrayIndex >= alarmWords.size()){
            Log.d(TAG, "onReceive: stop alarm");
            alarmManager.cancel(pendingIntent);
            startTomorrowAlarm(alarmManager, pendingIntent, nextDayCalendar, editor);
            return;
        }

        if(alarmWords !=null){
            Log.d(TAG, "array index: " + arrayIndex);
            //Get an instance of notification manager
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String html = alarmWords.get(arrayIndex).getHtml();
            String word = alarmWords.get(arrayIndex).getWord();
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
            String str = Html.fromHtml(html).toString();
            String[] split = str.split("\\r?\\n");
            String mean = split[6];
            meaningIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            meaningIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent meaningPendingIntent = PendingIntent.getActivity(context.getApplicationContext(), id, meaningIntent
                                                                            , PendingIntent.FLAG_UPDATE_CURRENT);

            //Build notification
            notiBuilder = new NotificationCompat.Builder(context.getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                                            .setContentTitle(word)
                                            .setContentIntent(meaningPendingIntent)
                                            .setContentText(mean)
                                            .setSmallIcon(R.mipmap.ic_launcher);
            notificationManager.notify(id, notiBuilder.build());

            //Increase history index in SharedPrefs
            arrayIndex++;
            editor.putInt("index", arrayIndex);
            editor.apply();

            //Turn off notification if meet end hour
            if(currentHour >= endHour){
                Log.d(TAG, "onReceive: meet end hour");
                alarmManager.cancel(pendingIntent);
                startTomorrowAlarm(alarmManager, pendingIntent, nextDayCalendar, editor);
                return;
            }
        }else{
            Log.d(TAG, "onReceive: no history data");
            return;
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
                                    , Calendar startCalendar
                                    , SharedPreferences.Editor editor){
        Log.d(TAG, "startTomorrowAlarm");
        editor.putInt("index", 0);
        editor.apply();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startCalendar.getTimeInMillis(), 3000, pendingIntent);
    }

    private ArrayList<Word> getAlarmWords(int pAlarmId, int numberOfWords){
        ArrayList<Word> words = null;
        ArrayList<Word> historyWords;
        if(pAlarmId == DatabaseContract.ALARM_HISTORY){
            historyWords = db.getHistoryWords();
            for (Word word: new ArrayList<Word>(historyWords)){
                if (word.getRemembered() == 1){
                    historyWords.remove(word);
                }
            }
            words = historyWords;
            ArrayList<Word> tmpWords = new ArrayList<>();
            for (int i = 0; i < numberOfWords; i++){
                tmpWords.add(words.get(i));
            }
            words = tmpWords;
        }
        if(pAlarmId == DatabaseContract.ALARM_FAVORITE){
            words = db.getFavoriteWords();
        }
        return words;
    }
}
