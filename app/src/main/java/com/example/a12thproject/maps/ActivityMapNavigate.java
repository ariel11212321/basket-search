package com.example.a12thproject.maps;



import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;


import com.example.a12thproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ActivityMapNavigate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_navigate);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please enable location permission", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please enable location permission")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {
                        finish();
                    });
            AlertDialog alert = builder.create();
            alert.show();

            return;
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String destination = getIntent().getStringExtra("dest");







                            Geocoder geocoder = new Geocoder(ActivityMapNavigate.this);

                            try {
                                // Get destination coordinates from the address
                                List<Address> destinationAddresses = geocoder.getFromLocationName(destination, 1);

                                if (destinationAddresses != null && !destinationAddresses.isEmpty()) {
                                    Address address = destinationAddresses.get(0);
                                    double destLatitude = address.getLatitude();
                                    double destLongitude = address.getLongitude();

                                    // Create destination location
                                    Location destinationLocation = new Location("Destination");
                                    destinationLocation.setLatitude(destLatitude);
                                    destinationLocation.setLongitude(destLongitude);

                                    // Get current location
                                    Location currentLocation = new Location("Current");
                                    currentLocation.setLatitude(location.getLatitude());
                                    currentLocation.setLongitude(location.getLongitude());

                                    // Calculate the distance and direction
                                    float distance = currentLocation.distanceTo(destinationLocation);
                                    float bearing = currentLocation.bearingTo(destinationLocation);

                                    // Open Google Maps app with directions
                                    String uri = "google.navigation:q=" + destLatitude + "," + destLongitude +
                                            "&mode=d&distance=" + distance + "&bearing=" + bearing;
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                                        intent.setPackage("com.google.android.apps.maps");
                                    }

                                    if (intent.resolveActivity(getPackageManager()) != null) {
                                        startActivity(intent);
                                    } else {
                                        // Handle the case when Google Maps app is not installed on the device
                                        // You can open a web-based map or display an error message.
                                    }
                                } else {
                                    // Handle the case when no coordinates are found for the given address
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                // Handle the IOException
                            }
                        }
                    }
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                });







    }
}