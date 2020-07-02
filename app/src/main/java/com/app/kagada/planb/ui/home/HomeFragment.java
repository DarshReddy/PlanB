package com.app.kagada.planb.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.kagada.planb.activities.MainActivity;
import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.LocationTrack;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

  private EditText mBudget;
  private Double mLat;
  private Double mLong;
  private ArrayList permissionsToRequest;
  private ArrayList permissionsRejected = new ArrayList();
  private ArrayList permissions = new ArrayList();

  private final static int ALL_PERMISSIONS_RESULT = 101;
  private LocationTrack locationTrack;

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_home, container, false);
//    Places.initialize(getActivity().getApplicationContext(), "AIzaSyDkl1zx6zVPgame1O6isxWuhxGwTIcFfgc");
    return root;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

/*
    AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
            getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
      @Override
      public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
        Toast.makeText(getContext(), "" + place.getLatLng(), Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onError(Status status) {
        // TODO: Handle the error.
        Log.i(TAG, "An error occurred: " + status);
      }
    });
*/

    permissions.add(ACCESS_FINE_LOCATION);
    permissions.add(ACCESS_COARSE_LOCATION);

    permissionsToRequest = findUnAskedPermissions(permissions);
    //get the permissions we have asked for before but are not granted..
    //we will store this in a global list to access later.

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


      if (permissionsToRequest.size() > 0)
        requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
    }
    locationTrack = new LocationTrack(getContext());

    mBudget = view.findViewById(R.id.budget_edit);
    mBudget.requestFocus();
    Button mPlanBtn = view.findViewById(R.id.btn_plan);
    mPlanBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getLoc();
        if(mLat==null && mLong==null) {
          mLat=locationTrack.getLatitude();
          mLong=locationTrack.getLongitude();
        }
        if (mLat != 0.0 && mLong != 0.0 && !mBudget.getText().toString().equals("")) {
          Intent intent = new Intent(getActivity(), MainActivity.class);
          intent.putExtra("call_frag",1);
          intent.putExtra("LAT", mLat);
          intent.putExtra("LONG", mLong);
          intent.putExtra("COST", mBudget.getText().toString());
          startActivity(intent);
        } else
          Toast.makeText(getContext(), "Location fetching unsuccessful, please enter all details or restart and try again!", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void getLoc() {
    if (locationTrack.canGetLocation()) {


      mLong = locationTrack.getLongitude();
      mLat = locationTrack.getLatitude();
      if (mLat==0.0 && mLong ==0.0) {
        locationTrack.networkLoc();
        mLong = locationTrack.getLongitude();
        mLat = locationTrack.getLatitude();
      }
//      Toast.makeText(getContext(), "Longitude:" + Double.toString(mLong) + "\nLatitude:" + Double.toString(mLat), Toast.LENGTH_SHORT).show();
    } else {
      displayLocationSettingsRequest(getContext());
    }
  }

  private ArrayList findUnAskedPermissions(ArrayList wanted) {
    ArrayList result = new ArrayList();

    for (Object perm : wanted) {
      if (!hasPermission((String) perm)) {
        result.add(perm);
      }
    }

    return result;
  }
  private boolean hasPermission(String permission) {
    if (canMakeSmores()) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return (requireActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
      }
    }
    return true;
  }
  private boolean canMakeSmores() {
    return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {

    if (requestCode == ALL_PERMISSIONS_RESULT) {
      for (Object perms : permissionsToRequest) {
        if (!hasPermission((String) perms)) {
          permissionsRejected.add(perms);
        }
      }

      if (permissionsRejected.size() > 0) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
            showMessageOKCancel(
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                      }
                    });
          }
        }

      }
    }

  }

  @Override
  public void onPause() {
    super.onPause();
    locationTrack.stopListener();
  }

  private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
    new AlertDialog.Builder(getContext())
            .setMessage("These permissions are mandatory for the application. Please allow access.")
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show();
  }

  private void displayLocationSettingsRequest(final Context context) {
    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build();
    googleApiClient.connect();

    final LocationRequest locationRequest = LocationRequest.create();
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(10000);
    locationRequest.setFastestInterval(10000 / 2);

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);
    builder.setAlwaysShow(true);

    Task<LocationSettingsResponse> result =
            LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());

    result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
      @Override
      public void onComplete(@NotNull Task<LocationSettingsResponse> task) {
        try {
          LocationSettingsResponse response = task.getResult(ApiException.class);
          // All location settings are satisfied. The client can initialize location
          // requests here.
          locationTrack.canGetLocation = true;
        } catch (ApiException exception) {
          switch (exception.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
              // Location settings are not satisfied. But could be fixed by showing the
              // user a dialog.
              try {
                // Cast to a resolvable exception.
                ResolvableApiException resolvable = (ResolvableApiException) exception;
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                resolvable.startResolutionForResult(
                        getActivity(),
                        1);
              } catch (IntentSender.SendIntentException e) {
                // Ignore the error.
              } catch (ClassCastException e) {
                // Ignore, should be an impossible error.
              }
              break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
              // Location settings are not satisfied. However, we have no way to fix the
              // settings so we won't show the dialog.

              break;
          }
        }
      }
    });
  }
}