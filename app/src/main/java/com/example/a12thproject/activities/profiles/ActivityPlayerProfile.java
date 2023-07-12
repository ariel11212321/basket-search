package com.example.a12thproject.activities.profiles;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;


import com.example.a12thproject.Dialogs;

import com.example.a12thproject.MusicService;
import com.example.a12thproject.R;
import com.example.a12thproject.SmsManager;
import com.example.a12thproject.activities.ActivityGame;
import com.example.a12thproject.activities.alllist.ActivityAllCourts;
import com.example.a12thproject.activities.alllist.ActivityAllPlayers;
import com.example.a12thproject.activities.alllist.ActivityAllRatings;
import com.example.a12thproject.classes.MapHelper;
import com.example.a12thproject.classes.Player;
import com.example.a12thproject.firebase.PlayerFirebase;
import com.example.a12thproject.maps.ActivityMap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;


public class ActivityPlayerProfile extends AppCompatActivity {

    FirebaseFirestore db = null;
    // load image from firebase storage
    ImageView imageView;
    StorageReference storageReference;

    String profileUsername = null;
    String currUsername = null;

    PlayerFirebase playerFirebase = new PlayerFirebase();



    String currRating = "";
    String numRatings = "";

    AtomicBoolean flag = new AtomicBoolean(false);

    Player curr = null;

