package com.example.a12thproject.firebase;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdminManager {
    private FirebaseFirestore db = null;
    private static final String COLLECTION_NAME = "ADMIN_PASSWORDS";

    public AdminManager() {
        db = FirebaseFirestore.getInstance();
    }



    public String generatePassword() {
        return db.collection(COLLECTION_NAME).document().getId();
    }


    public boolean addPassword(Context c, String author, String password) {
        try {


            HashMap<String, String> map = new HashMap<>();
            map.put("password", password);
            map.put("author", author);

            db.collection(COLLECTION_NAME).document(author).set(map);



        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean removePassword(String password) {
        try {
            db.collection(COLLECTION_NAME).whereEqualTo("password", password).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        db.collection(COLLECTION_NAME).document(document.getId()).delete();
                    }
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean checkPassword(String password) {
        if(password.isEmpty()) {
            return false;
        }
        AtomicBoolean isExist = new AtomicBoolean(false);
        try {
            db.collection(COLLECTION_NAME).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if(Objects.equals(document.getString("password"), password)) {
                           isExist.set(true);
                        }
                    }
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return isExist.get();
    }
    public boolean checkById(String id) {
        try {
            return db.collection(COLLECTION_NAME).document(id).get().isSuccessful();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
