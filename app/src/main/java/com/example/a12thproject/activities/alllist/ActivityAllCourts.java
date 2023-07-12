package com.example.a12thproject.activities.alllist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a12thproject.Dialogs;
import com.example.a12thproject.MusicService;
import com.example.a12thproject.R;
import com.example.a12thproject.activities.profiles.ActivityCourtProfile;
import com.example.a12thproject.adapters.CourtAdapter;
import com.example.a12thproject.classes.Court;

import com.example.a12thproject.classes.MapHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class ActivityAllCourts extends AppCompatActivity {

    CourtAdapter adapter;
    ArrayList<Court> courts;
    FirebaseFirestore db = null;
    ImageButton filter = null;
    ListView listView;

    Button btnSearch;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_all_courts);

        db = FirebaseFirestore.getInstance();


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});

        listView = (ListView) findViewById(R.id.listViewCourts);
        filter = findViewById(R.id.btnFilterCourts);
        btnSearch = findViewById(R.id.btnSearchCourts);

        courts = new ArrayList<Court>();





      if(getIntent().hasExtra("owned")) {
          initOwned();
      }
      else if(getIntent().hasExtra("requests")) {
          initRequest();
      } else {
            init();
      }


        filter.setOnClickListener(v -> {
            filterDialog();
        });





    }

    public void initOwned() {
            courts = new ArrayList<>();
            db.collection("courts").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        Court c = doc.toObject(Court.class);
                        if(c.getAuthor().equals(getIntent().getStringExtra("currUsername"))) {
                            courts.add(c);
                        }
                    }
                    adapter = new CourtAdapter(this, R.layout.onerowcourt, courts);
                    listView.setAdapter(adapter);


                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Intent intent = new Intent(this, ActivityCourtProfile.class);
                        intent.putExtra("courtName", courts.get(position).getName());
                        if(getIntent().hasExtra("admin") || courts.get(position).getAuthor().equals(getIntent().getStringExtra("currUsername"))) {
                            intent.putExtra("owned", true);
                        } else {
                            intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                        }

                        intent.putExtra("court", courts.get(position));
                        startActivity(intent);
                    });
                }
            });
    }

    public void initRequest() {
            courts = new ArrayList<>();


            db.collection("courtRequests").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        Court c = doc.toObject(Court.class);
                        courts.add(c);
                    }
                    CourtAdapter adapter = new CourtAdapter(this, R.layout.onerowcourt, courts);
                    listView.setAdapter(adapter);
                }
            });
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(this, ActivityCourtProfile.class);
                intent.putExtra("courtName", courts.get(position).getName());
                if(getIntent().hasExtra("admin")) {
                    intent.putExtra("admin", true);
                } else {
                    intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                }
                intent.putExtra("request", true);
                intent.putExtra("court", courts.get(position));
                startActivity(intent);
            });

    }
    public void init() {
            courts = new ArrayList<>();
            db.collection("courts").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Court c = document.toObject(Court.class);
                        courts.add(c);
                    }
                    adapter = new CourtAdapter(this, R.layout.onerowcourt, courts);
                    listView.setAdapter(adapter);
                }
            });


            listView.setOnItemClickListener((parent, view, position, id) -> {

                if(getIntent().hasExtra("gameRequest")) {
                    Intent i = new Intent();
                    if(!Boolean.parseBoolean(courts.get(position).getAvailable())) {
                        Toast.makeText(getApplicationContext(), "this court is not available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i.putExtra("courtChosen", courts.get(position));
                    setResult(5, i);
                    finish();
                } else {

                    Intent intent = new Intent(this, ActivityCourtProfile.class);
                    intent.putExtra("courtName", courts.get(position).getName());
                    if (getIntent().hasExtra("admin")) {
                        intent.putExtra("admin", true);
                    } else {
                        intent.putExtra("currUsername", getIntent().getStringExtra("currUsername"));
                    }
                    intent.putExtra("court", courts.get(position));


                    startActivity(intent);
                }
            });


            filter.setOnClickListener(v -> {
                filterDialog();
            });
    }


    private void filterDialog() {
        String[] filterOptions = {"name", "rating"};
        Dialogs.filterDialog(this, filterOptions, courts);

        courts.clear();

        btnSearch.setOnClickListener(v -> {
            String[] filterValues = Dialogs.FILTER_VALUES;
            ArrayList<String> filterOptionsList = Dialogs.FILTER_OPTIONS;

            if(filterOptionsList == null || filterValues == null) {
                if(getIntent().hasExtra("requests")) {
                    initRequest();
                }
                else if(getIntent().hasExtra("owned")) {
                    initOwned();
                }
                else {
                    init();
                }
            }


            if(getIntent().hasExtra("requests")) {
                db.collection("courtRequests").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Court> filteredCourts = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Court c = document.toObject(Court.class);
                            boolean b = true;

                            for(int i = 0; i < filterOptionsList.size(); i++) {
                                if(filterOptionsList.get(i).equals("distance")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        courts.sort(new Comparator<Court>() {
                                            @Override
                                            public int compare(Court o1, Court o2) {
                                                return Double.compare(distance(getIntent().getStringExtra("location"), o1.getLocation()),  distance(getIntent().getStringExtra("location"), o2.getLocation()));
                                            }
                                        });
                                    }
                                }
                                if(!(filterOptionsList.get(i).equals("country") || filterOptionsList.get(i).equals("city")) && (!c.getLocation().contains(filterValues[i]))) {
                                    b = false;
                                }
                                if(!Objects.requireNonNull(document.get(filterOptionsList.get(i))).toString().contains(filterValues[i])) {
                                    b = false;
                                }

                            }
                            if(b) {
                                filteredCourts.add(c);
                            }
                        }

                        adapter = new CourtAdapter(this, R.layout.onerowcourt, filteredCourts);
                        listView.setAdapter(adapter);
                    } else {

                    }
                });

            }   else if(getIntent().hasExtra("owned")) {
                db.collection("courts").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Court> filteredCourts = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Court c = document.toObject(Court.class);
                            boolean b = c.getAuthor().equals(getIntent().getStringExtra("currUsername"));

                            for(int i = 0; i < filterOptionsList.size() && i < filterValues.length; i++) {
                                if(filterOptionsList.get(i).equals("distance")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        courts.sort(new Comparator<Court>() {
                                            @Override
                                            public int compare(Court o1, Court o2) {
                                                return Double.compare(distance(getIntent().getStringExtra("location"), o1.getLocation()),  distance(getIntent().getStringExtra("location"), o2.getLocation()));
                                            }
                                        });
                                    }
                                }
                                if((filterOptionsList.get(i).equals("country") || filterOptionsList.get(i).equals("city")) && (!MapHelper.getLocation(this, c.getLocation()).contains(filterValues[i]))) {
                                    b = false;
                                }
                                if(!Objects.requireNonNull(document.get(filterOptionsList.get(i))).toString().contains(filterValues[i])) {
                                    b = false;
                                }

                            }
                            if(b) {
                                filteredCourts.add(c);
                            }
                        }

                        adapter = new CourtAdapter(this, R.layout.onerowcourt, filteredCourts);
                        listView.setAdapter(adapter);
                    } else {

                    }
                });
            }

            else {
                db.collection("courts").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot document : task.getResult()) {
                            Court c = document.toObject(Court.class);
                            boolean b = true;



                            for(int i = 0; i < filterOptionsList.size(); i++) {
                                if(c == null) {
                                    continue;
                                }
                                else if(filterOptionsList.get(i).equals("country") || filterOptionsList.get(i).equals("city")) {
                                    if(!c.getLocation().contains(filterValues[i])) {
                                        b = false;
                                    }
                                }
                                else if(!Objects.requireNonNull(document.get(filterOptionsList.get(i))).toString().contains(filterValues[i])) {
                                    b = false;
                                }

                            }
                            if(b) {
                                courts.add(c);
                            }
                        }
                        adapter = new CourtAdapter(this, R.layout.onerowcourt, courts);
                        listView.setAdapter(adapter);
                    }
                });
            }

        });


    }



    private double distance(String loc1, String loc2) {
        String[] loc1Arr = loc1.split(",");
        String[] loc2Arr = loc2.split(",");
        double lat1 = Double.parseDouble(loc1Arr[0]);
        double lon1 = Double.parseDouble(loc1Arr[1]);
        double lat2 = Double.parseDouble(loc2Arr[0]);
        double lon2 = Double.parseDouble(loc2Arr[1]);

        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        double distance = results[0] / 1000.0; // convert to kilometers

        return distance;
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