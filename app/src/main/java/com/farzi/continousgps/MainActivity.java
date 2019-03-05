package com.farzi.continousgps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity{
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY";
    private static final String TAG = "LocationUpdate";
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 100;
    private static final long FASTEST_INTERVAL = 100;
    private TextView coordinates, accuracy, bearing, speed, provider, bearingAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String[] requestedPermissions = retrievePermissions(this);
        for(String requestedPermission : requestedPermissions){
            requestPermission(requestedPermission);
        }
        coordinates = (TextView)findViewById(R.id.coordinates);
        accuracy = (TextView)findViewById(R.id.accuracy);
        bearing = (TextView)findViewById(R.id.bearing);
        speed = (TextView)findViewById(R.id.speed);
        provider = (TextView)findViewById(R.id.provider);
        bearingAccuracy = (TextView)findViewById(R.id.bearing_accuracy);
        createLocationRequest();
        updateValuesFromBundle(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    updateUI(location);
                }
            };
        };
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                null /* Looper */);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state
        //updateUI();
    }

    void updateUI(Location location){
        Log.d(TAG, location.toString());
        coordinates.setText("Coordinates : " + location.getLatitude() + ", " + location.getLongitude());
        accuracy.setText("Accuracy : " + location.getAccuracy());
        bearing.setText("Bearing : " + location.getBearing());
        speed.setText("Speed : " + location.getSpeed());
        provider.setText("provider : " +location.getProvider());
        bearingAccuracy.setText("bearingAccuracy : " + location.getBearingAccuracyDegrees());
    }

    public static String[] retrievePermissions(Context context) {
        try {
            return context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException ("This should have never happened.", e);
        }
    }

    private void requestPermission(String requestedPermission){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, requestedPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{requestedPermission}, 1);
        } else {
            // Permission has already been granted
        }
    }
}
