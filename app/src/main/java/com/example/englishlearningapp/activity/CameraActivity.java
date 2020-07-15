package com.example.englishlearningapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION = 1;
    ImageView imgTranslate;
    ListView lvCaptureText;
    ArrayList<String> arrCaptureText;
    ArrayAdapter adapter;
    DatabaseAccess databaseAccess;
    LoginManager loginManager;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;
    Bitmap bmp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(CameraActivity.this);
        setContentView(R.layout.activity_camera);
        imgTranslate = findViewById(R.id.image_view_translate);
        lvCaptureText = findViewById(R.id.list_view_capture_text);
        databaseAccess = DatabaseAccess.getInstance(this);
        arrCaptureText = new ArrayList<>();
        loginManager = new LoginManager(this);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrCaptureText);
        String filename = getIntent().getStringExtra("image");
        getBitmapIntent(filename, bmp);
        lvCaptureText.setAdapter(adapter);
        lvCaptureText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Word> wordList = databaseAccess.getWordExactly(arrCaptureText.get(position).trim().toLowerCase());
                if(wordList.isEmpty()){
                    Toast.makeText(CameraActivity.this, "Không thể tra từ", Toast.LENGTH_SHORT).show();
                }else {
                    String html = wordList.get(0).getHtml();
                    String word = wordList.get(0).getWord();
                    int wordId = wordList.get(0).getId();
                    int remembered = wordList.get(0).getRemembered();
                    moveToMeaningActivity(html, word, wordId, remembered);
                    saveHistory(wordId, loginManager.getUserId());
                }
            }
        });
    }

    private void moveToMeaningActivity(String html, String word, int wordId, int remembered) {
        Intent meaningIntent = new Intent(this, MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        meaningIntent.putExtra("id", wordId);
        meaningIntent.putExtra("remembered", remembered);
        startActivity(meaningIntent);
    }

    private void getBitmapIntent(String filename, Bitmap bmp){
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
            imgTranslate.setImageBitmap(bmp);
            getTextFromImage(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()){
            Toast.makeText(this, "Could not get the text", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            for (int i = 0; i < items.size(); i++){
                TextBlock item = items.valueAt(i);
                String[] words = item.getValue().split(" ");
                for (String word: words) {
//                    String description = databaseAccess.getWords(word).get(0).getDescription();
                    arrCaptureText.add(word);
//                    arrCaptureText.add(word + ": " + description);
                }
            }

        }
    }


    public void saveHistory(final int wordID, final int pUserID){
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(CameraActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(CameraActivity.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0){
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double longtitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    Geocoder geocoder = new Geocoder(CameraActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longtitude, 1);
                        Address obj = addresses.get(0);
                        final String location = obj.getAddressLine(0);

                        final long currentDateTime = System.currentTimeMillis();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                        String dateString = simpleDateFormat.format(currentDateTime);
                        //Nếu có internet và đã login thì add vô server vào local với sync status = success
                        if(Server.haveNetworkConnection(CameraActivity.this) && pUserID > 0){
                            final long insertId = databaseAccess.addHistory(wordID, currentDateTime, pUserID, 0,0, location);
                            String sendDataUrl = Server.SEND_DATA_URL;
                            final RequestQueue requestQueue = Volley.newRequestQueue(CameraActivity.this);

                            //Initial request body
                            JSONArray jsonArray = new JSONArray();
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("table", "searchhistory");
                                JSONArray jsonArray1 = new JSONArray();
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("Id", insertId);
                                jsonObject1.put("IdUser", pUserID);
                                jsonObject1.put("IdWord", wordID);
                                jsonObject1.put("Remembered", 0);
                                jsonObject1.put("Synchronized", 0);
                                jsonObject1.put("TimeSearch", dateString);
                                jsonObject1.put("LinkWeb", "");
                                jsonObject1.put("IsChange", 0);
                                jsonObject1.put("IdServer", 0);
                                jsonArray1.put(jsonObject1);
                                jsonObject.put("data", jsonArray1);
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, sendDataUrl, jsonArray, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    if(response.length() > 0){
                                        try {
                                            JSONObject dataArray = (JSONObject) response.get(0);
                                            Log.d(TAG, "onResponse: " + dataArray);
                                            JSONArray array = (JSONArray) dataArray.get("data");
                                            JSONObject data = (JSONObject) array.get(0);
                                            int idServer = data.getInt("IdServer");
                                            databaseAccess.updateHistoryIdServer(insertId, idServer);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Error: ", error.getMessage());
                                }
                            });
                            requestQueue.add(request);
                        }else{ //Nếu không có internet hoặc chưa login thì add vô local với sync status = fail
                            databaseAccess.addHistory(wordID, System.currentTimeMillis(), 0, 0,0, location);
                            Log.d(TAG, "saveHistory: no internet or no login, add to local");
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }, Looper.getMainLooper());
    }


}
