package com.example.a12thproject.activities.alllist;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.a12thproject.Dialogs;
import com.example.a12thproject.MusicService;
import com.example.a12thproject.R;

import com.example.a12thproject.activities.profiles.ActivityTeamProfile;
import com.example.a12thproject.adapters.TeamAdapter;
import com.example.a12thproject.classes.MapHelper;
import com.example.a12thproject.classes.Team;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityAllTeams extends AppCompatActivity {

    FirebaseFirestore db = null;
    TeamAdapter adapter;
    ArrayList<Team> teams;
    ListView listView;
    ImageButton filter;
    Button search;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_all_teams);


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});

        db = FirebaseFirestore.getInstance();
        teams = new ArrayList<>();


        listView = (ListView) findViewById(R.id.listViewTeams);
        filter = (ImageButton) findViewById(R.id.btnFilterTeams);
        search = (Button) findViewById(R.id.btnSearchTeams);




        init();
        initRequests();

        filter.setOnClickListener(v -> {
           filterDialog();
        });
    }
    private void filterDialog() {
        String[] filterOptions = {"name", "rating", "captain", "gamesPlayed"};
        Dialogs.filterDialog(this, filterOptions, teams);


        search.setOnClickListener(v -> {
            String[] FILTER_VALUES = Dialogs.FILTER_VALUES;
            ArrayList<String> FILTER_OPTIONS = Dialogs.FILTER_OPTIONS;

            if(FILTER_VALUES == null && FILTER_OPTIONS == null) {
                return;
            }
            teams.clear();

            if(getIntent().hasExtra("requests")) {
                db.collection("teamRequests").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Team t = document.toObject(Team.class);
                            boolean b = true;

                            if(t == null)  {
                                continue;
                            }

                            for(int i = 0; i < FILTER_OPTIONS.size() && i < FILTER_VALUES.length; i++) {
                                assert FILTER_VALUES != null;
                                if(FILTER_OPTIONS.get(i).equals("country") || FILTER_OPTIONS.get(i).equals("city") && (!MapHelper.getLocation(this, t.getLocation()).contains(FILTER_VALUES[i]))) {

                                }
                                else if(!Objects.requireNonNull(document.get(FILTER_OPTIONS.get(i))).toString().contains(FILTER_VALUES[i])) {
                                    b = false;
                                }
                            }
                            if(b) {
                                teams.add(t);
                            }

                        }
                        adapter = new TeamAdapter(this, R.layout.onerowteam, teams);
                        listView.setAdapter(adapter);
                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                        Toast.makeText(this, "could not get teams", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(e -> {
                    System.out.println("Error getting documents: " + e);
                    Toast.makeText(this, "could not get teams", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                db.collection("teams").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Team t = document.toObject(Team.class);
                            boolean b = true;

                            for(int i = 0; i < FILTER_OPTIONS.size(); i++) {
                                assert FILTER_VALUES != null;
                                if(!Objects.requireNonNull(document.get(FILTER_OPTIONS.get(i))).toString().contains(FILTER_VALUES[i])) {
                                    b = false;
                                }
                            }


                        }
                        adapter = new TeamAdapter(this, R.layout.onerowteam, teams);
                        listView.setAdapter(adapter);
                    }
                }).addOnFailureListener(e -> {
                    System.out.println("Error getting documents: " + e);
                    Toast.makeText(this, "could not get teams", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }



        });



    }


    private void initRequests() {
        if(getIntent().hasExtra("requests")) {
            teams.clear();
            db.collection("teamRequests").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Team team = document.toObject(Team.class);
                        if(team == null) {
                            continue;
                        }
                        teams.add(team);
                    }
                    adapter = new TeamAdapter(this, R.layout.onerowteam, teams);
                    listView.setAdapter(adapter);
                }
            });


            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent i = new Intent(this, ActivityTeamProfile.class);
                i.putExtra("admin", true);
                i.putExtra("teamName", teams.get(position).getName());
                i.putExtra("request", true);
                i.putExtra("team", teams.get(position));
                startActivity(i);
            });
        }
    }
    private void init() {
        if(!getIntent().hasExtra("requests")) {
            teams.clear();
            db.collection("teams").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Team t = document.toObject(Team.class);
                        teams.add(t);
                    }
                    adapter = new TeamAdapter(this, R.layout.onerowteam, teams);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "could not get teams", Toast.LENGTH_SHORT).show();
                   finish();
                }
            });
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(this, ActivityTeamProfile.class);
                intent.putExtra("teamName", teams.get(position).getName());
                intent.putExtra("team", teams.get(position));
                if(getIntent().hasExtra("admin")) {
                    intent.putExtra("admin", true);
                }
                else {
                    intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                }
                startActivity(intent);
            });

        }
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
