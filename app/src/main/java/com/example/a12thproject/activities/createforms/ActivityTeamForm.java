package com.example.a12thproject.activities.createforms;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.a12thproject.HomeActivity;
import com.example.a12thproject.MusicService;
import com.example.a12thproject.NotificationBuilder;
import com.example.a12thproject.R;

import com.example.a12thproject.activities.ActivitySignup;
import com.example.a12thproject.activities.ActivityUploadImage;
import com.example.a12thproject.broadcasts.MyReceiver;
import com.example.a12thproject.classes.MapHelper;
import com.example.a12thproject.classes.Team;
import com.example.a12thproject.firebase.TeamFirebase;
import com.example.a12thproject.maps.ActivityMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActivityTeamForm extends AppCompatActivity {

    Team t = null;

    TeamFirebase tf = null;
    FirebaseFirestore db = null;


    String location = "";
    MyReceiver internetStateReceiver;

    ImageButton btnChooseLocationTeam;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                location = data.getStringExtra("location");
            } else {
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_form);


        MyReceiver internetStateReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetStateReceiver, intentFilter);

        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});


        db = FirebaseFirestore.getInstance();
        tf = new TeamFirebase();

        db.collection("players").document(getIntent().getStringExtra("currUsername")).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(!task.getResult().getString("team").toLowerCase(Locale.ROOT).equals("no team")) {
                    Toast.makeText(this, "You already on a team", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });


        EditText name = (EditText) findViewById(R.id.etTeamName);


        ImageView image = (ImageView) findViewById(R.id.teamImage);
        image.setClickable(true);

        image.setOnClickListener(v -> {
                    if (name.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "please choose a name before proceeding to take a picture.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    db.collection("teams").document(name.getText().toString()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            Toast.makeText(getApplicationContext(), "there is already a team with this name, please choose another one.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(this, ActivityUploadImage.class);
                            i.putExtra("name", name.getText().toString());
                            i.putExtra("team", true);
                            startActivity(i);
                        }
                    });
        });

        btnChooseLocationTeam = (ImageButton) findViewById(R.id.btnChooseLocationTeam);
        btnChooseLocationTeam.setOnClickListener(v -> {
            if(MyReceiver.ENABLED) {
                Intent i = new Intent(this, ActivityMap.class);
                i.putExtra("name", name.getText().toString());
                i.putExtra("team", true);
                startActivityForResult(i, 1);
            } else {
                NotificationBuilder.createNotification(getApplicationContext(), "no internet", "you cant access map features without internet");
            }
        });



        ImageButton request = (ImageButton) findViewById(R.id.requestTeam);


        request.setOnClickListener(v -> {

                if(name.getText().toString().isEmpty()) {
                    Toast.makeText(this, "please choose a name for your team", Toast.LENGTH_SHORT).show();
                }

                if(location.equals("")) {
                    Toast.makeText(this, "Please choose a location", Toast.LENGTH_SHORT).show();
                    return;
                }

                t = new Team();

                t.setRating(0);
                t.setNumRatings(0);
                t.setName(name.getText().toString());
                t.setCaptain(getIntent().getStringExtra("currUsername"));
                t.setLocation(location);



                db.collection("players").document(getIntent().getStringExtra("currUsername")).update("team", t.getName());
                completeTeamForm();


        });


    }
    void completeTeamForm() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.complete_team_form);



        TextView name = (TextView) d.findViewById(R.id.tvNameTeam);
        TextView captain = (TextView) d.findViewById(R.id.tvCaptainTeam);
        TextView location = (TextView) d.findViewById(R.id.tvLocationTeam);

        name.setText(t.getName());
        captain.setText(t.getCaptain());
        location.setText(MapHelper.getLocation(this, t.getLocation()));



        ImageView iv = (ImageView) d.findViewById(R.id.teamPic);

        d.setCancelable(false);
        d.show();


        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("imageTeams/"+t.getName());
        final long ONE_MEGABYTE = 1024 * 1024;
        profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            iv.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
            iv.setImageBitmap(null);
        });

        Button submit = (Button) d.findViewById(R.id.btn_submit_team);
        submit.setOnClickListener(v -> {
            tf.insertRequest(this, t);
            Intent intent = new Intent(ActivityTeamForm.this, HomeActivity.class);
            intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
            startActivity(intent);

        });

        ImageButton back = d.findViewById(R.id.ibBack);
        back.setOnClickListener(v -> {
            d.dismiss();
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
