package com.example.mylocationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class UserLocationActivity extends AppCompatActivity {

        FusedLocationProviderClient mFusedLocationClient;
        TextView latitudeTextView, longitTextView;
        int PERMISSION_ID = 44;
        private static final String TAG = "UserLocationActivity";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_location);

            initViews();
            getLastLocation();
        }

        private void initViews() {
            latitudeTextView = findViewById(R.id.latTextView);
            longitTextView = findViewById(R.id.lonTextView);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }
        @SuppressLint("MissingPermission")
        private void getLastLocation() {
            /** check if permissions are already granted **/
            if (checkPermissions()) {

                /** check if location is enabled **/
                if (isLocationEnabled()) {

                    // getting last location from FusedLocationClient object
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                latitudeTextView.setText(location.getLatitude() + "");
                                longitTextView.setText(location.getLongitude() + "");
                                Log.d(TAG, "onLocationResult longitude:"+location.getLongitude());
                                Log.d(TAG, "onLocationResult latitude:"+location.getLatitude());
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            } else {
                // if permissions aren't available, request for permissions
                requestPermissions();
//                https://www.google.com/maps/search/?api=1&query= 37.421998333333335, -122.084
            }
        }

        @SuppressLint("MissingPermission")
        private void requestNewLocationData() {

            // Initializing LocationRequest object with appropriate methods
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5);
            mLocationRequest.setFastestInterval(0);
            mLocationRequest.setNumUpdates(1);

            // setting LocationRequest on FusedLocationClient
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }

        private LocationCallback mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
                longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
            }
        };
        /** This checks if permission has already been granted **/
        private boolean checkPermissions() {
            return ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            // If we want background location on Android 10.0 and higher, use: ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        /** if permission hasn't been granted, here we request for permission **/
        private void requestPermissions() {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        }



        /** method to check if location is enabled **/
        private boolean isLocationEnabled() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }

        // If everything is alright then
        @Override
        public void
        onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == PERMISSION_ID) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if (checkPermissions()) {
                getLastLocation();
            }
        }
    }

