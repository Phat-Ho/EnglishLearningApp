package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    MaterialButton btnLogin, btnRegister;
    TextInputEditText txtEmail, txtPassword;
    ProgressBar loginProgressBar;
    TextInputLayout textInputLayout;
    public static String email = "";
    public static int userID = -1;
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
                textInputLayout.setError(null);
                return false;
            }
        });
    }

    private void Login(){
        final String email = txtEmail.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            textInputLayout.setError("Hãy nhập email và password");
        }else{
            loginProgressBar.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            //Bắt đầu gọi webservice để thực hiện đăng nhập thông qua thư viện Volley
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Json Response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if(success.equals("true")){
                            int userID = jsonObject.getInt("userID");
                            String email = jsonObject.getString("email");

                            //Gán lại cho login manager
                            loginManager.createUserData(null, email, userID);

                            //Sync database after login
                            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                            networkChangeReceiver = new NetworkChangeReceiver();
                            registerReceiver(networkChangeReceiver, intentFilter);

                            //Chuyển qua màn hình MainHome
                            navigateToHomeScreen();
                        }else{
                            loginProgressBar.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                            textInputLayout.setError("Sai toàn khoản hoặc mật khẩu");
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Login Fail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        loginProgressBar.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);
                        btnRegister.setVisibility(View.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    loginProgressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    btnRegister.setVisibility(View.VISIBLE);
                }
            }){
                //Truyền params cho url qua phương thức POST
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);

                    return params;
                }
            };
            requestQueue.add(stringRequest);
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
        textInputLayout = findViewById(R.id.login_password_text_input);
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

}
