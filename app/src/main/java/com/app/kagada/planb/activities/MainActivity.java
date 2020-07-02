package com.app.kagada.planb.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.APIClient;
import com.app.kagada.planb.networks.APIInterface;
import com.app.kagada.planb.networks.putResponse;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private long BackTime;
    private Toast toast;
    private int todo;
    private SharedPreferences preferences;
    private StorageReference mStorageRef;
    private ImageView imgView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("myToken", Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        imgView = header.findViewById(R.id.header_image);
        Glide
                .with(header)
                .load(preferences.getString("img_url",""))
                .centerCrop()
                .into(imgView);
        TextView header_text = header.findViewById(R.id.header_text);
        TextView header_body = header.findViewById(R.id.header_body);
        ImageButton header_btn = header.findViewById(R.id.img_update);
        header_text.setText(preferences.getString("username","User"));
        header_body.setText(preferences.getString("phone","xxx"));
        header_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                }
            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_dates,
                R.id.nav_account, R.id.nav_share, R.id.nav_rating, R.id.nav_plan)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

      Intent intent = getIntent();
      todo = intent.getIntExtra("call_frag",0);
      switch (todo) {
        case 0: navController.navigate(R.id.nav_home);
                break;
        case 1: navController.navigate(R.id.nav_plan, intent.getExtras());
                break;
        case 2: navController.navigate(R.id.nav_dates, intent.getExtras());
                break;
        case 3: navController.navigate(R.id.nav_rating, intent.getExtras());
                break;
      }

      navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_home) {
                }
                if (destination.getId() == R.id.nav_history) {
                }
                if (destination.getId() == R.id.nav_account) {
                }
                if (destination.getId() == R.id.nav_share) {
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
      if(todo==0) {
        if (BackTime + 2000 > System.currentTimeMillis()) {
          toast.cancel();
          moveTaskToBack(true);
        } else {
          toast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
          toast.show();
        }
        BackTime = System.currentTimeMillis();
      }
      else finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            }
        });
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this, "There ain't any settings to change yet!;)", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                final Uri selectedImage = imageReturnedIntent.getData();
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
                                            if (!preferences.getString("img_url","").equals("")) {
                                                StorageReference refDel = mStorageRef.getStorage().getReferenceFromUrl(preferences.getString("img_url",""));
                                                refDel.delete().addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this, "Deleting old picture failed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            String imgUrl = uri.toString();
                                            imgView.setImageURI(selectedImage);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("img_url", imgUrl);
                                            editor.apply();
                                            Map<String, String> params = new HashMap<>();
                                            params.put("email", preferences.getString("username","User"));
                                            params.put("img_url", imgUrl);

                                            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                                            apiInterface.putUser(params).enqueue(new Callback<putResponse>() {
                                                @Override
                                                public void onResponse(Call<putResponse> call, Response<putResponse> response) {
                                                    assert response.body() != null;
                                                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailure(Call<putResponse> call, Throwable t) {
                                                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(this, "You need to provide location permission!", Toast.LENGTH_SHORT).show();
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