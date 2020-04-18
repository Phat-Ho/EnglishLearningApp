package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.receiver.NetworkChangeReceiver;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    MaterialButton btnLogin, btnRegister;
    TextInputEditText txtEmail, txtPassword;
    ProgressBar loginProgressBar;
    TextInputLayout textInputLayoutPassword, textInputLayoutEmail;
    public String LOGIN_URL = Server.LOGIN_URL;
    NetworkChangeReceiver networkChangeReceiver = null;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MappingView();
        SetUpEvent();
        loginManager = new LoginManager(this);
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
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
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
        Log.d(TAG, "email: " + email);
        final String password = txtPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            textInputLayoutPassword.setError("Hãy nhập email và password");
        }else{
            setLoginProgressBarVisibility(true);
            //Bắt đầu gọi webservice để thực hiện đăng nhập thông qua thư viện Volley
            final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
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

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            //Sync database after login
                            syncDatabaseRemoteToLocal();

                            //Chuyển qua màn hình MainHome
                            navigateToHomeScreen();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Login Fail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        setLoginProgressBarVisibility(false);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    setLoginProgressBarVisibility(false);
                }
            });
            requestQueue.add(loginRequest);
            //Kết thúc gọi webservice
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(networkChangeReceiver != null){
            unregisterReceiver(networkChangeReceiver);
        }

    }

    private void MappingView() {
        btnLogin = findViewById(R.id.login_next_button);
        btnRegister = findViewById(R.id.login_register_button);
        txtEmail = findViewById(R.id.login_email_text);
        txtPassword = findViewById(R.id.login_password_text);
        loginProgressBar = findViewById(R.id.login_progressbar);
        textInputLayoutPassword = findViewById(R.id.login_password_text_input);
        textInputLayoutEmail = findViewById(R.id.login_email_text_input);
    }

    private void navigateToHomeScreen(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loginProgressBar.setVisibility(View.GONE);
                Intent mainIntent = new Intent(LoginActivity.this, MainHomeActivity.class);
                startActivity(mainIntent);
                finish();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }

    private void syncDatabaseRemoteToLocal(){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
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
}
