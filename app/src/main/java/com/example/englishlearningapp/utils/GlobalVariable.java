package com.example.englishlearningapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class GlobalVariable extends Application {
    private static GlobalVariable instance = null;
    private static final String TAG = "GlobalVariable";
    private GlobalVariable(Context context){
        instance = (GlobalVariable) context.getApplicationContext();
    }

    public GlobalVariable() { }

    public static GlobalVariable getInstance(Context context) {
        if(instance == null){
            instance = new GlobalVariable(context);
        }
        return instance;
    }

    public static void changeStatusBarColor(Activity activity){
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#03A9F4"));

    }

    public Socket mSocket;
    {
        try {
            mSocket = IO.socket(Server.SOCKET_HOST);
        } catch (URISyntaxException e) {
            Log.d(TAG, "instance initializer: " + e.getMessage());
        }
    }

}
