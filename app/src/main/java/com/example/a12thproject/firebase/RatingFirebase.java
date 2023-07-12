package com.example.a12thproject.firebase;

import com.example.a12thproject.classes.Rating;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

public class RatingFirebase {
    private FirebaseFirestore db = null;
    private static String COLLECTION_NAME = "ratings";

    public RatingFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    public boolean alreadyRated(String from, String to, String forWho) {
        AtomicBoolean alreadyRated = new AtomicBoolean(false);
        db.collection(COLLECTION_NAME).whereEqualTo("from", from).whereEqualTo("to", to).whereEqualTo("forWho", forWho).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    System.err.println("Already rated");
                    alreadyRated.set(true);
                } else {
                    System.err.println("Not rated");
                    alreadyRated.set(false);
                }
            }
        });
        return alreadyRated.get();
    }

    public void deleteAll() {
        db.collection("players").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    db.collection("players").document(document.getId()).collection("ratings").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            for (DocumentSnapshot document1 : task1.getResult()) {
                                db.collection("players").document(document.getId()).collection("ratings").document(document1.getId()).delete();
                            }
                        }
                    });
                }
            }
        });
        // do same for teams db
        db.collection("teams").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    db.collection("teams").document(document.getId()).collection("ratings").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            for (DocumentSnapshot document1 : task1.getResult()) {
                                db.collection("teams").document(document.getId()).collection("ratings").document(document1.getId()).delete();
                            }
                        }
                    });
                }
            }
        });
        db.collection("courts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    db.collection("courts").document(document.getId()).collection("ratings").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            for (DocumentSnapshot document1 : task1.getResult()) {
                                db.collection("courts").document(document.getId()).collection("ratings").document(document1.getId()).delete();
                            }
                        }
                    });
                }
            }
        });
    }


    public Rating addRating(Rating r) {
        r.setId(db.collection(COLLECTION_NAME).document().getId());
        try {
           switch (r.getForWho()) {
               case "p":
                   db.collection("players").document(r.getTo()).collection("ratings").document(r.getFrom()).set(r);
                   break;
               case "t":
                     db.collection("teams").document(r.getTo()).collection("ratings").document(r.getFrom()).set(r);
                     break;
               case "c":
                        db.collection("courts").document(r.getTo()).collection("ratings").document(r.getFrom()).set(r);
                        break;
           }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return r;

    }
    public Rating getRating(String id) {
        Rating r = new Rating();
        db.collection(COLLECTION_NAME).document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                r.setId(task.getResult().getId());
                r.setFrom(task.getResult().getString("from"));
                r.setTo(task.getResult().getString("to"));
                r.setRating(task.getResult().getString("rating"));
                r.setComment(task.getResult().getString("comment"));
            }
        });
        return r;
    }
    public boolean deleteRating(Rating r) {
        try {
            db.collection(COLLECTION_NAME).document(r.getId()).delete();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updateRating(Rating r) {
        try {
            db.collection(COLLECTION_NAME).document(r.getId()).set(r);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
