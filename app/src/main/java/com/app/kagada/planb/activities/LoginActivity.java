package com.app.kagada.planb.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.APIClient;
import com.app.kagada.planb.networks.APIInterface;
import com.app.kagada.planb.networks.LoginResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    SharedPreferences pref;
    public static String authtoken="";
    public String fcmtoken="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("myToken", Context.MODE_PRIVATE);
        editTextUsername = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        findViewById(R.id.buttonLogin).setOnClickListener(this);
        findViewById(R.id.textViewRegister).setOnClickListener(this);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM fail", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        fcmtoken = task.getResult().getToken();
                    }
                });
    }

    private void userLogin() {

        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }

        if (!username.matches("^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{0,29}$")) {
            editTextUsername.setError("Enter a valid username");
            editTextUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("username",username);
        params.put("password", password);
        params.put("token", fcmtoken);

        apiInterface.loginUser(params).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.code()==200) {
                    LoginResponse lr = response.body();
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("token", lr.getToken());
                    editor.putString("username", lr.getUserResponse().getEmail());
                    editor.putString("phone",lr.getUserResponse().getPhone());
                    editor.putString("img_url", lr.getUserResponse().getImgUrl());
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    authtoken = lr.getToken();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else if (response.code()==400) {
                    Toast.makeText(LoginActivity.this, "Login Unsuccessful, Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                userLogin();
                break;
            case R.id.textViewRegister:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }
}
