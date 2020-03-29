package com.example.englishlearningapp.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MainActivity;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.ArrayList;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    NotificationManager notificationManager;
    NotificationCompat.Builder notiBuilder;
    public ArrayList<Word> historyWords;
    DatabaseAccess db;
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get history words array index to show notification
        SharedPreferences preferences = context.getSharedPreferences("historyIndex", Context.MODE_PRIVATE);
        int arrayIndex = preferences.getInt("index", 0);
        //Get history word from database
        db = DatabaseAccess.getInstance(context);
        db.open();
        historyWords = db.getHistoryWords();
        Log.d(TAG, "setRepeatAlarm: " + arrayIndex);
        //Get an instance of notification manager
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String html = historyWords.get(arrayIndex).getHtml();
        String word = historyWords.get(arrayIndex).getWord();

        //Start Meaning activity when click on notification
        Intent meaningIntent = new Intent(context, MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        PendingIntent meaningPendingIntent = PendingIntent.getActivity(context, 0, meaningIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build notification
        notiBuilder = new NotificationCompat.Builder(context).setContentTitle(word)
                                                                .setContentIntent(meaningPendingIntent)
                                                                .setSmallIcon(R.mipmap.ic_launcher);
        notificationManager.notify((int) System.currentTimeMillis(), notiBuilder.build());

        //Increase history index in SharedPrefs
        arrayIndex++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("index", arrayIndex);
        editor.apply();

        if(arrayIndex == historyWords.size()){
            context.unregisterReceiver(this);
        }
    }
}
