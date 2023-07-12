package com.example.a12thproject.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.a12thproject.R;
import com.example.a12thproject.activities.alllist.ActivityAllCourts;
import com.example.a12thproject.activities.alllist.ActivityAllPlayers;
import com.example.a12thproject.activities.alllist.ActivityAllReports;
import com.example.a12thproject.activities.alllist.ActivityAllTeams;
import com.example.a12thproject.firebase.AdminManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

public class ActivityAdmin extends AppCompatActivity {

    FirebaseFirestore db = null;
    AdminManager af = null;

    String curr_pass = "";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    TextView admin_name;
    ImageView imageView7;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});


        imageView7 = findViewById(R.id.imageView7);
        admin_name = findViewById(R.id.admin_name);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
        };

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item selection here
                int itemId = item.getItemId();

                switch (itemId) {
                    case R.id.itemAllPlayersList:
                        Intent intent = new Intent(ActivityAdmin.this, ActivityAllPlayers.class);
                        intent.putExtra("admin", true);
                        startActivity(intent);
                        return true;
                    case R.id.itemAllCourtsList:
                        Intent intent2 = new Intent(ActivityAdmin.this, ActivityAllCourts.class);
                        intent2.putExtra("admin", true);
                        startActivity(intent2);
                        return true;
                    case R.id.itemAllTeamsList:
                        Intent intent3 = new Intent(ActivityAdmin.this, ActivityAllTeams.class);
                        intent3.putExtra("admin", true);
                        startActivity(intent3);
                        return true;
                    case R.id.itemAllReportsList:
                        Intent intent5 = new Intent(ActivityAdmin.this, ActivityAllReports.class);
                        intent5.putExtra("admin", true);
                        startActivity(intent5);
                        return true;
                    case R.id.itemGeneratePassword:
                        dialogGeneratePassword();

                        return true;
                    case R.id.itemAllCourtRequestsList:
                        Intent intent8 = new Intent(ActivityAdmin.this, ActivityAllCourts.class);
                        intent8.putExtra("admin", true);
                        intent8.putExtra("requests", true);
                        startActivity(intent8);
                        return true;
                    case R.id.itemAllTeamRequestsList:
                        Intent intent9 = new Intent(ActivityAdmin.this, ActivityAllTeams.class);
                        intent9.putExtra("admin", true);
                        intent9.putExtra("requests", true);
                        startActivity(intent9);
                        return true;
                    case R.id.logoutAdmin:
                        Intent intent6 = new Intent(ActivityAdmin.this, MainActivity.class);
                        startActivity(intent6);
                        return true;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        db = FirebaseFirestore.getInstance();
        af = new AdminManager();



        if(!getIntent().hasExtra("logged")) {
            checkAdminDialog();
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }



    }
    /*
    case R.id.itemAllPlayersList:
                Intent intent = new Intent(this, ActivityAllPlayers.class);
                intent.putExtra("admin", true);
                startActivity(intent);
                return true;
            case R.id.itemAllCourtsList:
                Intent intent2 = new Intent(this, ActivityAllCourts.class);
                intent2.putExtra("admin", true);
                startActivity(intent2);
                return true;
            case R.id.itemAllTeamsList:
                Intent intent3 = new Intent(this, ActivityAllTeams.class);
                intent3.putExtra("admin", true);
                startActivity(intent3);
                return true;
            case R.id.itemAllReportsList:
                Intent intent5 = new Intent(this, ActivityAllReports.class);
                intent5.putExtra("admin", true);
                startActivity(intent5);
                return true;
            case R.id.itemGeneratePassword:
                dialogGeneratePassword();

                return true;
            case R.id.itemAllCourtRequestsList:
                Intent intent8 = new Intent(this, ActivityAllCourts.class);
                intent8.putExtra("admin", true);
                intent8.putExtra("requests", true);
                startActivity(intent8);
                return true;
            case R.id.itemAllTeamRequestsList:
                Intent intent9 = new Intent(this, ActivityAllTeams.class);
                intent9.putExtra("admin", true);
                intent9.putExtra("requests", true);
                startActivity(intent9);
                return true;
            case R.id.logoutAdmin:
                Intent intent6 = new Intent(this, MainActivity.class);
                startActivity(intent6);
                return true;
     */
    private void dialogGeneratePassword() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_generate_password);
        d.show();
        d.setCancelable(true);

        TextView tv = d.findViewById(R.id.tvNewAdminPass);
        tv.setVisibility(View.INVISIBLE);

        Button generate = d.findViewById(R.id.btn_generate);
        final String[] new_pass = {""};
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new_pass[0] = af.generatePassword();
                    if(!new_pass[0].equals(""))
                        af.addPassword(getApplicationContext(), curr_pass, new_pass[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityAdmin.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ActivityAdmin.this, "Password generated", Toast.LENGTH_SHORT).show();
                tv.setVisibility(View.VISIBLE);
                tv.setText(tv.getText() + new_pass[0]);
                generate.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void checkAdminDialog() {


        Dialog d = new Dialog(this);
        d.setCancelable(false);
        d.setContentView(R.layout.dialog_checkadmin);
        d.show();

        EditText pass = d.findViewById(R.id.admin_password);

        Button check = d.findViewById(R.id.check_pass);
        String pass_text = pass.getText().toString();

        ImageButton goback = d.findViewById(R.id.btn_go_back);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAdmin.this, MainActivity.class);
                startActivity(intent);
            }
        });


        check.setOnClickListener(v -> {
            try {

                curr_pass = pass.getText().toString();
                db.collection("ADMIN_PASSWORDS").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            if (Objects.equals(document.getString("password"), pass.getText().toString())) {
                                d.dismiss();
                                return;
                            }
                        }
                        Toast.makeText(ActivityAdmin.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch(Exception e) {

            }

        });
    }


}
