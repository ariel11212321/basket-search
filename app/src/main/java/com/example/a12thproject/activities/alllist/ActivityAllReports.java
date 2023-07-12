package com.example.a12thproject.activities.alllist;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12thproject.Dialogs;
import com.example.a12thproject.MusicService;
import com.example.a12thproject.R;
import com.example.a12thproject.activities.profiles.ActivityTeamProfile;
import com.example.a12thproject.activities.profiles.ActivityCourtProfile;
import com.example.a12thproject.activities.profiles.ActivityPlayerProfile;
import com.example.a12thproject.adapters.ReportAdapter;
import com.example.a12thproject.classes.Report;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityAllReports extends AppCompatActivity {
    ImageButton filter;
    Button search;

    ListView list;
    ArrayList<Report> reports;
    ReportAdapter adapter;
    FirebaseFirestore db = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);



        ImageButton back = findViewById(R.id.btnBack1);
        back.setOnClickListener(v -> {finish();});


        db = FirebaseFirestore.getInstance();

        list = findViewById(R.id.listViewReports);

        reports = new ArrayList<>();

        db.collection("reports").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Report r = new Report();
                    r.setComment(document.getString("comment"));
                    r.setFrom(document.getString("from"));
                    r.setTo(document.getString("to"));
                    r.setType(document.getString("type"));
                    r.setId(document.getId());
                    r.setForWho(document.getString("forWho"));
                    reports.add(r);
                }
                adapter = new ReportAdapter(this, R.layout.onerowplayer, reports);
                list.setAdapter(adapter);
            }
        });
        list.setOnItemClickListener((parent, view, position, id) -> {
            Report r = reports.get(position);

            Dialog d = new Dialog(this);
            d.setContentView(R.layout.show_report_dialog);
            d.setTitle("Report");
            d.show();


            TextView from = d.findViewById(R.id.tv_report_from);
            TextView to = d.findViewById(R.id.tv_report_to);
            TextView type = d.findViewById(R.id.tv_report_type);
            TextView comment = d.findViewById(R.id.tv_report_comment);
            TextView forWho = d.findViewById(R.id.tv_report_forwho);

            Button btnDeleteReport = d.findViewById(R.id.btnDeleteReport);

            btnDeleteReport.setOnClickListener(v -> {
                db.collection("reports").document(r.getId()).delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Report deleted", Toast.LENGTH_SHORT).show();
                        d.dismiss();
                    }
                });
            });


            r.setForWho(r.getForWho().equals("p") ? "Player" : r.getForWho().equals("t") ? "Team" : "Court");

            from.setText(from.getText() + r.getFrom());
            to.setText(to.getText() + r.getTo());
            type.setText(type.getText() + r.getType());
            comment.setText(comment.getText() + r.getComment());
            forWho.setText(forWho.getText() + r.getForWho());

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