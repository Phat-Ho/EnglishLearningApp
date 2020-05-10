package com.example.englishlearningapp.navigation_bottom_fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "SearchFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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

    AutoCompleteTextView edtSearch;
    ListView lvTranslatedWords;
    ArrayList<Word> words;
    ArrayList<Word> completeWordsData;
    DatabaseAccess databaseAccess;
    ArrayAdapter adapter;
    SharedPreferences loginPref;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //edtSearch = view.findViewById(R.id.editTextSearch);
        //lvTranslatedWords = view.findViewById(R.id.listViewTranslatedWords);
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        loginPref = getActivity().getSharedPreferences("loginState", Context.MODE_PRIVATE);
        final int userID = loginPref.getInt("userID", 0);
        final boolean isLogin = loginPref.getBoolean("isLogin", false);
        loadDatabase(edtSearch.getText().toString().trim());
        lvTranslatedWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Word> historyWords = databaseAccess.getHistoryWordsWithoutDuplicateSortByAZ();
                boolean isSaved = false;

                String html = completeWordsData.get(position).getHtml();
                int remembered = completeWordsData.get(position).getRemembered();

                for (int i = 0; i < historyWords.size(); i++) {
                    if(completeWordsData.get(position).getId() == historyWords.get(i).getId()){
                        isSaved = true;
                        remembered = historyWords.get(i).getRemembered();
                        Log.d(TAG, "Word is saved");
                        break;
                    }
                }
                if(isSaved == false){
                    saveHistory(completeWordsData.get(position).getId(), isLogin, userID);
                }
                if (remembered == 1){
                    moveToMeaningActivity(html,
                            completeWordsData.get(position).getWord(),
                            completeWordsData.get(position).getId(),
                            1);
                } else {
                    moveToMeaningActivity(html,
                            completeWordsData.get(position).getWord(),
                            completeWordsData.get(position).getId(),
                            0);
                }

                hideSoftKeyBoard();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadDatabase(edtSearch.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    public void saveHistory(final int wordID, boolean pIsLogin, final int pUserID){
        //Nếu có internet và đã login thì add vô server vào local với sync status = success
        if(Server.haveNetworkConnection(getActivity()) && pIsLogin == true){
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
                            Log.d(TAG, "onResponse: added to server");
                        }else{
                            databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC, currentDateTime);
                            Log.d(TAG, "onResponse: " + message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
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
            Log.d(TAG, "saveHistory: no internet or no login, add to local");
        }
    }

    private void moveToMeaningActivity(String html, String word, int id, int remembered) {
        Intent meaningIntent = new Intent(getActivity(), MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        meaningIntent.putExtra("id", id);
        meaningIntent.putExtra("remembered", remembered);
        startActivity(meaningIntent);
    }

    private void loadDatabase(String word) {
        if (word.equals("")) {
            completeWordsData = databaseAccess.getWords(word);
            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, completeWordsData);
            lvTranslatedWords.setAdapter(adapter);
        } else {
            completeWordsData = databaseAccess.getWords(word);
            adapter.clear();
            adapter.addAll(completeWordsData);
            adapter.notifyDataSetChanged();
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