package com.example.a12thproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.a12thproject.activities.MainActivity;
import com.example.a12thproject.activities.alllist.ActivityAllCourts;
import com.example.a12thproject.activities.alllist.ActivityAllPlayers;
import com.example.a12thproject.activities.alllist.ActivityAllTeams;
import com.example.a12thproject.activities.createforms.ActivityCourtForm;
import com.example.a12thproject.activities.createforms.ActivityTeamForm;
import com.example.a12thproject.activities.profiles.ActivityPlayerProfile;
import com.example.a12thproject.classes.Player;
import com.example.a12thproject.firebase.PlayerFirebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    PlayerFirebase playerFirebase = new PlayerFirebase();

    String location = "0,0";



    FirebaseFirestore db = null;

    String message;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private ArrayList<String> permissions;

    boolean permissionDenied = true;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            boolean specificPermissionDenied = false;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;

                    if (!specificPermissionDenied) {
                        specificPermissionDenied = true;
                        // Ask for the specific permission that is denied
                        ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, PERMISSION_REQUEST_CODE);
                        break;
                    }
                }
            }

            if (allPermissionsGranted) {
                permissionDenied = false;
            }
        }
    }



    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        String name = getIntent().getStringExtra("currUsername");




// Initialize the permissions ArrayList in your activity's onCreate() or constructor
        permissions = new ArrayList<>();
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        permissions.add(Manifest.permission.READ_PHONE_NUMBERS);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        for (String perm : permissions) {
            if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                    // Show an explanation to the user asynchronously
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Needed")
                            .setMessage("This app requires the permission to function properly.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                ActivityCompat.requestPermissions(this, new String[]{perm}, PERMISSION_REQUEST_CODE);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                Toast.makeText(this, "Permission denied. Please enable it from the app settings.", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .create()
                            .show();
                }

                permissionDenied = true;
                break;
            }
        }







        checkLocationPermission();




        ImageView watchProfile = (ImageView) findViewById(R.id.watch_profile);
        watchProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("players").document(name).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Player player = document.toObject(Player.class);
                            Intent intent = new Intent(HomeActivity.this, ActivityPlayerProfile.class);
                            intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                            intent.putExtra("username", getIntent().getStringExtra("currUsername"));
                            intent.putExtra("player", (Serializable) player);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        ImageView createCourt = findViewById(R.id.createCourtLogo);
        createCourt.setClickable(true);
        createCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ActivityCourtForm.class);
                intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                startActivity(intent);
            }
        });

        ImageView searchTeam = findViewById(R.id.searchTeamLogo);
        searchTeam.setClickable(true);
        searchTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ActivityAllTeams.class);
                intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                startActivity(intent);
            }
        });

        ImageView searchCourt = findViewById(R.id.searchCourtLogo);
        searchCourt.setClickable(true);
        searchCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ActivityAllCourts.class);
                intent.putExtra("location", location);
                intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                startActivity(intent);
            }
        });

        ImageView createTeam = findViewById(R.id.createTeamLogo);
        createTeam.setClickable(true);
        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ActivityTeamForm.class);
                intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                startActivity(intent);
            }
        });

        ImageView searchPlayer = findViewById(R.id.searchPlayerLogo);
        searchPlayer.setClickable(true);
        searchPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ActivityAllPlayers.class);
                intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                startActivity(intent);
            }
        });

    }

    private void locationChecker() {
        LocationManager locationManager;
        LocationListener locationListener;



// Initialize the object in onCreate or a similar method
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

// Get last known location
        Location lastKnownLocation = null;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            db.collection("players").document(getIntent().getStringExtra("currUsername")).update("location", latitude+","+longitude);
            location = latitude+","+longitude;
        }



        locationListener = new LocationListener() {




            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                db.collection("players").document(getIntent().getStringExtra("currUsername")).update("location", latitude+","+longitude);
            }

            @Override
            public void onLocationChanged(@NonNull List<android.location.Location> locations) {
                LocationListener.super.onLocationChanged(locations);
            }

            @Override
            public void onFlushComplete(int requestCode) {
                LocationListener.super.onFlushComplete(requestCode);
            }


        };

// Request location updates
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mnuMusicOn:
                Intent i = new Intent(this, MusicService.class);
                startService(i);
                return true;





            case R.id.itemLogout:
                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", null);
                editor.commit();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.itemReport:
                Dialogs.reportDialog(this, getIntent().getStringExtra("currUsername"), "admin", "a");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkLocationPermission() {
       boolean flag = true;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            flag = false;
        }

// Check if fine location permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    2);
            flag = false;
        }
        if(flag) {
            locationChecker();
        }


    }

}
