package com.example.a12thproject.activities.profiles;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.example.a12thproject.Dialogs;
import com.example.a12thproject.MusicService;
import com.example.a12thproject.NotificationBuilder;
import com.example.a12thproject.R;

import com.example.a12thproject.SmsManager;
import com.example.a12thproject.activities.ActivityAdmin;
import com.example.a12thproject.activities.alllist.ActivityAllRatings;
import com.example.a12thproject.broadcasts.MyReceiver;
import com.example.a12thproject.classes.Court;
import com.example.a12thproject.classes.MapHelper;
import com.example.a12thproject.firebase.CourtFirebase;
import com.example.a12thproject.firebase.ReportFirebase;
import com.example.a12thproject.maps.ActivityMap;
import com.example.a12thproject.maps.ActivityMapNavigate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.checkerframework.checker.signature.qual.CanonicalNameOrEmpty;

import java.util.Objects;

public class ActivityCourtProfile extends AppCompatActivity {

    TextView name;
    TextView address;
    TextView phone;
    TextView website;

    TextView description;
    TextView author;

    Court new_court = new Court();


    String auth = null;


    Court curr = new Court();
    CourtFirebase cf = null;
    FirebaseFirestore db = null;

    CheckBox cb;


    MyReceiver internetStateReceiver;

    TextView tvAway;

