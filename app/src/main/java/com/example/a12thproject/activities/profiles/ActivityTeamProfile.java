package com.example.a12thproject.activities.profiles;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.a12thproject.Dialogs;
import com.example.a12thproject.MusicService;
import com.example.a12thproject.R;
import com.example.a12thproject.activities.ActivityUploadImage;
import com.example.a12thproject.classes.Player;
import com.example.a12thproject.classes.Team;
import com.example.a12thproject.activities.alllist.ActivityAllPlayers;
import com.example.a12thproject.activities.alllist.ActivityAllRatings;
import com.example.a12thproject.firebase.TeamFirebase;
import com.example.a12thproject.widgets.KeyPairBoolData;
import com.example.a12thproject.widgets.MultiSpinnerSearch;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ActivityTeamProfile extends AppCompatActivity {

    private TextView tvTeamName;
    private TextView tvTeamCaptain;

    private Spinner spTeamPlayers;
    private Team team = new Team();
    private TeamFirebase teamFirebase;
    private ImageView ivTeamImage;
    private TextView tvTeamRating;


    FirebaseFirestore db = null;


    private Button report;
    private Button edit;
    private Button team_join_requests;
    private Button request_to_join;


    Button leave;
    Button allReviews;




    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_team_profile);


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});


        db = FirebaseFirestore.getInstance();

        teamFirebase = new TeamFirebase();


        tvTeamName = findViewById(R.id.teamName);
        tvTeamCaptain = findViewById(R.id.teamCaptain);

        ivTeamImage = findViewById(R.id.teamImage);
        spTeamPlayers = findViewById(R.id.teamPlayersSpinner);


        edit = findViewById(R.id.btn_edit_team);
        request_to_join = findViewById(R.id.btn_request_join_team);
        team_join_requests = findViewById(R.id.btn_team_join_requests);

        leave = findViewById(R.id.btn_leave_team);

        report = findViewById(R.id.report_team_btn);



        team = (Team) getIntent().getSerializableExtra("team");


        if(team == null) {
            Toast.makeText(this, "Error loading team", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initAttributes();
        initImage();
        initSpinner();
        reportDialog();

    }







    private void reportDialog() {
        report = findViewById(R.id.report_team_btn);
        report.setOnClickListener(v -> {
            Dialogs.reportDialog(this, getIntent().getStringExtra("currUsername"), team.getName(), "t");


        });
    }

    private void initAttributes() {

        tvTeamName.setText(" " + team.getName());
        tvTeamCaptain.setText("captain: " + team.getCaptain());

        if(getIntent().hasExtra("request")) {





            Button accept = findViewById(R.id.accept);
            Button decline = findViewById(R.id.decline);

            decline.setVisibility(Button.VISIBLE);
            accept.setVisibility(Button.VISIBLE);


            accept.setOnClickListener(task -> {
                try {
                    db.collection("players").document(team.getCaptain()).get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            DocumentSnapshot document = task1.getResult();
                            if(document.exists()) {
                                Player player = document.toObject(Player.class);
                                if(!player.getTeam().equals(team.getName())) {
                                    Toast.makeText(this, "Captain already has a team", Toast.LENGTH_SHORT).show();
                                    db.collection("teamRequests").document(team.getName()).delete();
                                    return;
                                } else {
                                    teamFirebase.addTeam(team);
                                    db.collection("teamRequests").document(team.getName()).delete();
                                    Toast.makeText(this, "Team accepted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "error accepting team", Toast.LENGTH_SHORT).show();
                }

                finish();
            });
            decline.setOnClickListener(v -> {
                try {
                    db.collection("teamRequests").document(team.getName()).delete();

                    db.collection("players").document(team.getCaptain()).get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                Player player = document.toObject(Player.class);
                                if(player.getTeam().equals(team.getName())) {
                                    player.setTeam("no team");
                                    db.collection("players").document(player.getUsername()).set(player);
                                }
                            }
                        }
                    });

                    Toast.makeText(this, "Request declined", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "error declining team", Toast.LENGTH_SHORT).show();
                }
                finish();
            });


            edit.setVisibility(View.VISIBLE);
            team_join_requests.setVisibility(View.INVISIBLE);
            request_to_join.setVisibility(View.INVISIBLE);



        } else {
                if(getIntent().hasExtra("admin") || getIntent().getStringExtra("currUsername").equals(team.getCaptain())) {
                    team_join_requests.setOnClickListener(task1 -> {
                        Intent intent = new Intent(this, ActivityAllPlayers.class);
                        if(getIntent().hasExtra("admin")) {
                            intent.putExtra("admin",  true);
                        }
                        else {
                            intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                        }
                        intent.putExtra("teamJoinRequests", true);
                        intent.putExtra("teamName", getIntent().getStringExtra("teamName"));

                        startActivity(intent);
                    });
                    team_join_requests.setVisibility(View.VISIBLE);
                    request_to_join.setVisibility(View.INVISIBLE);
                    report.setVisibility(View.INVISIBLE);





                    edit.setOnClickListener(v -> {
                        editTeamDialog();
                    });

                }
                else {
                    edit.setVisibility(View.INVISIBLE);

                    request_to_join.setOnClickListener(task1 -> {
                        teamFirebase.addJoinRequest(team, getIntent().getStringExtra("currUsername"));
                        Toast.makeText(this, "Request sent", Toast.LENGTH_SHORT).show();
                    });
                    team_join_requests.setVisibility(View.INVISIBLE);
                    request_to_join.setVisibility(View.VISIBLE);

                }
        }

    }

    private void editTeamDialog() {
        edit = findViewById(R.id.btn_edit_team);
        edit.setOnClickListener(v -> {
            Dialog d = new Dialog(this);
            d.setContentView(R.layout.dialog_edit_team);
            d.show();



            Button delete = d.findViewById(R.id.btnDelete);
            delete.setOnClickListener(v1 -> {
                try {
                    // delete the team from the database entirely, not only the fields
                    db.collection("teams").document(team.getName()).delete();

                    FirebaseStorage.getInstance().getReference("imageTeams/"+team.getName()).delete();

                    db.collection("reports").whereEqualTo("to", team.getName()).get().addOnCompleteListener(task -> {
                        if(task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                            for(DocumentSnapshot ds : task.getResult().getDocuments()) {
                                db.collection("reports").document(ds.getId()).delete();
                            }
                        }
                    });

                    // delete all the  coolections inside the team document
                    db.collection("teams").document(team.getName()).collection("joinRequests").get().addOnCompleteListener(task1 -> {
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            db.collection("teams").document(team.getName()).collection("joinRequests").document(document.getId()).delete();
                        }
                    });
                    db.collection("teams").document(team.getName()).collection("players").get().addOnCompleteListener(task1 -> {
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            db.collection("teams").document(team.getName()).collection("players").document(document.getId()).delete();
                        }
                    });


                    Toast.makeText(this, "Team deleted", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "error deleting team", Toast.LENGTH_SHORT).show();
                }
            });










        });

    }



    private void initSpinner() {



        db.collection("teams").document(team.getName()).collection("players").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> playersArray = new ArrayList<>();
                playersArray.add("team players");
                for(QueryDocumentSnapshot document : task.getResult()) {
                    playersArray.add(document.getId());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, playersArray);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spTeamPlayers.setAdapter(adapter);

                spTeamPlayers.setSelection(0, false);


                spTeamPlayers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String player = spTeamPlayers.getSelectedItem().toString();
                        db.collection("players").document(player).get().addOnCompleteListener(task -> {



                           if(task.isSuccessful() && task.getResult().exists()) {
                               Player p = task.getResult().toObject(Player.class);
                               Intent i = new Intent(getApplicationContext(), ActivityPlayerProfile.class);

                               if(p.getUsername().equals(getIntent().getStringExtra("currUsername"))) {
                                   leave.setVisibility(View.VISIBLE);

                                   leave.setOnClickListener(v -> {
                                       teamFirebase.leaveTeam(team, getIntent().getStringExtra("currUsername"));
                                       Toast.makeText(getApplicationContext(), "You have left the team", Toast.LENGTH_SHORT).show();
                                       finish();

                                       if(team.getCaptain().equals(getIntent().getStringExtra("currUsername"))) {
                                           teamFirebase.deleteTeam(team);
                                           finish();
                                       }


                                   });
                               }

                               i.putExtra("player", p);
                               i.putExtra("username", p.getUsername());
                               if(getIntent().hasExtra("admin")) {
                                      i.putExtra("admin", true);
                               } else {
                                   i.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                               }
                               startActivity(i);
                           }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            } else {
                Toast.makeText(this, "Error loading players", Toast.LENGTH_SHORT).show();
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


    private void initImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("imageTeams/" + getIntent().getStringExtra("teamName"));
        final long ONE_MEGABYTE = 1024 * 1024;
        profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ivTeamImage.setImageBitmap(bmp);
        }).addOnFailureListener(exception -> {
            exception.printStackTrace();
        });

    }
}
