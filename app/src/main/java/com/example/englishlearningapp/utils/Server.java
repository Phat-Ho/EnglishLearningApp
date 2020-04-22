package com.example.englishlearningapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Server
{
    public static String host = "http://192.168.1.62/";
    public static String backend_host = "http://192.168.1.62:5001/";
    public static String REGISTER_URL = backend_host + "api/hash/addOrUpdate";
    public static String LOGIN_URL = backend_host + "api/hash/login?";
    public static String ADD_HISTORY_URL = Server.host + "english/addHistory.php";
    public static String GET_HISTORY_URL = Server.host + "english/getHistoryByUserId.php?";
    public static String UPDATE_HISTORY_URL = Server.host + "english/updateHistorySync.php?";
    public static String CHECK_USER_URL = backend_host + "api/hash/checkUser?";

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
