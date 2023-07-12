package com.example.a12thproject.activities;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a12thproject.MusicService;
import com.example.a12thproject.NotificationBuilder;
import com.example.a12thproject.broadcasts.MyReceiver;
import com.example.a12thproject.maps.ActivityMap;
import com.example.a12thproject.Checker;
import com.example.a12thproject.classes.Player;
import com.example.a12thproject.firebase.PlayerFirebase;
import com.example.a12thproject.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivitySignup extends AppCompatActivity {

    Button register = null;
    ImageButton btnMap = null;
    EditText username = null;
    EditText password = null;
    EditText fullName = null;
    EditText email = null;
    EditText phone = null;
    EditText position = null;
    EditText age = null;
    EditText height = null;
    Uri selectedImage = null;
    String dob = null;
    RadioButton rbMale = null;
    RadioButton rbFemale = null;

    String location = "";


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storageReference;
    PlayerFirebase playerFirebase = null;


    Checker checker;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    String gender = "";

    Calendar c = Calendar.getInstance();

    GoogleMap map;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});


        playerFirebase = new PlayerFirebase();


        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        register = (Button) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.SUPASSWORD);
        fullName = (EditText) findViewById(R.id.fullname);
        phone = (EditText) findViewById(R.id.phone_signup);
        position = (EditText) findViewById(R.id.position);
        height = findViewById(R.id.height);



        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        register.setEnabled(true);



       rbMale.setOnCheckedChangeListener((listener, checked) -> {
            if (checked) {
                rbFemale.setChecked(false);
                gender = "male";
            }
       });
       rbFemale.setOnCheckedChangeListener((listener, checked) -> {
            if (checked) {
                rbMale.setChecked(false);
                gender = "female";
            }
         });

        register.setOnClickListener(v -> {

            // check first if username is already taken


                       try {


                           if(checkAllFields()) {
                               Player p = new Player();
                               p.setUsername(username.getText().toString());
                               p.setPassword(password.getText().toString());
                               p.setFullname(fullName.getText().toString());
                               p.setPhone(phone.getText().toString());
                               p.setPosition(position.getText().toString());
                               p.setGender(gender);
                               p.setNumRatings(0);
                               p.setRating(0);
                               p.setLocation("no location");
                               p.setHeight(height.getText().toString());
                               p.setTeam("no team");
                               p.setId(db.collection("players").document().getId());
                               // set the date by the user
                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                   p.setDate(String.valueOf((c.get(Calendar.YEAR) - datePickerDialog.getDatePicker().getYear())));
                               }
                               if(Integer.parseInt(p.getDate()) < 8) {
                                      Toast.makeText(ActivitySignup.this, "You must be at least 8 years old", Toast.LENGTH_SHORT).show();
                                      return;
                               }
                               p.setLocation(location);
                               try {
                                   playerFirebase.addPlayer(p);
                                   Toast.makeText(ActivitySignup.this, "Player added successfully", Toast.LENGTH_SHORT).show();
                                   Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                   i.putExtra("username", p.getUsername());
                                   i.putExtra("password", p.getPassword());

                               } catch (Exception e) {
                                   Toast.makeText(ActivitySignup.this, "Error adding player", Toast.LENGTH_SHORT).show();
                                   e.printStackTrace();
                               }

                               Intent intent = new Intent(ActivitySignup.this, ActivityUploadImage.class);
                               intent.putExtra("name", username.getText().toString());
                               startActivity(intent);
                           } else {
                                 Toast.makeText(ActivitySignup.this, "Please fill all fields properly", Toast.LENGTH_SHORT).show();
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }








        });
    }




    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month++;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = 3;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dob = String.valueOf(datePickerDialog.getDatePicker().getYear());
        }
    }
    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    public boolean checkAllFields() {
        checker = new Checker();

        if(password.getText().toString().isEmpty() || username.getText().toString().isEmpty() || fullName.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || position.getText().toString().isEmpty() || dob.isEmpty()) {
            return false;
        }
        String password_text = password.getText().toString();
        String phone_text = phone.getText().toString();
        String position_text = position.getText().toString();

        if(5 < Integer.parseInt(position_text) || 1 > Integer.parseInt(position_text)) {
            position.setError("Position must be between 1 and 5");
            return false;
        }

        checker.setStr(password.getText().toString());

        if(!checker.validPassword()) {
            password.setError("Password must be at least 8 characters long and contain at least one number and digit and start with a  capital letter and contain at least one special character");
            return false;
        }

        if(!(rbMale.isChecked() || rbFemale.isChecked())) {
            return false;
        }
       checker.setStr(phone_text);
        if(!checker.validPhone()) {
            phone.setError("Phone number must be 10 digits long");
            return false;
        }
        checker.setStr(height.getText().toString());
        if(!checker.validHeight()) {
            height.setError("Height must be in the format of centimeters");
            return false;
        }
        return true;
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

