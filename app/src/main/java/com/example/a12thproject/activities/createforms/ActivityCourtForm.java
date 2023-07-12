package com.example.a12thproject.activities.createforms;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.a12thproject.Checker;
import com.example.a12thproject.HomeActivity;
import com.example.a12thproject.MusicService;
import com.example.a12thproject.NotificationBuilder;
import com.example.a12thproject.R;
import com.example.a12thproject.activities.ActivitySignup;
import com.example.a12thproject.activities.ActivityUploadImage;
import com.example.a12thproject.broadcasts.MyReceiver;
import com.example.a12thproject.classes.Court;
import com.example.a12thproject.classes.MapHelper;
import com.example.a12thproject.firebase.CourtFirebase;
import com.example.a12thproject.maps.ActivityMap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class ActivityCourtForm extends AppCompatActivity {
    ImageView img = null;
    Uri selectedImage = null;
    EditText name = null;
    TextView address = null;
    EditText phone = null;
    EditText website = null;
    EditText description = null;
    ImageView image = null;

    Court c = null;

    String location = "";


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CourtFirebase cf = null;

    Checker checker = null;

    MyReceiver internetStateReceiver;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_form);


        internetStateReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetStateReceiver, intentFilter);


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});

         cf = new CourtFirebase();



         name = (EditText) findViewById(R.id.tvNameTeam);
         address = (TextView) findViewById(R.id.address);
         phone = (EditText) findViewById(R.id.position2);
         website = (EditText) findViewById(R.id.website);
         description = (EditText) findViewById(R.id.description);
         image = (ImageView) findViewById(R.id.image);


         address.setOnClickListener(v -> {



             if(MyReceiver.ENABLED) {
                 Intent i = new Intent(this, ActivityMap.class);
                 if(getIntent().hasExtra("admin")) {
                     i.putExtra("admin", true);
                 } else {
                     i.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                 }
                 startActivityForResult(i, 1);
             } else {
                 NotificationBuilder.createNotification(getApplicationContext(), "no internet", "you cant access map features without internet");
             }

         });


        // add a button to save the court to the database
        ImageButton save =  findViewById(R.id.createCourtBtn);
        img = (ImageView) findViewById(R.id.courtPic);


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "please choose a name before proceeding to take a picture.", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("courts").document(name.getText().toString()).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult().exists()) {
                        Toast.makeText(getApplicationContext(), "there is already a court with this name, please choose another one.", Toast.LENGTH_SHORT).show();
                     } else {
                        Intent intent = new Intent(ActivityCourtForm.this, ActivityUploadImage.class);
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("court", true);
                        startActivity(intent);
                    }
                });
            }
        });


        save.setOnClickListener(v -> {
            // create a new court object




            try {
                if(checkFields()) {

                    if(location.isEmpty()) {
                        Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    c = new Court();
                    c.setName(name.getText().toString());

                    c.setPhone(phone.getText().toString());
                    c.setWebsite(website.getText().toString());
                    c.setDescription(description.getText().toString());
                    c.setNumRatings(0);
                    c.setRating(0);
                    c.setAuthor(getIntent().getStringExtra("currUsername"));
                    c.setLocation(location);
                    c.setAvailable("true");
                    // add the court to the database
                    completeDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error, please try again later", Toast.LENGTH_SHORT).show();
                finish();
            }






        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Result code is OK, retrieve the string value from the Intent
                location = data.getStringExtra("location");

            } else {

            }
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


    public boolean checkFields() {
        checker = new Checker();
        checker.setStr(phone.getText().toString());

        // if something is empty, return false
        if (name.getText().toString().isEmpty() || address.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || website.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        // if the phone number is not 10 digits, return false
        if (!checker.validPhone()) {
            phone.setError("Invalid Phone Number");
            return false;
        }

        checker.setStr(website.getText().toString());

        if (!checker.validWebsite()) {
            website.setError("Invalid Website");
            return false;
        }
        return true;
    }

    public void completeDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.complete_court_form);


        dialog.setCancelable(true);
        dialog.show();


        TextView name = dialog.findViewById(R.id.courtName);
        name.setText(c.getName());
        TextView address = dialog.findViewById(R.id.courtAddress);
        address.setText(MapHelper.getLocation(this, c.getLocation()));
        TextView phone = dialog.findViewById(R.id.courtPhone);
        phone.setText(c.getPhone());
        TextView website = dialog.findViewById(R.id.courtWebsite);
        website.setText(c.getWebsite());
        TextView description = dialog.findViewById(R.id.courtDescription);
        description.setText(c.getDescription());



        ImageView iv = (ImageView) dialog.findViewById(R.id.courtPicture);




        Button submit = dialog.findViewById(R.id.btn_submit_court);
        submit.setOnClickListener(v -> {

            db.collection("courtRequests").document(c.getName()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Toast.makeText(getApplicationContext(), "Court already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("courts").document(c.getName()).get().addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful() && task2.getResult().exists()) {
                                Toast.makeText(getApplicationContext(), "Court already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                cf.insertRequest(getApplicationContext(), c);
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                }
            });


        });




        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("imageCourts/"+name.getText().toString());
        final long ONE_MEGABYTE = 1024 * 1024;
        profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            iv.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
            iv.setImageBitmap(null);
        });



    }


}
