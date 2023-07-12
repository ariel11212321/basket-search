package com.example.a12thproject.firebase;

import android.content.Context;
import android.widget.Toast;

import com.example.a12thproject.classes.Player;
import com.example.a12thproject.classes.Team;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;


public class TeamFirebase {
    FirebaseFirestore db;
    public TeamFirebase() {
        db = FirebaseFirestore.getInstance();
    }
    public boolean addTeam(Team t) {
        try {
            t.setId(db.collection("teams").document().getId());
            db.collection("teams").document(t.getName()).set(t);
            db.collection("players").document(t.getCaptain()).update("team", t.getName());
            db.collection("teams").document(t.getName()).collection("players").document(t.getCaptain()).set(new HashMap<>());
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean leaveTeam(Team t, String username) {
        try {
            if(t.getCaptain().equals(username)) {
                db.collection("teams").document(t.getName()).delete();
            } else {
                db.collection("teams").document(t.getName()).collection("players").document(username).delete();
                db.collection("players").document(username).update("team", "no team");
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean addToTeam(Team t, Player p) {
        try {
            db.collection("teams").document(t.getName()).collection("players").document(p.getUsername()).set(new HashMap<>());
            db.collection("players").document(p.getUsername()).update("team", t.getName());
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean insertRequest(Context c, Team t) {
        try {
            t.setId(db.collection("teams").document().getId());
            db.collection("teamRequests").document(t.getName()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Toast.makeText(c, "Team already exists", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        db.collection("teams").document(t.getName()).get().addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful() && task2.getResult().exists()) {
                                Toast.makeText(c, "Team already exists", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                try {
                                    db.collection("players").document(t.getCaptain()).update("team", t.getName());
                                    db.collection("teams").document(t.getName()).collection("players").document(t.getCaptain()).set(new HashMap<>());
                                    db.collection("teamRequests").document(t.getName()).set(t);
                                    Toast.makeText(c, "Request sent", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean isCaptain(String username) {
        AtomicBoolean flag = new AtomicBoolean(false);
        db.collection("teams").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot doc : task.getResult()) {
                    String captain = doc.getString("captain");
                    if(captain.equals(username)) {
                        flag.set(true);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
        return flag.get();
    }

    public  void removeTeam(Team t) {
        db.collection("teams").document(t.getName()).delete();
    }
    public boolean updateTeam(Team t) {
       return db.collection("teams").document(t.getName()).set(t).isSuccessful();
    }
    public  boolean deleteTeam(Team t) {
        try {
            db.collection("teams").document(t.getName()).delete();
            db.collection("teams").document(t.getName()).collection("players").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot doc : task.getResult()) {
                        db.collection("players").document(doc.getId()).update("team", "no team");
                        db.collection("teams").document(t.getName()).collection("players").document(doc.getId()).delete();
                    }
                }
            });
            db.collection("teams").document(t.getName()).collection("joinRequests").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot doc : task.getResult()) {
                        db.collection("teams").document(t.getName()).collection("joinRequests").document(doc.getId()).delete();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public Team getByName(String name) {
        Team t = new Team();
        t.setId("-1");

        try {
            db.collection("teams").document(name).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    t.setName(task.getResult().getString("name"));
                    t.setCaptain(task.getResult().getString("captain"));
                    t.setId(task.getResult().getId());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return t.getId().equals("-1") ? null : t;
    }
    public boolean addJoinRequest(Team t, String username) {
        try {
            db.collection("teams").document(t.getName()).collection("joinRequests").document(username).set(new HashMap<>());
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}