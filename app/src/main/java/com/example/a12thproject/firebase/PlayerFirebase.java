package com.example.a12thproject.firebase;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.a12thproject.classes.Court;
import com.example.a12thproject.classes.Player;
import com.example.a12thproject.classes.Team;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerFirebase implements OnCompleteListener<DocumentSnapshot> {

    private FirebaseFirestore db;
    private static final String COLLECTION_NAME = "players";
    AtomicReference<Player> p = new AtomicReference<>();


    public PlayerFirebase() {
        db = FirebaseFirestore.getInstance();
    }


    public void close() {
        db.terminate();
    }
    public void open() {
        db = FirebaseFirestore.getInstance();
    }


    public Player addPlayer(Player p) {
        try {
            db.collection(COLLECTION_NAME).document(p.getUsername()).set(p);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        p.setId(db.collection(COLLECTION_NAME).document(p.getUsername()).getId());
        return p;
    }



    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        open();
        db.collection(COLLECTION_NAME).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    players.add(document.toObject(Player.class));
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        close();

        return players;
    }

    public boolean removeFriendRequest(String username, String friendUsername) {
        try {
            db.collection(COLLECTION_NAME).document(username).collection("friendRequests").document(friendUsername).delete().addOnSuccessListener(aVoid -> {
                System.out.println("Friend request removed");
            }).addOnFailureListener(e -> {
                System.out.println("Friend request not removed");
                throw new RuntimeException(e);
            });
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean addFriend(String username, String friendUsername) {
        try {
            db.collection("players").document(username).collection("friends").document(friendUsername).set(new HashMap<>());
            db.collection("players").document(friendUsername).collection("friends").document(username).set(new HashMap<>());
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean removeFriend(String player1, String player2) {
        try {
            db.collection(COLLECTION_NAME).document(player1).collection("friends").document(player2).delete();
            db.collection(COLLECTION_NAME).document(player2).collection("friends").document(player1).delete();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }




    public void deletePlayer(Player p) {
        try {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReference("images/"+p.getUsername());
            ref.delete();

            db.collection("reports").whereEqualTo("to", p.getUsername()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                    for(DocumentSnapshot ds : task.getResult().getDocuments()) {
                        db.collection("reports").document(ds.getId()).delete();
                    }
                }
            });

            db.collection(COLLECTION_NAME).document(p.getUsername()).delete();
            db.collection(COLLECTION_NAME).document(p.getUsername()).collection("friendRequests").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection(COLLECTION_NAME).document(p.getUsername()).collection("friendRequests").document(document.getId()).delete();
                    }
                }
            });
            db.collection(COLLECTION_NAME).document(p.getUsername()).collection("friends").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection(COLLECTION_NAME).document(p.getUsername()).collection("friends").document(document.getId()).delete();
                    }
                }
            });
            // delete ratings collection
            db.collection(COLLECTION_NAME).document(p.getUsername()).collection("ratings").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection(COLLECTION_NAME).document(p.getUsername()).collection("ratings").document(document.getId()).delete();
                    }
                }
            });
            db.collection("teams").whereEqualTo("captain", p.getUsername()).get().addOnCompleteListener(task1 -> {
                if(task1.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task1.getResult()) {
                        Team t = document.toObject(Team.class);
                        db.collection("teams").document(t.getName()).delete();
                    }
                }
            });
            db.collection("courts").whereEqualTo("author", p.getUsername()).get().addOnCompleteListener(task1 -> {
                if(task1.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task1.getResult()) {
                        Court curr = document.toObject(Court.class);
                        db.collection("courts").document(curr.getName()).delete();
                    }
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                p.set(document.toObject(Player.class));

            } else {
                p.set(null);
            }
        }
    }
}


