package com.example.englishlearningapp.navigation_bottom_fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.activity.RegisterActivity;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        edtSearch = view.findViewById(R.id.editTextSearch);
        lvTranslatedWords = view.findViewById(R.id.listViewTranslatedWords);
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        words = new ArrayList<>();

        loadDatabase(edtSearch.getText().toString().trim());

        lvTranslatedWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Word> historyWords = databaseAccess.getHistoryWords();
                boolean isSaved = false;
                for (int i = 0; i < historyWords.size(); i++) {
                    if(completeWordsData.get(position).getId() == historyWords.get(i).getId()){
                        isSaved = true;
                        Log.d(TAG, "Word is saved");
                        break;
                    }
                }
                if(isSaved == false){
                    saveHistory(completeWordsData.get(position).getId());
                }
                moveToMeaningActivity(completeWordsData.get(position).getHtml(), completeWordsData.get(position).getWord());
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

    public void saveHistory(final int wordID){
        //Nếu có internet thì add vô server và local với sync status = success
        if(Server.haveNetworkConnection(getActivity())){
            String url = Server.ADD_HISTORY_URL + "userid=0&wordid=" + wordID;
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        if(message.equals("success")){
                            databaseAccess.addHistory(wordID, DatabaseContract.SYNC);
                            Toast.makeText(getActivity(), "Add to server", Toast.LENGTH_SHORT).show();
                        }else{
                            databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC);
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
                    databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC);
                }
            });
            requestQueue.add(stringRequest);
        }else{ //Nếu không có internet thì add vô local với sync status = fail
            databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC);
            Log.d(TAG, "saveHistory: no internet, add to local");
        }
    }

    private void moveToMeaningActivity(String html, String word) {
        Intent meaningIntent = new Intent(getActivity(), MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        startActivity(meaningIntent);
    }

    private void loadDatabase(String word) {
        /*databaseAccess.open();*/
        if (word.equals("")) {
            completeWordsData = databaseAccess.getWords(word);
            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, completeWordsData);
            lvTranslatedWords.setAdapter(adapter);
        } else {
            completeWordsData = databaseAccess.getWords(word);
            words = completeWordsData;
            adapter.clear();
            adapter.addAll(completeWordsData);
            adapter.notifyDataSetChanged();
        }
    }

    private void hideSoftKeyBoard() {
        View v = getActivity().getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}