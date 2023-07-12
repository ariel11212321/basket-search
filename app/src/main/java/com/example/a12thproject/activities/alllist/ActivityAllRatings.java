package com.example.a12thproject.activities.alllist;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.a12thproject.Dialogs;
import com.example.a12thproject.MusicService;
import com.example.a12thproject.R;
import com.example.a12thproject.adapters.RatingAdapter;
import com.example.a12thproject.classes.Rating;
import com.example.a12thproject.widgets.Item;
import com.example.a12thproject.widgets.MultiSelectionSpinner;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class  ActivityAllRatings extends AppCompatActivity {

    ListView listView;
    RatingAdapter adapter;
    FirebaseFirestore db = null;
    ArrayList<Rating> ratings = new ArrayList<>();

    String path;


    Button search;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);


        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});


        db = FirebaseFirestore.getInstance();



        listView = findViewById(R.id.listViewReviews);







         path = "";

        if(getIntent().getStringExtra("id").equals("p")) {
            path = "players";
        }
        else if(getIntent().getStringExtra("id").equals("c")) {
            path = "courts";
        }
       else {
           path = "teams";
        }
        db.collection(path).document(getIntent().getStringExtra("name")).collection("ratings").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Rating r = document.toObject(Rating.class);
                    ratings.add(r);
                }
                adapter = new RatingAdapter(ActivityAllRatings.this, R.layout.onerowrating, ratings);
                listView.setAdapter(adapter);
            }
        });


        String finalPath = path;
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Dialog dialog = new Dialog(ActivityAllRatings.this);
            dialog.setContentView(R.layout.dialog_rating);
            dialog.show();
            Rating r = ratings.get(position);
            TextView to = dialog.findViewById(R.id.tvRatingTo);
            to.setText(r.getTo());
            TextView from = dialog.findViewById(R.id.tvRatingFrom);

            TextView comment = dialog.findViewById(R.id.tvRatingComment);
            comment.setText("comment: " + r.getComment());

            from.setText(from.getText().toString() + r.getFrom());





            RatingBar rate = dialog.findViewById(R.id.ratingBarRate);
            rate.setIsIndicator(true);
            rate.setRating(Float.parseFloat(r.getRating()));




        });



    }
    private void filterDialog() {
        String[] FILTER_OPTIONS = {"rating", "from", "comment"};
        Dialogs.filterDialog(this, FILTER_OPTIONS, ratings);

        search.setOnClickListener(v -> {
            ratings.clear();
            ArrayList<String> options = Dialogs.FILTER_OPTIONS;
            String[] values = Dialogs.FILTER_VALUES;

            db.collection(path).document(getIntent().getStringExtra("name")).collection("ratings").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Rating r = document.toObject(Rating.class);
                        boolean b = true;

                        if(r == null) {
                            continue;
                        }

                        for(int i = 0; i < options.size(); i++) {
                            if(!Objects.requireNonNull(document.get(options.get(i))).toString().contains(values[i])) {
                                b = false;
                                break;
                            }
                        }

                        if(b) {
                            ratings.add(r);
                        }

                    }
                    adapter = new RatingAdapter(ActivityAllRatings.this, R.layout.onerowrating, ratings);
                    listView.setAdapter(adapter);
                }
            });


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
