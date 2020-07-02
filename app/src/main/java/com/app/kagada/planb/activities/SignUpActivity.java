package com.app.kagada.planb.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.APIClient;
import com.app.kagada.planb.networks.APIInterface;
import com.app.kagada.planb.networks.UserResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextUsername, editTextPassword, editTextPhone;
    private ImageView imgProfile;
    boolean is_female;
    private com.app.kagada.planb.networks.APIInterface APIInterface = APIClient.getClient().create(APIInterface.class);
    private String token;
    private StorageReference mStorageRef;
    private String imgUrl = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        imgProfile = findViewById(R.id.profile_img);
        Button imgButton = findViewById(R.id.button_img);
        editTextUsername = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.user_phone);
        Spinner spin = findViewById(R.id.spinner);
        String[] objects = {"Male", "Female"};
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.spin_item, objects);

        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                is_female = position == 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);
        imgButton.setOnClickListener(this);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM fail", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = Objects.requireNonNull(task.getResult()).getToken();

                    }
                });
    }

    private void userSignUp() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();


        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }

        if (!username.matches("^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{0,29}$")) {
            editTextUsername.setError("Username not available");
            editTextUsername.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone is required");
            editTextPhone.requestFocus();
            return;
        }

        if (!phone.matches("^[6-9]\\d{9}$")) {
            editTextPhone.setError("Enter valid phone number");
            editTextPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password should be at least 6 character long");
            editTextPassword.requestFocus();
            return;
        }

        String gender;
        if (is_female)
            gender = "True";
        else
            gender = "False";
        Map<String, String> params = new HashMap<>();
        params.put("email", username);
        params.put("password", password);
        params.put("is_female", gender);
        params.put("token", token);
        params.put("img_url", imgUrl);
        params.put("phone", phone);

        APIInterface.postUser(params).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.code() == 201) {
                    UserResponse dr = response.body();
                    Toast.makeText(SignUpActivity.this, "SignUp Successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

                } else {
                    Toast.makeText(SignUpActivity.this, "SignUp unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignUp:
                userSignUp();
                break;
            case R.id.textViewLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.button_img:
                if (checkPermission()) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                byte[] data;
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    bmp = Bitmap.createScaledBitmap(bmp, 250, 250, true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    data = stream.toByteArray();
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
                    String dateToStr = format.format(today);
                    StorageReference proRef = mStorageRef.child("profile/" + dateToStr + ".jpeg");
                    proRef.putBytes(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imgUrl = uri.toString();
                                        }
                                    });
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgProfile.setImageURI(selectedImage);

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        else {
            try {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            return true;
        }
    }
}