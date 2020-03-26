package com.example.englishlearningapp.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.englishlearningapp.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeaningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeaningFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MeaningFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeaningFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeaningFragment newInstance(String param1, String param2) {
        MeaningFragment fragment = new MeaningFragment();
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

    TextView txtMeaning;
    ImageButton imgBtnPronounce;
    TextToSpeech tts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meaning, container, false);

        txtMeaning = view.findViewById(R.id.textViewMeaning);
        imgBtnPronounce = view.findViewById(R.id.imageButtonPronounce);
        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        String html = getArguments().getString("html");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtMeaning.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtMeaning.setText(Html.fromHtml(html));
        }

        final String word = getArguments().getString("word");
        imgBtnPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "");
            }
        });
        return view;
    }
}
