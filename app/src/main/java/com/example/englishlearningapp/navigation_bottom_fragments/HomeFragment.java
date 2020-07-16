package com.example.englishlearningapp.navigation_bottom_fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.ConnetedWordActivity;
import com.example.englishlearningapp.activity.MainHomeActivity;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.adapters.HomeGridViewAdapter;
import com.example.englishlearningapp.fragments.LoginFragment;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.GridSpacingItemDecoration;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HomeFragment";
    private static final int REQUEST_CODE_LOCATION = 1;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    }

    AutoCompleteTextView txtMeaningSearch;
    DatabaseAccess databaseAccess;
    LoginManager loginManager;
    HomeGridViewAdapter adapter;
    ImageButton imgBtnGame;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList subjectNames = new ArrayList<>(Arrays.asList(getResources().getString(R.string.subject_learning),
                getResources().getString(R.string.vietnamese),
                getResources().getString(R.string.look_up_camera)));
        ArrayList subjectImages = new ArrayList<>(Arrays.asList(R.drawable.img_topic, R.drawable.img_vie_eng, R.drawable.img_camera));
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        txtMeaningSearch = view.findViewById(R.id.meaning_auto_complete_search_box_home);
        databaseAccess = DatabaseAccess.getInstance(getContext());
        SetAutoCompleteSearchBox();
        loginManager = new LoginManager(getContext());
        recyclerView = view.findViewById(R.id.recylerViewHome);
        recyclerView.setNestedScrollingEnabled(false);
        imgBtnGame = view.findViewById(R.id.img_btn_game);
        layoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);


        int spanCount = 1;
        int spacing = 0;
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        adapter = new HomeGridViewAdapter(getActivity(), subjectImages, subjectNames);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgBtnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginManager.isLogin()){
                    Intent connectedWordIntent = new Intent(getActivity(), ConnetedWordActivity.class);
                    if(getActivity() != null) {
                        getActivity().startActivity(connectedWordIntent);
                    }else{
                        showAlert("Đã xảy ra lỗi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                }else{
                    showAlert("Đăng nhập để có thể chơi game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }
            }
        });
    }

    private void SetAutoCompleteSearchBox() {
        final ArrayAdapter searchBoxAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1);
        txtMeaningSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Word> wordList = databaseAccess.getWords(s.toString());
                searchBoxAdapter.clear();
                searchBoxAdapter.addAll(wordList);
                txtMeaningSearch.setAdapter(searchBoxAdapter);
                txtMeaningSearch.setThreshold(1);
                searchBoxAdapter.notifyDataSetChanged();
                if (wordList.isEmpty()){
                    showAlert(getResources().getText(R.string.no_result_found).toString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtMeaningSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtMeaningSearch.showDropDown();
            }
        });

        txtMeaningSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Word> word = databaseAccess.getWords(txtMeaningSearch.getText().toString());
                String wordHeader = word.get(0).getWord();
                String wordHtml = word.get(0).getHtml();
                int wordId = word.get(0).getId();
                String youtubeLink = databaseAccess.getWordsById(wordId).getYoutubeLink();
                saveHistory(word.get(0).getId(), loginManager.getUserId());
                RefreshScreen(wordHeader, wordHtml, wordId, youtubeLink);
                hideSoftKeyBoard();
            }
        });
    }

    private void RefreshScreen(String word, String html, int wordId, String youtubeLink) {
        Intent intent = new Intent(getContext(), MeaningActivity.class);
        intent.putExtra("word", word);
        intent.putExtra("html", html);
        intent.putExtra("id", wordId);
        intent.putExtra("youtubeLink", youtubeLink);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveHistory(final int wordID, final int pUserID){
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            final long currentDateTime = System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            String dateString = simpleDateFormat.format(currentDateTime);
            //Nếu có internet và đã login thì add vô server vào local với sync status = success
            if(Server.haveNetworkConnection(getActivity()) && pUserID > 0){
                final long insertId = databaseAccess.addHistory(wordID, currentDateTime, pUserID, 0,0);
                String sendDataUrl = Server.SEND_DATA_URL;
                final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

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
                        if(error != null){
                            Log.e("Error: ", error.getMessage() == null ? "null pointer" : error.getMessage());
                        }

                    }
                });
                requestQueue.add(request);
            }else{ //Nếu không có internet hoặc chưa login thì add vô local với sync status = fail
                databaseAccess.addHistory(wordID, System.currentTimeMillis(), 0, 0,0);
            }
        } else {
            LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(getActivity() != null ? getActivity() : requireContext()).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        double longtitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        getAddress(latitude, longtitude, wordID, pUserID);
                    }
                }
            }, Looper.getMainLooper());
        }
    }

    public void getAddress(double lat, double lng, int wordID, int pUserID) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String location = obj.getAddressLine(0);
            final long currentDateTime = System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            String dateString = simpleDateFormat.format(currentDateTime);
            //Nếu có internet và đã login thì add vô server vào local với sync status = success
            if(Server.haveNetworkConnection(getActivity()) && pUserID > 0){
                final long insertId = databaseAccess.addHistory(wordID, currentDateTime, pUserID, 0,0, location);
                String sendDataUrl = Server.SEND_DATA_URL;
                final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

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
                        if(error != null){
                            Log.e("Error: ", error.getMessage() == null ? "null pointer" : error.getMessage());
                        }

                    }
                });
                requestQueue.add(request);
            }else{ //Nếu không có internet hoặc chưa login thì add vô local với sync status = fail
                databaseAccess.addHistory(wordID, System.currentTimeMillis(), 0, 0,0, location);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void showAlert(String title, DialogInterface.OnClickListener listener){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity() != null ? getActivity() : requireContext());
        builder.setTitle(title);
        builder.setPositiveButton("OK", listener);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BottomNavigationView)getActivity().findViewById(R.id.navigation_bottom)).setSelectedItemId(R.id.navigation_profile);
                dialog.dismiss();
            }
        });
    }

    private void hideSoftKeyBoard() {
        View v = getActivity().getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == HomeGridViewAdapter.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }
}
