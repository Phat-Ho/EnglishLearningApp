package com.example.englishlearningapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.RegisterActivity;
import com.example.englishlearningapp.navigation_bottom_fragments.ProfileFragment;
import com.example.englishlearningapp.receiver.NetworkChangeReceiver;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


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
    private static final String TAG = "LoginFragment";
    MaterialButton btnLogin, btnRegister, btnForgotPassword;
    TextInputEditText txtEmail, txtPassword;
    ProgressBar loginProgressBar;
    TextInputLayout textInputLayoutPassword, textInputLayoutEmail;
    public String LOGIN_URL = Server.LOGIN_URL;
    NetworkChangeReceiver networkChangeReceiver = null;
    LoginManager loginManager;
    DatabaseAccess databaseAccess;
    boolean isRegistered = false;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        MappingView(view);
        SetUpEvent();
        databaseAccess = DatabaseAccess.getInstance(getActivity());
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
        btnForgotPassword = view.findViewById(R.id.buttonForgotPassword);
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

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, forgotPasswordFragment).addToBackStack(null).commit();
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void Login(){
        final String email = txtEmail.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();

        if(!isValidEmail(email)){
            textInputLayoutEmail.setError(getString(R.string.invalid_email));
            if(email.isEmpty()){
                textInputLayoutEmail.setError(getString(R.string.please_enter_email));
            }
        } else if (password.isEmpty()){
            textInputLayoutPassword.setError(getString(R.string.please_enter_password));
        } else{
            textInputLayoutEmail.setErrorEnabled(false);
            textInputLayoutPassword.setErrorEnabled(false);
            setLoginProgressBarVisibility(true);
            //Bắt đầu gọi webservice để thực hiện đăng nhập thông qua thư viện Volley
            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String url = LOGIN_URL + "email=" + email + "&password=" + password;
            JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("login Response", response.toString());
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
                            String temp = response.getString("Birthday");
                            String dob = "";
                            if(!temp.equals("null")){
                                dob = temp;
                            }

                            //Gán lại cho login manager
                            loginManager.createUserData(userId, name, email, password, number, dob);

                            //Sync database after login
                            syncDatabaseRemoteToLocal();

                            //Update database with userId
                            updateDatabaseWithUserId(userId);

                            //Chuyển qua màn hình profile
                            navigateToProfileScreen();
                        }
                    } catch (JSONException e) {
                        showAlert("Đăng nhập thất bại", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        setLoginProgressBarVisibility(false);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error != null){
                        Toast.makeText(getActivity(), "Error: " + (error.getMessage() == null ? "null pointer" : error.getMessage()), Toast.LENGTH_SHORT).show();
                        setLoginProgressBarVisibility(false);
                    }
                }
            });
            requestQueue.add(loginRequest);
            //Kết thúc gọi webservice
        }
    }

    private void updateDatabaseWithUserId(int idUser) {
        databaseAccess.updateHistoryIdUser(idUser);
    }

    private void navigateToProfileScreen(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loginProgressBar.setVisibility(View.GONE);
                showAlert("Đăng nhập thành công",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProfileFragment profileFragment = new ProfileFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                    }
                });
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }

    private void syncDatabaseRemoteToLocal(){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver();
        if(getActivity() != null){
            getActivity().registerReceiver(networkChangeReceiver, intentFilter);
        }else{
            requireContext().registerReceiver(networkChangeReceiver, intentFilter);
        }
        isRegistered = true;
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

    private void showAlert(String title, DialogInterface.OnClickListener listener){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity() != null ? getActivity() : requireContext());
        builder.setTitle(title);
        builder.setPositiveButton("OK", listener);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(networkChangeReceiver != null && isRegistered){
            if(getActivity() != null){
                getActivity().unregisterReceiver(networkChangeReceiver);
            }else{
                requireContext().unregisterReceiver(networkChangeReceiver);
            }
            networkChangeReceiver = null;
            isRegistered = false;
        }
        txtEmail.setText("");
        txtPassword.setText("");
    }
}