    ReportFirebase rf = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_profile);


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {
            finish();
        });

        cf = new CourtFirebase();
        rf = new ReportFirebase();


        internetStateReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetStateReceiver, intentFilter);

        db = FirebaseFirestore.getInstance();


        name = findViewById(R.id.court_name);
        address = findViewById(R.id.court_address);
        phone = findViewById(R.id.court_phone);
        website = findViewById(R.id.court_website);
        description = findViewById(R.id.court_description);
        author = findViewById(R.id.court_author);
        cb = findViewById(R.id.checkBox);
        tvAway = findViewById(R.id.tvAway);


        curr = (Court) getIntent().getSerializableExtra("court");
        if (curr == null) {
            Toast.makeText(this, "Error loading court", Toast.LENGTH_SHORT).show();
            finish();
        }
        cb.setText(Boolean.parseBoolean(curr.getAvailable()) ? "available" : "unavailable");
        cb.setChecked(Boolean.parseBoolean(curr.getAvailable()));
        cb.setClickable(false);


        loadCourt();
        handleImage();
        gotoWebsite();
        gotoPhone();
        updateRating();


    }


    public void editCourtDialog() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_edit_court);

        d.show();
        d.setCancelable(true);

        EditText name = d.findViewById(R.id.edit_court_name);
        EditText phone = d.findViewById(R.id.edit_court_phone);
        EditText website = d.findViewById(R.id.edit_court_website3);
        EditText description = d.findViewById(R.id.edit_court_description);

        db.collection("courts").document(curr.getName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name.setText(document.getString("name"));
                        address.setText(document.getString("address"));
                        phone.setText(document.getString("phone"));
                        website.setText(document.getString("website"));
                        description.setText(document.getString("description"));
                    }
                }
            }
        });
        name.setFocusable(false);
        address.setClickable(false);
        address.setFocusable(false);
        address.setFocusableInTouchMode(false);


        Button save = d.findViewById(R.id.btn_edit_court_submit);
        Button delete = d.findViewById(R.id.btn_delete_court);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cf.delete(curr);
                    d.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityCourtProfile.this, "Error deleting court", Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Court new_court = new Court();

                    new_court.setName(name.getText().toString());
                    new_court.setLocation(address.getText().toString());
                    new_court.setPhone(phone.getText().toString());
                    new_court.setWebsite(website.getText().toString());
                    new_court.setDescription(description.getText().toString());

                    db.collection("players").document(curr.getAuthor()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (!getIntent().hasExtra("admin")) {
                                    String dstPhone = document.getString("phone");
                                    // random 6 digit code
                                    String code = String.valueOf((int) (Math.random() * 900000) + 100000);
                                    SmsManager.sendSms(getApplicationContext(), dstPhone, "your code is: " + code);

                                    Dialog sms_dialog = new Dialog(getApplicationContext());

                                    sms_dialog.setContentView(R.layout.dialog_sms_verify_update);
                                    sms_dialog.show();

                                    Button verify = sms_dialog.findViewById(R.id.btn_confirm_sms);
                                    EditText etCode = sms_dialog.findViewById(R.id.etSmsCode);

                                    verify.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String user_code = etCode.getText().toString();
                                            int codeInt = Integer.parseInt(user_code);
                                            if (codeInt == Integer.parseInt(code)) {
                                                cf.delete(curr);
                                                cf.insert(new_court);
                                                if (sms_dialog != null) {
                                                    sms_dialog.dismiss();
                                                }
                                                NotificationBuilder.createNotification(getApplicationContext(), "Court Updated", "Court " + curr.getName() + " has been updated");
                                                if (d != null) {
                                                    d.dismiss();
                                                }

                                            } else {
                                                Toast.makeText(ActivityCourtProfile.this, "Incorrect code", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                } else {
                                    cf.delete(curr);
                                    cf.insert(new_court);
                                    NotificationBuilder.createNotification(getApplicationContext(), "Court Updated", "Court " + curr.getName() + " has been updated");
                                    d.dismiss();
                                }


                            }
                        }
                    });

                    d.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityCourtProfile.this, "Error updating court", Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });

    }

    private void updateRating() {
        db.collection("courts").document(getIntent().getStringExtra("courtName")).collection("ratings").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                double sum = 0;
                int count = 0;
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    sum += Double.parseDouble(document.getString("rating"));
                    count++;
                }
                if (count == 0) {
                    return;
                }
                for (int i = 0; i < ((int) sum / count); i++) {
                    ImageView iv = findViewById(R.id.cstar1 + i);
                    iv.setImageResource(R.drawable.yellow_star);
                }
                curr.setRating(sum / count);
                curr.setNumRatings(count);
                db.collection("courts").document(getIntent().getStringExtra("courtName")).update("rating", sum);
                db.collection("courts").document(getIntent().getStringExtra("courtName")).update("numRatings", count);

            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        curr = new_court;
        onCreate(null);
    }

    private void reportDialog() {
        Dialogs.reportDialog(this, getIntent().getStringExtra("currUsername"), curr.getName(), "c");
    }

    private void reviewDialog() {

        db.collection("courts").document(curr.getName()).collection("ratings").document(getIntent().getStringExtra("currUsername")).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(this, "you already rated this court", Toast.LENGTH_SHORT).show();
            } else {
                String curr_rating = Dialogs.reviewDialog(this, getIntent().getStringExtra("currUsername"), curr.getName(), "c");
                curr_rating = Dialogs.RATING;
                try {
                    updateRating();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error updating rating", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private Location createLocationFromString(String locationString) {
        String[] coordinates = locationString.split(",");
        double latitude = Double.parseDouble(coordinates[0]);
        double longitude = Double.parseDouble(coordinates[1]);

        Location location = new Location("Current");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    private void loadCourt() {
        if (getIntent().hasExtra("request")) {

            name.setText("\t" + curr.getName());
            address.setText("\t" + MapHelper.getLocation(this, curr.getLocation()));
            phone.setText("\t" + curr.getPhone());
            website.setText(" " + curr.getWebsite());
            description.setText(curr.getDescription());
            author.setText("\t" + curr.getAuthor());
            auth = curr.getAuthor();




            Button acceptOrDecline = findViewById(R.id.BtnAcceptOrDecline);
            acceptOrDecline.setVisibility(View.VISIBLE);
            acceptOrDecline.setBackgroundColor(Color.GREEN);

            acceptOrDecline.setOnClickListener(v -> {
                Dialog d = new Dialog(this);
                d.setContentView(R.layout.dialog_accept_or_decline);

                d.show();
                d.setCancelable(true);

                Button accept = d.findViewById(R.id.btn_accept_dialog);
                Button decline = d.findViewById(R.id.btn_decline_dialog);

                accept.setOnClickListener(v1 -> {
                    try {
                        cf.insert(curr);
                        db.collection("courtRequests").document(getIntent().getStringExtra("courtName")).delete();

                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error accepting court", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(d != null && d.isShowing()) {
                        d.dismiss();
                    }


                });

                decline.setOnClickListener(v1 -> {
                    try {
                        db.collection("courtRequests").document(getIntent().getStringExtra("courtName")).delete();
                        db.collection("players").document(curr.getAuthor()).update("court", "no court");
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error declining court", Toast.LENGTH_SHORT).show();
                    }
                    if(d != null && d.isShowing()) {
                        d.dismiss();
                    }

                });
            });
        } else {

                name.setText(" " + curr.getName());
                address.setText(" " + MapHelper.getLocation(this, curr.getLocation()));
                phone.setText(" " + curr.getPhone());
                website.setText(" " + curr.getWebsite());
                author.setText(" " + curr.getAuthor());
                description.setText(curr.getDescription());
                auth = curr.getAuthor();

            android.location.LocationManager locationManager = (android.location.LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "please allow location permissions", Toast.LENGTH_SHORT).show();
                return;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location dest = createLocationFromString(curr.getLocation());

            tvAway.setText(calculateDistance(lastKnownLocation, dest) + " kilometers" + tvAway.getText().toString());



        }
    }


    private void gotoPhone() {
        phone.setClickable(true);
        phone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+curr.getPhone()));
            startActivity(intent);
        });

    }


    public double calculateDistance(Location location1, Location location2) {
        float distanceInMeters = location1.distanceTo(location2);
        double distanceInKm = distanceInMeters / 1000.0;
        return distanceInKm;
    }
    private void gotoWebsite() {
        website.setClickable(true);
        website.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(curr.getWebsite()));
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(curr.getWebsite()));
            startActivity(intent);
        });
    }



    private void handleImage() {
        ImageView imageView = findViewById(R.id.IVcourtProfilePicture);

        // load image from firebase storage
        // set image to imageview

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("imageCourts/"+curr.getName());

        final long ONE_MEGABYTE = 1024 * 1024;
        profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
            // put court image
            imageView.setImageResource(R.drawable.court);
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_court_profile, menu);

        if(getIntent().hasExtra("admin") || curr.getAuthor().equals(getIntent().getStringExtra("currUsername"))) {
                    menu.setGroupVisible(R.id.groupEdit, true);
                    menu.setGroupVisible(R.id.groupNoEdit, false);
        } else {
            menu.setGroupVisible(R.id.groupEdit, false);
            menu.setGroupVisible(R.id.groupNoEdit, true);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
           switch (item.getItemId()) {

               case R.id.mnuMusicOn:
                   Intent i = new Intent(this, MusicService.class);
                   startService(i);
                   return true;


               case R.id.itemEditCourt:
                   editCourtDialog();
                   return true;
               case R.id.itemAllCourtReviews:
                   Intent intent = new Intent(ActivityCourtProfile.this, ActivityAllRatings.class);
                   if(!getIntent().hasExtra("admin")) {
                       intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                   } else {
                       intent.putExtra("admin", true);
                   }
                   intent.putExtra("name", getIntent().getStringExtra("courtName"));
                   intent.putExtra("id", "c");
                   startActivity(intent);
                     return true;
               case R.id.itemReportCourt:
                     reportDialog();
                     return true;
               case R.id.itemAddCourtReview:
                     reviewDialog();
                     return true;
               default:
                   return super.onOptionsItemSelected(item);
           }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetStateReceiver);
    }
}
