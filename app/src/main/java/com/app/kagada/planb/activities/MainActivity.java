package com.app.kagada.planb.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.MyNotificationManager;
import com.app.kagada.planb.ui.dates.DateFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private long BackTime;
    private Toast toast;
    private NavController navController;
    private int todo;
    private SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("myToken", Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        ImageView img = header.findViewById(R.id.header_image);
        Glide
                .with(header)
                .load(preferences.getString("img_url","TODO"))
                .centerCrop()
                .into(img);
        TextView header_text = header.findViewById(R.id.header_text);
        TextView header_body = header.findViewById(R.id.header_body);
        header_text.setText(preferences.getString("username","User"));
        header_body.setText(preferences.getString("phone","xxx"));

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_dates,
                R.id.nav_account, R.id.nav_share, R.id.nav_rating, R.id.nav_plan)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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
}