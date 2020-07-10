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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


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
                    saveHistory(completeWordsData.get(position).getId(), userID);
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

    public void saveHistory(final int wordID, final int pUserID){
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
            databaseAccess.addHistory(wordID, System.currentTimeMillis(), 0, 0,0);
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