package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText registerEmail;
    TextInputEditText registerPassword;
    MaterialButton registerButton;
    ProgressBar registerProgressBar;
    String REGISTER_URL = "http://192.168.1.62/android/register.php";

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
    }

    private void MappingView() {
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);
        registerProgressBar = findViewById(R.id.register_progressbar);
    }

    private void Register(){
        registerProgressBar.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);

        final String email = registerEmail.getText().toString().trim();
        final String password = registerPassword.getText().toString().trim();

        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Json Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if(success.equals("true")){
                        Toast.makeText(RegisterActivity.this, "Register Success!", Toast.LENGTH_SHORT).show();
                    }

                    registerProgressBar.setVisibility(View.GONE);
                    registerButton.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

}
