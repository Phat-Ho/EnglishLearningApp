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
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText registerEmail;
    TextInputEditText registerPassword;
    MaterialButton registerButton;
    ProgressBar registerProgressBar;
    TextInputLayout registerTextInputPassword;
    String REGISTER_URL = Server.REGISTER_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MappingView();
        OnClickEvent();
    }

    private void OnClickEvent() {
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
    }

    private void MappingView() {
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);
        registerProgressBar = findViewById(R.id.register_progressbar);
        registerTextInputPassword = findViewById(R.id.register_text_input);
    }

    private void Register(){
        final String email = registerEmail.getText().toString().trim();
        final String password = registerPassword.getText().toString().trim();

        //Kiểm chứng email và password
        if(email.isEmpty()){
            registerEmail.setError("Hãy nhập email!");
        }else{
            if(password.length() < 8){
                registerTextInputPassword.setError("Password phải nhiều hơn 8 kí tự");
            }else{
                registerProgressBar.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.GONE);
                //Bắt đầu gọi webservice để thực hiện đăng kí thông qua thư viện Volley
                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Json Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if(success.equals("true")){
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
                            }else{
                                if(message.equals("email existence")){
                                    registerEmail.setError("Đã tồn tại email!");
                                }
                            }
                            registerProgressBar.setVisibility(View.GONE);
                            registerButton.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            Log.d("Register Fail: ", e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Register Fail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            registerProgressBar.setVisibility(View.GONE);
                            registerButton.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        registerProgressBar.setVisibility(View.GONE);
                        registerButton.setVisibility(View.VISIBLE);
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
    }
}
