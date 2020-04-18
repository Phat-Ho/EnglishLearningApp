package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
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
    MaterialButton registerButton;
    ProgressBar registerProgressBar;
    TextInputLayout registerTextInputPassword, registerTextInputEmail;
    String REGISTER_URL = Server.REGISTER_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                if(registerPassword.getText().toString().length() >= 8){
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
    }

    private void MappingView() {
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);
        registerProgressBar = findViewById(R.id.register_progressbar);
        registerTextInputPassword = findViewById(R.id.register_text_input);
        registerTextInputEmail = findViewById(R.id.register_text_layout_email);
    }

    private void Register() {
        final String email = registerEmail.getText().toString().trim();
        final String password = registerPassword.getText().toString().trim();

        final JSONObject parameter = new JSONObject();
        try {
            parameter.put("Id", 0);
            parameter.put("Email", email);
            parameter.put("Password", password);
        } catch (JSONException error){
            Log.d(TAG, "Register Json error: " + error.getMessage());
        }

        //Kiểm chứng email và password
        if(!isValidEmail(email)){
            registerTextInputEmail.setError("Email không hợp lệ");
        }else{
            if(password.length() < 8){
                registerTextInputPassword.setError("Password phải nhiều hơn 8 kí tự");
            }else{
                registerProgressBar.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.GONE);
                //Bắt đầu gọi webservice để thực hiện đăng kí thông qua thư viện Volley
                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
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
                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginIntent);
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
                requestQueue.add(registerRequest);
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
}
