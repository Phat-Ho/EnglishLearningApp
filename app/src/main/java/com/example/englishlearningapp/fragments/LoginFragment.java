package com.example.englishlearningapp.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.LoginActivity;
import com.example.englishlearningapp.activity.MainHomeActivity;
import com.example.englishlearningapp.activity.RegisterActivity;
import com.example.englishlearningapp.navigation_bottom_fragments.ProfileFragment;
import com.example.englishlearningapp.receiver.NetworkChangeReceiver;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

    MaterialButton btnLogin, btnRegister;
    TextInputEditText txtEmail, txtPassword;
    ProgressBar loginProgressBar;
    TextInputLayout textInputLayoutPassword, textInputLayoutEmail;
    public String LOGIN_URL = Server.LOGIN_URL;
    NetworkChangeReceiver networkChangeReceiver = null;
    LoginManager loginManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        MappingView(view);
        SetUpEvent();
        loginManager = new LoginManager(getActivity());
        return view;
    }

    private void MappingView(View view) {
        btnLogin = view.findViewById(R.id.buttonSignIn);
        btnRegister = view.findViewById(R.id.buttonRegisterLogin);
        txtEmail = view.findViewById(R.id.editTextEmail);
        txtPassword = view.findViewById(R.id.editTextPassword);
        loginProgressBar = view.findViewById(R.id.progressBarLogin);
        textInputLayoutPassword = view.findViewById(R.id.textInputPassword);
        textInputLayoutEmail = view.findViewById(R.id.textInputEmail);
    }

    private void SetUpEvent() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                textInputLayoutPassword.setError(null);
                return false;
            }
        });
    }

    private void Login(){
        final String email = txtEmail.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            textInputLayoutPassword.setError("Hãy nhập email và password");
        }else{
            setLoginProgressBarVisibility(true);
            //Bắt đầu gọi webservice để thực hiện đăng nhập thông qua thư viện Volley
            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String url = LOGIN_URL + "email=" + email + "&password=" + password;
            JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Json Response", response.toString());
                    try {
                        if(response.has("authenticated"))
                        {
                            String authenticated = response.getString("authenticated");
                            if(authenticated.equals("not match")){
                                setLoginProgressBarVisibility(false);
                                textInputLayoutPassword.setError("Sai mật khẩu");
                            }else {
                                if (authenticated.equals("false")){
                                    setLoginProgressBarVisibility(false);
                                    textInputLayoutEmail.setError("Không tìm thấy user");
                                }
                            }
                        }else{
                            int userId = response.getInt("Id");
                            String email = response.getString("Email");
                            String name = response.getString("Name");
                            String number = response.getString("NumberPhone");
                            String dob = response.getString("Birthday");

                            //Gán lại cho login manager
                            loginManager.createUserData(userId, name, email, password, number, dob);

                            Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            //Sync database after login
                            syncDatabaseRemoteToLocal();

                            //Chuyển qua màn hình MainHome
                            navigateToHomeScreen();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "Login Fail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        setLoginProgressBarVisibility(false);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    setLoginProgressBarVisibility(false);
                }
            });
            requestQueue.add(loginRequest);
            //Kết thúc gọi webservice
        }
    }

    private void navigateToHomeScreen(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loginProgressBar.setVisibility(View.GONE);
//                Intent mainIntent = new Intent(getActivity(), MainHomeActivity.class);
//                startActivity(mainIntent);
//                getActivity().finish();
                ProfileFragment profileFragment = new ProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }

    private void syncDatabaseRemoteToLocal(){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver();
        getActivity().registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void setLoginProgressBarVisibility(boolean isVisible){
        if(isVisible){
            loginProgressBar.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
        }else{
            loginProgressBar.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(networkChangeReceiver != null){
            getActivity().unregisterReceiver(networkChangeReceiver);
        }
    }
}