    Button startGame;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});



        profileUsername = getIntent().getStringExtra("username");
        currUsername = getIntent().getStringExtra("currUsername");

        db = FirebaseFirestore.getInstance();

        TextView username = findViewById(R.id.pusername);
        TextView position = findViewById(R.id.pposition);
        TextView dob = findViewById(R.id.page);
        TextView team = findViewById(R.id.pteam);
        TextView phone = findViewById(R.id.pphone);
        TextView height = findViewById(R.id.pheight);
        TextView location = findViewById(R.id.plocation);
        startGame = findViewById(R.id.btn_start_game);


        curr = (Player) getIntent().getSerializableExtra("player");




        if(curr != null) {


                username.setText(" " + curr.getUsername());
                position.setText(" " + curr.getPosition());
                dob.setText(" " + curr.getDate());
                phone.setText(" " + curr.getPhone());
                height.setText(" " + curr.getHeight());
                location.setText(" " + MapHelper.getLocation(this, curr.getLocation()));
                team.setText(" " + curr.getTeam());


            if(getIntent().hasExtra("gameRequest")) {
                startGame.setText("add to game");

                startGame.setOnClickListener(v -> {
                    Toast.makeText(getApplicationContext(), "player game request sent", Toast.LENGTH_SHORT).show();
                    SmsManager.sendSms(getApplicationContext(), curr.getPhone(), "player has invited you to his game, go to your profile to accept");

                    finish();
                    finish();
                });
            }

                location.setClickable(true);
                location.setOnClickListener(v -> {
                    Intent intent = new Intent(ActivityPlayerProfile.this, ActivityMap.class);
                    intent.putExtra("location", curr.getLocation());
                    startActivity(intent);
                });

                if (!curr.getTeam().equals("No team")) {
                    team.setClickable(true);
                    team.setOnClickListener(v -> {
                        Intent intent = new Intent(ActivityPlayerProfile.this, ActivityTeamProfile.class);
                        intent.putExtra("teamName", curr.getTeam());
                        if (getIntent().hasExtra("admin")) {
                            intent.putExtra("admin", true);
                        } else {
                            intent.putExtra("currUsername", profileUsername);
                        }
                        startActivity(intent);
                    });
                }
                if (!((getIntent().hasExtra("admin") || currUsername.equals(profileUsername)))) {
                    int count = 0;
                    for (char c : location.getText().toString().toCharArray()) {
                        if (c == ',') {
                            count++;
                        }
                    }
                    if (count >= 2) {
                        String curr_location = location.getText().toString();
                        curr_location = curr_location.substring(curr_location.indexOf(","));
                        location.setText(curr_location);
                    }
                    startGame.setVisibility(View.INVISIBLE);
                } else {
                    startGame.setOnClickListener(v -> {
                        Intent intent = new Intent(ActivityPlayerProfile.this, ActivityGame.class);
                        intent.putExtra("currUsername", currUsername);
                        intent.putExtra("player", curr);
                        intent.putExtra("location", curr.getLocation());
                        startActivity(intent);
                    });
                }



                db.collection("players").document(profileUsername).collection("ratings").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        RatingBar bar = findViewById(R.id.ratingBarPlayer);

                        double sum = 0;
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            sum += Double.parseDouble((String) document.get("rating"));
                            count++;
                        }

                        if (count != 0) {
                            bar.setRating((float) (sum / count));
                        } else {
                            bar.setRating(0);
                        }
                    }
                });





            }




        viewRequest();
        loadPlayerPicture();


        phone.setClickable(true);
        phone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone.getText().toString()));
            startActivity(intent);
        });


    }
    private void updateRating() {
        db.collection("players").document(profileUsername).collection("ratings").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                double sum = 0;
                int count = 0;
                for(QueryDocumentSnapshot document : task.getResult()) {
                    sum += (double)document.get("rating");
                    count++;
                }
                   try {
                       db.collection("players").document(profileUsername).update("rating", sum / count);
                       db.collection("players").document(profileUsername).update("numRatings", count);
                   } catch (Exception e) {
                       Log.d("error", "error updating rating");
                       e.printStackTrace();
                   }
            }
        });
    }



    private void viewRequest() {
        if(getIntent().hasExtra("request") && getIntent().getStringExtra("request").equals("team")) {
            Button accept = findViewById(R.id.btn_accept_request);
            Button decline = findViewById(R.id.btn_decline_request);
            accept.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);


            accept.setOnClickListener(v -> {
                try {
                    db.collection("players").document(currUsername).update("team", getIntent().getStringExtra("teamName"));
                    db.collection("teams").document(getIntent().getStringExtra("teamName")).collection("players").document(profileUsername).set(new HashMap<>());
                    Toast.makeText(this, "Accepted request", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Log.d("error", "error accepting request");
                    Toast.makeText(this, "Error accepting request", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            decline.setOnClickListener(v -> {
                try {
                    db.collection("teams").document(getIntent().getStringExtra("teamName")).collection("teamRequests").document(profileUsername).delete();
                    Toast.makeText(this, "Declined request", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Log.d("error", "error declining request");
                    Toast.makeText(this, "Error declining request", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }


        else if(getIntent().hasExtra("request")) {
            Button accept = findViewById(R.id.btn_accept_request);
            Button decline = findViewById(R.id.btn_decline_request);
            accept.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);


            accept.setOnClickListener(v -> {
                try {
                    db.collection("players").document(currUsername).update("team", getIntent().getStringExtra("teamName"));
                    db.collection("teams").document(getIntent().getStringExtra("teamName")).collection("players").document(profileUsername).set(new HashMap<>());
                    Toast.makeText(this, "Accepted request", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Log.d("error", "error accepting request");
                    Toast.makeText(this, "Error accepting request", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            decline.setOnClickListener(v -> {
                try {
                    db.collection("teams").document(getIntent().getStringExtra("teamName")).collection("teamRequests").document(profileUsername).delete();
                    Toast.makeText(this, "Declined request", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Log.d("error", "error declining request");
                    Toast.makeText(this, "Error declining request", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });





                if(getIntent().getStringExtra("request").equals("friend")) {
                    accept.setOnClickListener(v1 -> {
                        try {
                            db.collection("players").document(currUsername).collection("friendRequests").document(profileUsername).delete();
                            playerFirebase.addFriend(currUsername, profileUsername);
                            Toast.makeText(this, "Accepted friend request from: " + profileUsername, Toast.LENGTH_SHORT).show();

                            finish();
                        } catch (Exception e) {
                            Toast.makeText(this, "Error accepting friend request", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                    decline.setOnClickListener(v1 -> {
                        try {
                            db.collection("players").document(currUsername).collection("friendRequests").document(profileUsername).delete();
                            playerFirebase.removeFriend(currUsername, profileUsername);
                            Toast.makeText(this, "Declined friend request from: " + profileUsername, Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(this, "Error, please try again later.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                }
        }
    }



    private boolean checkFriends() {
        AtomicBoolean isFriend = new AtomicBoolean(false);
        try {
            db.collection("players").document(currUsername).collection("friends").document(profileUsername).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        isFriend.set(true);
                    }
                }
            });
        } catch (Exception e) {
            return false;
        }
        return isFriend.get();
    }

    private boolean checkSentFriendRequest() {
        AtomicBoolean isSent = new AtomicBoolean(false);
        try {
            db.collection("players").document(profileUsername).collection("friendRequests").document(currUsername).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        isSent.set(true);
                    }
                }
            });
        } catch (Exception e) {
            return false;
        }
        return isSent.get();
    }

    private void sendFriendRequest() {
            try {
                db.collection("players").document(profileUsername).collection("friendRequests").document(currUsername).get().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful() && task1.getResult().exists()) {
                        Toast.makeText(this, "you already sent a friend request", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Friend request sent", Toast.LENGTH_SHORT).show();
                        db.collection("players").document(profileUsername).get().addOnCompleteListener(task -> {
                            if(task.isSuccessful() && task.getResult().exists()) {
                                String phone = task.getResult().getString("phone");
                                if(phone != null && !phone.isEmpty()) {
                                    android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                                    smsManager.sendTextMessage(phone, null, "you have a new friend request from: " + currUsername, null, null);
                                }
                            }
                        });
                    }
                });


            } catch (Exception e) {
                Toast.makeText(this, "Friend request failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
    }





    private void ratingDialog() {
        db.collection("players").document(profileUsername).collection("ratings").document(currUsername).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(this, "You have already rated this player", Toast.LENGTH_SHORT).show();
            } else {
            String rating;
            try {
                rating = Dialogs.reviewDialog(this, currUsername, profileUsername, "p");
                rating = Dialogs.RATING;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            updateRating();
        }
        });
    }

    private String getLocation(String location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());




        String[] latLngArray = curr.getLocation().split(",");

// Parse the latitude and longitude values as doubles
        double latitude = Double.parseDouble(latLngArray[0]);
        double longitude = Double.parseDouble(latLngArray[1]);

// Use the Geocoder to get the address
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String street = address.getAddressLine(0);
                String city = address.getLocality();
                String country = address.getCountryName();

                return street+","+city+","+country;

                // Do something with the street, city, and country values
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "no location";
    }
    private void loadPlayerPicture() {
        imageView = findViewById(R.id.player_profile_pic);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("images/"+profileUsername);
        final long ONE_MEGABYTE = 1024 * 1024;
        profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if(getIntent().hasExtra("admin") || getIntent().getStringExtra("currUsername").equals(getIntent().getStringExtra("username"))) {
            inflater.inflate(R.menu.menu_player_profile, menu);
        } else {
            inflater.inflate(R.menu.menu_player_profile_no_edit, menu);

            db.collection("players").document(profileUsername).collection("friends").document(currUsername).get().addOnCompleteListener(task -> {
               if(task.isSuccessful() && task.getResult().exists()) {
                   MenuItem item = menu.findItem(R.id.itemFriend);
                   item.setTitle("REMOVE FRIEND");
               }
            });

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(getIntent().hasExtra("admin") || currUsername.equals(profileUsername)) {
            switch (item.getItemId()) {
                case R.id.mnuMusicOn:
                    Intent i = new Intent(this, MusicService.class);
                    startService(i);
                    return true;

                case R.id.itemEditPlayer:

                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.POST_NOTIFICATIONS}, 1);
                        new Thread(() -> {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "please grant the permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    Dialogs.updatePlayerDialog(curr,this, false, profileUsername);
                    return true;
                    case R.id.itemAllFriendRequests:
                    Intent intent = new Intent(this, ActivityAllPlayers.class);
                    intent.putExtra("currUsername", profileUsername);
                    intent.putExtra("requests", true);
                    startActivity(intent);
                    return true;
                case R.id.itemAllFriends:
                    Intent intent1 = new Intent(this, ActivityAllPlayers.class);
                    if(getIntent().hasExtra("admin")) {
                        intent1.putExtra("admin", true);
                    }
                    intent1.putExtra("currUsername", profileUsername);
                    intent1.putExtra("friends", true);
                    startActivity(intent1);
                    return true;
                case R.id.itemAllCourtsOwned:
                    Intent intent2 = new Intent(this, ActivityAllCourts.class);
                    intent2.putExtra("owned", true);
                    if(getIntent().hasExtra("admin")) {
                        intent2.putExtra("admin", true);
                    } else {
                        intent2.putExtra("currUsername", profileUsername);
                    }
                    startActivity(intent2);
                    return true;

                case R.id.itemAllRatings:
                    Intent intent3 = new Intent(this, ActivityAllRatings.class);
                    intent3.putExtra("currUsername", profileUsername);
                    intent3.putExtra("name", profileUsername);
                    intent3.putExtra("id", "p");
                    startActivity(intent3);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {

            switch (item.getItemId()) {
                case R.id.mnuMusicOn:
                    Intent i = new Intent(this, MusicService.class);
                    startService(i);
                    return true;

                case R.id.itemAllRatings:
                    Intent intent = new Intent(this, ActivityAllRatings.class);
                    intent.putExtra("currUsername", profileUsername);
                    intent.putExtra("name", profileUsername);
                    intent.putExtra("id", "p");
                    startActivity(intent);
                    return true;
                case R.id.itemReport:
                    Dialogs.reportDialog(this, currUsername, profileUsername, "p");
                    return true;
                case R.id.itemRating:
                    ratingDialog();
                    return true;

                case R.id.itemAllCourtsOwned:
                    Intent intent2 = new Intent(this, ActivityAllCourts.class);
                    intent2.putExtra("currUsername", currUsername);
                    intent2.putExtra("owned", true);
                    if(getIntent().hasExtra("admin")) {
                        intent2.putExtra("admin", true);
                    }
                    startActivity(intent2);
                    return true;

                case R.id.itemFriend:
                    if(item.getTitle().equals("REMOVE FRIEND")) {
                        db.collection("players").document(profileUsername).collection("friends").document(currUsername).delete();
                        db.collection("players").document(currUsername).collection("friends").document(profileUsername).delete();
                    } else {
                        sendFriendRequest();
                    }


                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    }








}


