package com.example.englishlearningapp.navigation_bottom_fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.CameraActivity;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.adapters.HomeGridViewAdapter;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList subjectNames = new ArrayList<>(Arrays.asList(getResources().getString(R.string.subject_learning),
                getResources().getString(R.string.vietnamese),
                "Camera"));
        ArrayList subjectImages = new ArrayList<>(Arrays.asList(R.drawable.home_ic_1, R.drawable.home_ic_2, R.drawable.home_ic_3));
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        txtMeaningSearch = view.findViewById(R.id.meaning_auto_complete_search_box_home);
        databaseAccess = DatabaseAccess.getInstance(getContext());
        SetAutoCompleteSearchBox();
        loginManager = new LoginManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recylerViewHome);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new HomeGridViewAdapter(getActivity(), subjectNames, subjectImages, this);

        recyclerView.setAdapter(adapter);

        return view;
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
                    Toast.makeText(getActivity(), getResources().getText(R.string.no_result_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtMeaningSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Word> word = databaseAccess.getWords(txtMeaningSearch.getText().toString());
                String wordHeader = word.get(0).getWord();
                String wordHtml = word.get(0).getHtml();
                int wordId = word.get(0).getId();
                saveHistory(word.get(0).getId(), loginManager.getUserId());
                RefreshScreen(wordHeader, wordHtml, wordId);
                hideSoftKeyBoard();
            }
        });
    }

    private void RefreshScreen(String word, String html, int wordId) {
        Intent intent = new Intent(getContext(), MeaningActivity.class);
        intent.putExtra("word", word);
        intent.putExtra("html", html);
        intent.putExtra("id", wordId);
        startActivity(intent);
    }

    public void saveHistory(final int wordID, final int pUserID){
        //Nếu có internet và đã login thì add vô server vào local với sync status = success
        if(Server.haveNetworkConnection(getContext()) && pUserID > 0){
            final long currentDateTime = System.currentTimeMillis();
            String url = Server.ADD_HISTORY_URL;
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        if(message.equals("success")){
                            databaseAccess.addHistory(wordID, DatabaseContract.SYNC, currentDateTime);
                        }else{
                            databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC, currentDateTime);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC, currentDateTime);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("userid", String.valueOf(pUserID));
                    params.put("wordid", String.valueOf(wordID));
                    params.put("datetime", String.valueOf(currentDateTime));
                    params.put("sync", String.valueOf(DatabaseContract.SYNC));

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }else{ //Nếu không có internet hoặc chưa login thì add vô local với sync status = fail
            databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC, System.currentTimeMillis());
        }
    }

    public boolean isHistoryExistence(int wordId){
        Word word = databaseAccess.getHistoryWordById(wordId);
        if(word.getId() > 0){
            return true;
        }else{
            return false;
        }
    }

    public String getDatetime(){
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void hideSoftKeyBoard() {
        View v = getActivity().getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
