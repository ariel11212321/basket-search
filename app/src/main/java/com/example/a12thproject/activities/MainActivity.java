package com.example.a12thproject.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.a12thproject.Dialogs;
import com.example.a12thproject.HomeActivity;
import com.example.a12thproject.R;
import com.example.a12thproject.broadcasts.MyReceiver;
import com.example.a12thproject.classes.Player;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView username = null;
    TextView password = null;


    TextView signup = null;

    Player uc = null;


    FirebaseFirestore db = null;

    CheckBox rememberMe = null;

    SharedPreferences sp = null;
    SharedPreferences.Editor editor = null;
    String currLocation;

    String perm_checker = "";

    boolean flag = false;




    TextView textView9;

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        db = FirebaseFirestore.getInstance();
        db.clearPersistence();



        textView9 = findViewById(R.id.textView9);
        textView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "error, please fill out the username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "please allow send sms permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{ android.Manifest.permission.SEND_SMS}, 1);
                }

                db.collection("players").document(username.getText().toString()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Player player = document.toObject(Player.class);
                            Dialogs.updatePlayerDialog(player, MainActivity.this, true, getIntent().getStringExtra("currUsername"));
                        }
                    }
                });
            }
        });









        sp = getSharedPreferences("login", MODE_PRIVATE);
        editor = sp.edit();


        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        rememberMe = (CheckBox) findViewById(R.id.btnRemember);


        signup = (TextView) findViewById(R.id.signup);

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivitySignup.class);
            startActivity(intent);
        });

        if(getIntent().hasExtra("username") && getIntent().hasExtra("password")) {
            username.setText(getIntent().getStringExtra("username"));
            password.setText(getIntent().getStringExtra("password"));
        }

        if (sp.getString("username", null) != null) {
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            i.putExtra("currUsername", sp.getString("username", null));





            db.collection("players").document(sp.getString("username", null)).get().addOnSuccessListener(documentSnapshot -> {
                if (!(documentSnapshot.exists())) {
                    Toast.makeText(MainActivity.this, "User does not exist (may be deleted)", Toast.LENGTH_SHORT).show();
                    editor.clear();
                    editor.apply();
                    return;
                } else {
                    startActivity(i);
                }
            });
            username.setText(sp.getString("username", null));
            password.setText(sp.getString("password", null));
            uc = new Player();
            uc.setUsername(sp.getString("username", null));
            uc.setPassword(sp.getString("password", null));

            setCurrentLocation();
        }


        ImageButton login = findViewById(R.id.login);


        login.setOnClickListener(v -> {
            String username_text = username.getText().toString();
            String password_text = password.getText().toString();

            uc = new Player();
            uc.setPassword(password_text);
            uc.setUsername(username_text);





            if (password_text.isEmpty() || username_text.isEmpty() || username == null || password == null) {
                Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }




            try {


                DocumentReference docRef = db.collection("players").document(username_text);
                docRef.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot document = task1.getResult();
                        if (document.exists()) {
                            String pass = document.getString("password");
                            assert pass != null;
                            if (pass.equals(password_text)) {




                                setCurrentLocation();



                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.putExtra("currUsername", username.getText().toString());
                                startActivity(intent);






                                if (rememberMe.isChecked()) {
                                    editor.putString("username", username.getText().toString());
                                    editor.putString("password", password.getText().toString());
                                    editor.putBoolean("remember", true);
                                }
                                editor.apply();
                            } else {
                                Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                finish();
            }

        });


    }

    public void setCurrentLocation() {
        // get the current location of the user and set it to the current location of the player
        // this is done by getting the current location of the user and setting it to the current location of the player

        FusedLocationProviderClient fusedLocationClient;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        currLocation = locationToLatLong(location);
                        uc.setLocation(currLocation);
                        db.collection("players").document(username.getText().toString()).update("location", currLocation);
                    }
                }
            });
        }

    }

    private String locationToLatLong(Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }
    private String locationToCity(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getLocality();
        return cityName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemAdmin:
                Intent intent = new Intent(MainActivity.this, ActivityAdmin.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {

        }

    }



}

