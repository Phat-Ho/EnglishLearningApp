package com.example.englishlearningapp.navigation_bottom_fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.LoginActivity;
import com.example.englishlearningapp.activity.MainActivity;
import com.example.englishlearningapp.activity.RegisterActivity;
import com.example.englishlearningapp.fragments.LoginFragment;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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
    EditText edtProfileName, edtProfileDOB, edtProfilePhoneNo;
    MaterialButton btnProfileLogout, btnProfileSave;
    ProgressBar progressBarProfile;
    LoginManager loginManager;
    int userId = 0;
    Boolean isLogin = false;
    String REGISTER_URL = Server.REGISTER_URL;

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
                if(loginManager.isLogin()){
                    loginManager.logout();
                    Toast.makeText(getActivity(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    LoginFragment loginFragment = new LoginFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment).commit();
                }else{
                    Toast.makeText(getActivity(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginManager.isLogin()){
                    UpdateUserData();
                }else{
                    Toast.makeText(getActivity(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtProfileDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDateOfBirth();
            }
        });
    }

    private void UpdateUserData() {
        final String name = edtProfileName.getText().toString();
        final String phoneNo = edtProfilePhoneNo.getText().toString();
        final String dob = edtProfileDOB.getText().toString();
        String email = loginManager.getUserEmail();
        String password = loginManager.getUserPassword();

        final JSONObject parameter = new JSONObject();
        try {
            parameter.put("Id", loginManager.getUserId());
            parameter.put("Name", name);
            parameter.put("Email", email);
            parameter.put("Password", password);
            parameter.put("NumberPhone", phoneNo);
            parameter.put("Birthday", dob);
        } catch (JSONException error){
            Log.d(TAG, "Register Json error: " + error.getMessage());
        }

        setProfileProgressBarVisibility(true);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest registerRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+ response);
                int userId = Integer.parseInt(response);
                Log.d(TAG, "onResponse: " + response);
                if (userId > 0) {
                    //Save user info to login manager
                    loginManager.setUserName(name);
                    loginManager.setUserPhoneNo(phoneNo);
                    loginManager.setUserDob(dob);
                    //Delay progress bar 2 seconds
                    setProgressBarDelay(2000, "Lưu thành công");
                }else{
                    setProgressBarDelay(2000, "Đã xảy ra lỗi");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setProgressBarDelay(2000, "Đã xảy ra lỗi");
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return parameter.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(registerRequest);
    }

    private void inputDateOfBirth() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, year, month, day);
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackground(null);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackground(null);
    }

    public DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date;
            month++;
            if(month<10 && dayOfMonth <10){
                date = year + "-0" + month + "-0" + dayOfMonth;
            }else if(month<10){
                date = year + "-0" + month + "-" + dayOfMonth;
            }else if(dayOfMonth<10){
                date = year + "-" + month + "-0" + dayOfMonth;
            }else{
                date = year + "-" + month + "-" + dayOfMonth;
            }
            edtProfileDOB.setText(date);
        }
    };

    private void SetUserData() {
        isLogin = loginManager.isLogin();
        String userName;
        String userEmail;
        String dob;
        String userPhoneNo;


        if(isLogin){
            userId = loginManager.getUserId();
            userName = loginManager.getUserName();
            userEmail = loginManager.getUserEmail();
            userPhoneNo = loginManager.getUserPhoneNo();
            dob = formatDate(loginManager.getUserDob());
            txtProfileEmail.setText(userEmail);
            edtProfileName.setText(userName);
            edtProfilePhoneNo.setText(userPhoneNo);
            edtProfileDOB.setText(dob);
        }
    }

    private String formatDate(String datetime){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date!=null){
            return dateFormat.format(date);
        }else{
            return null;
        }
    }

    private void setProfileProgressBarVisibility(boolean isVisible){
        if(isVisible){
            progressBarProfile.setVisibility(View.VISIBLE);
            btnProfileSave.setVisibility(View.GONE);
            btnProfileLogout.setVisibility(View.GONE);
        }else{
            progressBarProfile.setVisibility(View.GONE);
            btnProfileSave.setVisibility(View.VISIBLE);
            btnProfileLogout.setVisibility(View.VISIBLE);
        }
    }

    private void setProgressBarDelay(int milliSecond, final String message){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setProfileProgressBarVisibility(false);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, milliSecond);
    }

    private void MappingView(View view) {
        txtProfileEmail = view.findViewById(R.id.profile_edt_email);
        edtProfileName = view.findViewById(R.id.profile_edt_name);
        edtProfileDOB = view.findViewById(R.id.profile_edt_dob);
        edtProfilePhoneNo = view.findViewById(R.id.profile_edt_number);
        btnProfileLogout = view.findViewById(R.id.profile_btn_logout);
        btnProfileSave = view.findViewById(R.id.profile_btn_save);
        progressBarProfile = view.findViewById(R.id.profile_progress_bar);
    }
}
