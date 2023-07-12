package com.example.a12thproject.activities.alllist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.example.a12thproject.Dialogs;

import com.example.a12thproject.MusicService;
import com.example.a12thproject.R;
import com.example.a12thproject.activities.profiles.ActivityPlayerProfile;
import com.example.a12thproject.adapters.PlayerAdapter;
import com.example.a12thproject.classes.MapHelper;
import com.example.a12thproject.classes.Player;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityAllPlayers extends AppCompatActivity {
    ListView listView;

    ArrayList<Player> players;
    PlayerAdapter playerAdapter;

    ImageButton btnFilter = null;
    Button btnSearch = null;


    FirebaseFirestore db = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_players);


        if(!getIntent().hasExtra("currUsername") && !getIntent().hasExtra("admin")) {
            Toast.makeText(this, "error, please try again later.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});

        db = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.listView);


        btnFilter = findViewById(R.id.playersFilter);
        btnSearch = findViewById(R.id.btnSearch);

        players = new ArrayList<>();




        if(getIntent().hasExtra("teamJoinRequests")) {
            initListTeamJoinRequests();
            btnFilter.setOnClickListener(v -> {
                filterDialog("teamJoinRequests");
            });
        }
        else if(getIntent().hasExtra("friends")) {
            initListFriends();
            btnFilter.setOnClickListener(v -> {
               filterDialog("friends");
            });
        }
        else if(getIntent().hasExtra("requests")) {
            initListFriendRequests();

            btnFilter.setOnClickListener(v -> {
               filterDialog("friendRequests");
            });
        } else {
            initList();
            btnFilter.setOnClickListener(v -> {
              filterDialog("players");
            });
        }



    }
    private void filterDialog(String search) {
        String[] filterOptions = {"username", "fullname", "position", "age", "rating", "team", "phone"};
        Dialogs.filterDialog(ActivityAllPlayers.this, filterOptions, players);
        players.clear();

        btnSearch.setOnClickListener(v -> {
            String[] finalFilterValues = Dialogs.FILTER_VALUES;
            ArrayList<String> finalFilterOptions = Dialogs.FILTER_OPTIONS;


            if (search.equals("players")) {
                db.collection("players").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        players.clear();
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            boolean b = true;
                            for (int i = 0; i < finalFilterOptions.size() && i < finalFilterValues.length; i++) {
                                try {
                                    if(finalFilterOptions.get(i).equals("country") || finalFilterOptions.get(i).equals("city")) {
                                        if(!MapHelper.getLocation(this, document.getString("location")).contains(finalFilterValues[i])) {
                                            b = false;
                                        }
                                    }
                                    else if (!(document.getString(finalFilterOptions.get(i).toLowerCase()).toLowerCase().contains(finalFilterValues[i]))) {
                                        b = false;
                                    }
                                } catch (Exception e) {
                                    b = false;
                                    e.printStackTrace();
                                }
                            }
                            if (b) {
                                players.add(document.toObject(Player.class));
                            }
                        }
                        playerAdapter = new PlayerAdapter(ActivityAllPlayers.this, R.layout.onerowplayer, players);
                        listView.setAdapter(playerAdapter);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "could not load filter options", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
            else if(search.equals("friends") || search.equals("friendRequests")) {
                // take the current user's friends list
                db.collection("players").document(getIntent().getStringExtra("currUsername")).collection("friends").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        players.clear();
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            db.collection("players").document(document.getId()).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    players.clear();
                                    boolean b = true;
                                    for (int i = 0; i < finalFilterOptions.size(); i++) {
                                        try {
                                            if (!(Objects.requireNonNull(task1.getResult().getString(finalFilterOptions.get(i))).contains(finalFilterValues[i]))) {
                                                b = false;
                                            }
                                        } catch (Exception e) {
                                            b = false;
                                            e.printStackTrace();
                                        }
                                    }
                                    if (b) {
                                        players.add(task1.getResult().toObject(Player.class));
                                    }

                                    playerAdapter = new PlayerAdapter(ActivityAllPlayers.this, R.layout.onerowplayer, players);
                                    listView.setAdapter(playerAdapter);
                                }
                            });
                        }
                    }
                });




            } else if (search.equals("teamJoinRequests")) {




                db.collection("teams").document(getIntent().getStringExtra("teamName")).collection("joinRequests").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        players.clear();
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            db.collection("players").document(document.getId()).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    players.clear();
                                    boolean b = true;
                                    for (int i = 0; i < finalFilterOptions.size(); i++) {
                                        try {
                                            if (!(Objects.requireNonNull(task1.getResult().getString(finalFilterOptions.get(i))).contains(finalFilterValues[i]))) {
                                                b = false;
                                            }
                                        } catch (Exception e) {
                                            b = false;
                                            e.printStackTrace();
                                        }
                                    }
                                    if (b) {
                                        players.add(task1.getResult().toObject(Player.class));
                                    }

                                    playerAdapter = new PlayerAdapter(ActivityAllPlayers.this, R.layout.onerowplayer, players);
                                    listView.setAdapter(playerAdapter);
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "could not load filter options", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "could not load filter options", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });

    }






    private void initListTeamJoinRequests() {
        try {
            players.clear();
            ArrayList<String> req = new ArrayList<String>();
            db.collection("teams").document(getIntent().getStringExtra("teamName")).collection("joinRequests").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                   for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                          req.add(document.getId());
                   }
                } else {
                    Toast.makeText(this, "Error getting player, please try again later. ", Toast.LENGTH_SHORT).show();
                }
            });

            db.collection("players").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if(req.contains(document.getId())) {
                            players.add(document.toObject(Player.class));
                        }
                    }
                    playerAdapter = new PlayerAdapter(this, R.layout.onerowplayer, players);
                    listView.setAdapter(playerAdapter);
                } else {
                    Toast.makeText(this, "Error getting player, please try again later. ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            listView.setOnItemClickListener((parent, view, position, id) -> {
                Player p = players.get(position);
                Intent i = new Intent(ActivityAllPlayers.this, ActivityPlayerProfile.class);
                if(getIntent().hasExtra("admin")) {
                    i.putExtra("admin", true);
                } else {
                    i.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                }
                i.putExtra("teamName", getIntent().getStringExtra("teamName"));
                i.putExtra("username", p.getUsername());
                i.putExtra("request", "team");
                i.putExtra("player", p);
                startActivity(i);
            });

        } catch (Exception e) {
            e.printStackTrace();

            finish();
        }
    }
    private void initListFriends() {

       db.collection("players").document(getIntent().getStringExtra("currUsername")).collection("friends").get().addOnCompleteListener(task -> {
              if(task.isSuccessful()) {
                players.clear();
                for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                     db.collection("players").document(document.getId()).get().addOnCompleteListener(task1 -> {
                          if(task1.isSuccessful()) {
                            players.add(task1.getResult().toObject(Player.class));
                            playerAdapter = new PlayerAdapter(this, R.layout.onerowplayer, players);
                            listView.setAdapter(playerAdapter);
                          }
                     });
                }
                PlayerAdapter playerAdapter = new PlayerAdapter(this, R.layout.onerowplayer, players);
                listView.setAdapter(playerAdapter);
              } else {
                  Toast.makeText(this, "Error getting player, please try again later. ", Toast.LENGTH_SHORT).show();
                  finish();
              }
       });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Player p = players.get(position);
            Intent i = new Intent(ActivityAllPlayers.this, ActivityPlayerProfile.class);
            i.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
            i.putExtra("username", p.getUsername());
            i.putExtra("player", p);
            startActivity(i);
        });

    }
    private void initListFriendRequests() {
        players.clear();

        db.collection("players").document(getIntent().getStringExtra("currUsername")).collection("friendRequests").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    db.collection("players").document(document.getId()).get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            players.add(task1.getResult().toObject(Player.class));
                            playerAdapter = new PlayerAdapter(this, R.layout.onerowplayer, players);
                            listView.setAdapter(playerAdapter);
                        } else {
                            Toast.makeText(this, "Error getting player, please try again later. ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                PlayerAdapter playerAdapter = new PlayerAdapter(this, R.layout.onerowplayer, players);
                listView.setAdapter(playerAdapter);
            } else {
                Toast.makeText(this, "Error getting player, please try again later. ", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Player p = players.get(position);
            Intent i = new Intent(ActivityAllPlayers.this, ActivityPlayerProfile.class);
            i.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
            i.putExtra("username", p.getUsername());
            i.putExtra("request", "friend");
            i.putExtra("player", p);
            startActivity(i);
        });
    }

    private void initList() {
        db.collection("players").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Player player = document.toObject(Player.class);
                    players.add(player);
                }
                playerAdapter = new PlayerAdapter(ActivityAllPlayers.this, R.layout.onerowplayer, players);
                listView.setAdapter(playerAdapter);
            }
        });





        listView.setOnItemClickListener((parent, view, position, id) -> {
            Player player = players.get(position);

            if(getIntent().hasExtra("gameRequest")) {
                if(getIntent().getStringExtra("gameRequest").equals("team1")) {
                    Intent i = new Intent();
                    i.putExtra("playerChosen", player.getUsername());
                    i.putExtra("position", getIntent().getIntExtra("position", 1));
                    setResult(1, i);
                    finish();
                } else {
                    Intent i = new Intent();
                    i.putExtra("playerChosen", player.getUsername());
                    i.putExtra("position", getIntent().getIntExtra("position", 1));
                    setResult(2, i);
                    finish();
                }
            } else {
                Intent intent = new Intent(ActivityAllPlayers.this, ActivityPlayerProfile.class);
                intent.putExtra("username", player.getUsername());

                if (getIntent().hasExtra("admin")) {
                    intent.putExtra("admin", true);
                } else {
                    intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                }


                intent.putExtra("player", player);
                startActivity(intent);
            }
        });
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