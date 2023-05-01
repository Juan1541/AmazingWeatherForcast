package com.example.weatherforecast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherforecast.R;
import com.example.weatherforecast.databinding.ActivityLoadingBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LoadingActivity extends AppCompatActivity {

    ActivityLoadingBinding binding; // View binding instance for the loading activity

    MyCountDownTimer myCountDownTimer; // Custom countdown timer instance

    FusedLocationProviderClient fusedLocationClient; // Fused location provider client instance

    final public String TAG = "JUAN DEBUGGING"; // Tag used for debugging purposes

    // Permission request codes for fine and coarse location
    private static final int FINE_LOCATION_PERMISSION_CODE = 100;
    private static final int COARSE_LOCATION_PERMISSION_CODE = 101;

    double latitude; // Variable to store the latitude obtained from location services
    double longitude; // Variable to store the longitude obtained from location services

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Sets the view for the loading activity

        // Loads an animated GIF image using Glide and displays it on the screen
        Glide.with(this).load(R.drawable.giphyfin).into(binding.stormyImage);

        myCountDownTimer = new MyCountDownTimer(5000, 1000); // Initializes the custom countdown timer
        myCountDownTimer.start(); // Starts the countdown

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // Initializes the location provider client

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE); // Checks for fine location permission
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION_PERMISSION_CODE); // Checks for coarse location permission

        // Gets the last known location using the location provider client and saves the latitude and longitude values
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error trying to get last GPS location"); // Logs an error message if the location cannot be obtained
                e.printStackTrace(); // Prints the stack trace for debugging purposes
            }
        });
    }

    // Checks for a specific permission and requests it if necessary
    public void checkPermission(String permission, int requestCode){
        if (ActivityCompat.checkSelfPermission(LoadingActivity.this, permission) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{permission},
                    requestCode);
        } else {
            Toast.makeText(LoadingActivity.this, "Permission already granted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Handles the result of a permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoadingActivity.this, "Fine Location Permission Granted",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoadingActivity.this, "Fine Location Permission Denied",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == COARSE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoadingActivity.this, "Coarse Location Permission Granted",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoadingActivity.this, "Coarse Location Permission Denied",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

        public class MyCountDownTimer extends CountDownTimer{

            int progress = 0; // Initialize the progress variable to 0

            // Constructor that takes the total time of the countdown and the interval at which onTick will be called
            public MyCountDownTimer(long millisInFuture, long countDownInterval){
                super(millisInFuture, countDownInterval);
            }

            // This method is called every countDownInterval milliseconds during the countdown
            @Override
            public void onTick(long millisUntilFinished){
                progress = progress + 20; // Increase progress by 20
                binding.loadingProgressBar.setProgress(progress);// Update the progress bar with the new progress value
            }

            // This method is called when the countdown is finished
            @Override
            public void onFinish(){
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                intent.putExtra("LATITUDE", String.valueOf(latitude)); // Put the latitude value as an extra in the intent
                intent.putExtra("LONGITUDE", String.valueOf(longitude)); // Put the longitude value as an extra in the intent
                startActivity(intent);
            }
    }
}