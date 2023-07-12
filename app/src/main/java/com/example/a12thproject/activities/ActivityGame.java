package com.example.a12thproject.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a12thproject.MusicService;
import com.example.a12thproject.R;
import com.example.a12thproject.SmsManager;
import com.example.a12thproject.WaitUntilNextHourTask;
import com.example.a12thproject.activities.alllist.ActivityAllCourts;
import com.example.a12thproject.activities.alllist.ActivityAllPlayers;
import com.example.a12thproject.classes.Court;
import com.example.a12thproject.classes.MapHelper;
import com.example.a12thproject.classes.Player;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class ActivityGame extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    FirebaseFirestore db;



    Button chooseCourt;

    Button start;

    Spinner team1Spinner;
    Spinner team2Spinner;

    String[] team1, team2;

    String requiredValue;

    Court courtChosen = null;

    Switch isPrivate;

    private int mYear, mMonth, mDay, mHour, mMinute;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = FirebaseFirestore.getInstance();

        start = (Button) findViewById(R.id.btn_submit_start);
        start.setOnClickListener(this);

        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});


        chooseCourt = (Button) findViewById(R.id.btn_choose_court);

        chooseCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityGame.this, ActivityAllCourts.class);
                i.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                i.putExtra("gameRequest", true);
                startActivityForResult(i, 5);
            }
        });


        Spinner team1Spinner = (Spinner) findViewById(R.id.team1Spinner);
        Spinner team2Spinner = (Spinner) findViewById(R.id.team2Spinner);


        ArrayAdapter<String> adapter;
         team1 = new String[]{"team 1 players", getIntent().getStringExtra("currUsername"), "player 2", "player 3", "player 4", "player 5"};
         team2 = new String[]{"team 2 players", "player 1", "player 2", "player 3", "player 4", "player 5"};



        int indexTeam1 = 0;
        int indexTeam2 = 0;





        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, team1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        team1Spinner.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, team2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        team2Spinner.setAdapter(adapter);


        team1Spinner.setSelection(0, false);
        team1Spinner.setSelection(1, false);

        team2Spinner.setSelection(0, false);

        team1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                for(int i = 1; i < position; i++) {
                    if(team1[i].equals("player "+position)) {
                        Toast.makeText(getApplicationContext(), "please make sure you invite players from up to down", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(!team1[position].equals("player "+position)) {
                    team1[position] = "player "+position;

                } else {
                    Intent i = new Intent(getApplicationContext(), ActivityAllPlayers.class);
                    i.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                    i.putExtra("gameRequest", "team1");
                    i.putExtra("position", position);
                    i.putExtra("location", getIntent().getStringExtra("location"));
                    startActivityForResult(i, 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        team2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 1; i < position; i++) {
                    if(team2[i].equals("player "+position)) {
                        Toast.makeText(getApplicationContext(), "please make sure you invite players from up to down", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(!team2[position].equals("player "+(position))) {
                    team2[position] = "player "+(position);
                } else {
                    Intent i = new Intent(getApplicationContext(), ActivityAllPlayers.class);
                    i.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                    i.putExtra("gameRequest", "team2");
                    i.putExtra("position", position);
                    i.putExtra("location", getIntent().getStringExtra("location"));
                    startActivityForResult(i, 2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            isPrivate = (Switch) findViewById(R.id.switchPrivate);

        }

    }

    @Override
    public void onClick(View v) {
        if(v == start) {

            int team1_counter = 0;
            for(int i = 0; i < team1.length; i++) {
                if(!team1[i].equals("player "+(i+1))) {
                    team1_counter++;
                }
            }
            int team2_counter = 0;
            for(int i = 0; i < team2.length; i++) {
                if(!team2[i].equals("player "+(i+1))) {
                    team2_counter++;
                }
            }
            if(team1_counter != team2_counter) {
                Toast.makeText(getApplicationContext(), "please choose the same amount of player on each team", Toast.LENGTH_SHORT).show();
            }


            if(courtChosen == null) {
                Toast.makeText(getApplicationContext(), "please choose a court", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("courts").document(courtChosen.getName()).update("available", "false");
            // wait for an hour and then update the court to be available again


            if(!isPrivate.isChecked()) {
                db.collection("players").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (calculateDistance(document.getString("location"), getIntent().getStringExtra("location")) <= 1) {
                                android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                                smsManager.sendTextMessage(document.getString("phone"), null, "you can come to court: " + courtChosen.getName() +", at location : " + courtChosen.getLocation(), null, null);
                            }
                        }
                    }
                });
            }
            for(String player : team1) {
                db.collection("players").document(player).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult().exists()) {
                        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                        smsManager.sendTextMessage(task.getResult().getString("phone"), null, "you can come to court: " + courtChosen.getName() +", at location : " + MapHelper.getLocation(this, courtChosen.getLocation()), null, null);

                    }
                });
            }
            for(String player : team2) {
                db.collection("players").document(player).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult().exists()) {
                        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                        smsManager.sendTextMessage(task.getResult().getString("phone"), null, "you can come to court: " + courtChosen.getName() +", at location : " + MapHelper.getLocation(this, courtChosen.getLocation()), null, null);
                    }
                });
            }





            Thread waitThread = new Thread(new WaitUntilNextHourTask());
            waitThread.start();
            courtChosen.setAvailable("true");
            db.collection("courts").document(courtChosen.getName()).set(courtChosen);


            db.collection("players").get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult().size() > 0) {
                    for(DocumentSnapshot ds : task.getResult().getDocuments()) {
                        if(calculateDistance(ds.getString("location"), courtChosen.getLocation()) <=1000000 ) {
                            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                            smsManager.sendTextMessage(ds.getString("phone"), null, "the following court is now available, come play: " + courtChosen.getName() +", at location : " + MapHelper.getLocation(this, courtChosen.getLocation()), null, null);
                        }
                    }
                }
            });

            finish();
        }

    }


    public static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    public static double calculateDistance(String location1, String location2) {
        // return the kilometer radius between two locations in the world, the locations oredered by lat,long
        String[] loc1 = location1.split(",");
        String[] loc2 = location2.split(",");
        double lat1 = Double.parseDouble(loc1[0]);
        double lon1 = Double.parseDouble(loc1[1]);
        double lat2 = Double.parseDouble(loc2[0]);
        double lon2 = Double.parseDouble(loc2[1]);
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist * 1.609344);

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1) {


                requiredValue = data.getStringExtra("playerChosen");

                for(int i = 0; i < team1.length; i++) {
                    if(team1[i].equals(requiredValue) || team2[i].equals(requiredValue)) {
                        Toast.makeText(getApplicationContext(), "you already have this player in one of the teams", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                team1[data.getIntExtra("position", 1)] = requiredValue;
                Toast.makeText(getApplicationContext(), requestCode, Toast.LENGTH_SHORT).show();
                ArrayAdapter<String> new_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, team1);
                team1Spinner.setAdapter(new_adapter);
            }
            else if(requestCode == 2) {
                requiredValue = data.getStringExtra("playerChosen");
                team2[data.getIntExtra("position", 1)] = requiredValue;
                Toast.makeText(getApplicationContext(), requestCode, Toast.LENGTH_SHORT).show();
                ArrayAdapter<String> new_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, team1);
                team2Spinner.setAdapter(new_adapter);
            }
            else if(requestCode == 5) {
                courtChosen = (Court) data.getSerializableExtra("courtChosen");
            }
        } catch (Exception ex) {
            Log.d("error", ex.toString());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music, menu);
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

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
