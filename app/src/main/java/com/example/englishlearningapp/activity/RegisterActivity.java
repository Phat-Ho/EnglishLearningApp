package com.example.englishlearningapp.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "Register Activity";
    TextInputEditText registerEmail;
    TextInputEditText registerPassword;
    MaterialButton registerButton, btnLoginWithAccount;
    ProgressBar registerProgressBar;
    TextInputLayout registerTextInputPassword, registerTextInputEmail;
    String REGISTER_URL = Server.REGISTER_URL;
    String CHECK_USER_URL = Server.CHECK_USER_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(RegisterActivity.this);
        setContentView(R.layout.activity_register);
        MappingView();
        HandleEvent();
    }

    private void HandleEvent() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
        registerPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(isValidPassword(registerPassword.getText().toString())){
                    registerTextInputPassword.setError(null);
                }
                return false;
            }
        });

        registerEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(isValidEmail(registerEmail.getText().toString())){
                    registerTextInputEmail.setError(null);
                }
                return false;
            }
        });

        btnLoginWithAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MappingView() {
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);
        registerProgressBar = findViewById(R.id.register_progressbar);
        registerTextInputPassword = findViewById(R.id.register_text_input);
        registerTextInputEmail = findViewById(R.id.register_text_layout_email);
        btnLoginWithAccount = findViewById(R.id.buttonLoginWithAccount);
    }

    private void Register() {
        final String email = registerEmail.getText().toString().trim();
        final String password = registerPassword.getText().toString().trim();

        //Kiểm chứng email và password
        if(!isValidEmail(email)){
            registerTextInputEmail.setError("Email không hợp lệ");
        }else{
            if(!isValidPassword(password)){
                registerTextInputPassword.setError("Password phải nhiều hơn 8 kí tự, gôm cả chữ và số");
            }else{
                registerProgressBar.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.GONE);
                registerUser(email, password);
            }
        }
    }

    private boolean isValidEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private boolean isValidPassword(String password){
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$";
        Pattern pat = Pattern.compile(passwordRegex);
        if(password.length() < 8){
            return false;
        }
        return pat.matcher(password).matches();
    }

    private void registerUser(String email, String password){
        String url = CHECK_USER_URL + "email="+email;
        Log.d(TAG, "registerUser: " + url);
        final JSONObject parameter = new JSONObject();
        try {
            parameter.put("Id", 0);
            parameter.put("Email", email);
            parameter.put("Password", password);
        } catch (JSONException error){
            Log.d(TAG, "Register Json error: " + error.getMessage());
        }
        final RequestQueue checkUserRequestQueue = Volley.newRequestQueue(RegisterActivity.this);
        JsonObjectRequest checkUserRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("exist")){
                        registerTextInputEmail.setError("Đã tồn tại user");
                        registerProgressBar.setVisibility(View.GONE);
                        registerButton.setVisibility(View.VISIBLE);
                    }else{
                        RequestQueue stringRequestQueue = Volley.newRequestQueue(RegisterActivity.this);
                        StringRequest registerRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                int userId = Integer.parseInt(response);
                                Log.d(TAG, "onResponse: " + response);
                                if (userId > 0) {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
                                    alertDialog.setTitle("Đăng kí thành công");
                                    alertDialog.setMessage("Chuyển qua màn hình đăng nhập");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    alertDialog.show();
                                }
                                registerProgressBar.setVisibility(View.GONE);
                                registerButton.setVisibility(View.VISIBLE);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                                registerProgressBar.setVisibility(View.GONE);
                                registerButton.setVisibility(View.VISIBLE);
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
                        stringRequestQueue.add(registerRequest);
                    }
                } catch (JSONException e) {
                    registerProgressBar.setVisibility(View.GONE);
                    registerButton.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                registerProgressBar.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                Log.d(TAG, "onErrorResponse: check user error: " + error.getMessage());
            }
        });
        checkUserRequestQueue.add(checkUserRequest);
    }
}
