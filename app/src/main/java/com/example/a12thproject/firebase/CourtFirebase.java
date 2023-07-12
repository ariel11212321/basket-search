package com.example.a12thproject.firebase;

import android.content.Context;
import android.widget.Toast;

import com.example.a12thproject.classes.Court;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class CourtFirebase {

    private FirebaseFirestore db;


    public void insertRequest(Context c, Court court) {
       try {



           String id = db.collection("courts").document().getId();
           court.setId(id);
           db.collection("courtRequests").document(court.getName()).set(court).addOnCompleteListener(task1 -> {
               if(task1.isSuccessful()) {
                   Toast.makeText(c, "Court request sent", Toast.LENGTH_SHORT).show();
               }
           }).addOnFailureListener(e -> {
               e.printStackTrace();
           });
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public CourtFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    public void insert(Court c) {
        try {
            c.setId(db.collection("courts").document().getId());
            db.collection("courts").document(c.getName()).set(c);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean update(Court c) {
        try {
            db.collection("courts").document(c.getName()).set(c);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean delete(String name) {
        try {
            db.collection("courts").document(name).delete();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean delete(Court c) {
        try {
            db.collection("courts").document(c.getName()).delete();
            db.collection("courtRequests").document(c.getName()).delete();

            FirebaseStorage.getInstance().getReference("imageCourts/"+c.getName()).delete();

            db.collection("reports").whereEqualTo("to", c.getName()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                    for(DocumentSnapshot ds : task.getResult().getDocuments()) {
                        db.collection("reports").document(ds.getId()).delete();
                    }
                }
            });

            db.collection("courts").document(c.getName()).collection("ratings").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        document.getReference().delete();
                    }
                }
            });



        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }




}