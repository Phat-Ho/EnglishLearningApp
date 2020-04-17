package com.example.englishlearningapp.navigation_bottom_fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.LoginActivity;
import com.example.englishlearningapp.activity.MainActivity;
import com.example.englishlearningapp.utils.LoginManager;
import com.google.android.material.button.MaterialButton;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProfileFragment";

    TextView txtProfileEmail;
    EditText edtProfileName;
    MaterialButton btnProfileLogout, btnProfileSave;
    LoginManager loginManager;
    int userId = 0;
    Boolean isLogin = false;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        loginManager = new LoginManager(getActivity());
        Log.d(TAG, "isLogin: " + loginManager.isLogin());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        MappingView(view);
        SetUserData();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HandleButtonEvent();
    }

    private void HandleButtonEvent() {
        btnProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginManager.logout();
                Toast.makeText(getActivity(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void SetUserData() {
        isLogin = loginManager.isLogin();
        String userName = "";
        String userEmail = "";

        if(isLogin){
            userId = loginManager.getUserId();
            userName = loginManager.getUserName();
            userEmail = loginManager.getUserEmail();
            txtProfileEmail.setText(userEmail);
            edtProfileName.setText(userName);
        }
    }

    private void MappingView(View view) {
        txtProfileEmail = view.findViewById(R.id.profile_edt_email);
        edtProfileName = view.findViewById(R.id.profile_edt_name);
        btnProfileLogout = view.findViewById(R.id.profile_btn_logout);
        btnProfileSave = view.findViewById(R.id.profile_btn_save);
    }
}
