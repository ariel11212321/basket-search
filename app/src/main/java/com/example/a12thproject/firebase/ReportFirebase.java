package com.example.a12thproject.firebase;

import android.util.Log;
import android.widget.Toast;

import com.example.a12thproject.classes.Report;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ReportFirebase {

    private final FirebaseFirestore db;
    private static final String COLLECTION_NAME = "reports";

    public ReportFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    public boolean addReport(Report report) {
        try {
            report.setId(db.collection(COLLECTION_NAME).document().getId());
            db.collection(COLLECTION_NAME).document(report.getId()).set(report);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public ArrayList<Report> getAllReports() {
        ArrayList<Report> reports = new ArrayList<>();
        db.collection(COLLECTION_NAME).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Report report = Report.mapToReport(document.getData());
                    reports.add(report);
                }
            }
        });
        return reports;
    }

    public boolean delete(Report report) {
        try {
            db.collection(COLLECTION_NAME).document(report.getId()).delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean update(Report report) {
        try {
            db.collection(COLLECTION_NAME).document(report.getId()).set(report);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getByName(String name, String forWho) {
        AtomicReference<String> id = new AtomicReference<>("");
        db.collection(COLLECTION_NAME).whereEqualTo("name", name).whereEqualTo("forWho", forWho).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Report r = Report.dsToReport(document);
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
        return id.get();
    }

    public Report getById(String id) {
        AtomicReference<Report> report = new AtomicReference<>();
        db.collection(COLLECTION_NAME).whereEqualTo("id", id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                report.set(Report.mapToReport(Objects.requireNonNull(document.getData())));
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
        return report.get();
    }


}
