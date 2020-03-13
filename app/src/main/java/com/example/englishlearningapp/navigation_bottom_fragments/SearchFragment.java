package com.example.englishlearningapp.navigation_bottom_fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;


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

    EditText edtSearch;
    ListView lvTranslatedWords;
    ProgressBar pgBarTranslate;
    String url = "https://translate.yandex.net/api/v1.5/tr.json/translate";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        edtSearch = view.findViewById(R.id.editTextSearch);
        pgBarTranslate = view.findViewById(R.id.progressBarTranslate);
        lvTranslatedWords = view.findViewById(R.id.listViewTranslatedWords);

        pgBarTranslate.setVisibility(View.GONE);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getTranslatedWord();
                    hideSoftKeyBoard();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void getTranslatedWord(){
        pgBarTranslate.setVisibility(View.VISIBLE);
        final String key = "trnsl.1.1.20200311T182743Z.a0f2a10aa284e86b.503d45acafbe1fe322301fb49be3039cafbb1fa5";
        final String text = edtSearch.getText().toString().trim();
        final String lang = "vi";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final ArrayList<String> textArray = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray textJsonArray = jsonObject.getJSONArray("text");
                            for (int i = 0; i < textJsonArray.length(); i++){
                                String translatedWord = textJsonArray.getString(i);
                                textArray.add(translatedWord);
                            }
                            pgBarTranslate.setVisibility(View.GONE);
                            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, textArray);
                            lvTranslatedWords.setAdapter(adapter);
                            lvTranslatedWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(getActivity(), "You choose " + textArray.get(position), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("AAA", error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("key", key);
                params.put("text", text);
                params.put("lang", lang);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void hideSoftKeyBoard() {
        View v = getActivity().getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        }
    }
}
