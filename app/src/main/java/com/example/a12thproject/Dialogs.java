package com.example.a12thproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.a12thproject.activities.MainActivity;
import com.example.a12thproject.activities.profiles.ActivityPlayerProfile;
import com.example.a12thproject.classes.Court;
import com.example.a12thproject.classes.Player;
import com.example.a12thproject.classes.Rating;
import com.example.a12thproject.classes.Report;
import com.example.a12thproject.classes.Team;
import com.example.a12thproject.firebase.CourtFirebase;
import com.example.a12thproject.firebase.PlayerFirebase;
import com.example.a12thproject.firebase.RatingFirebase;
import com.example.a12thproject.firebase.ReportFirebase;
import com.example.a12thproject.firebase.TeamFirebase;
import com.example.a12thproject.widgets.Item;
import com.example.a12thproject.widgets.KeyPairBoolData;
import com.example.a12thproject.widgets.MultiSelectionSpinner;
import com.example.a12thproject.widgets.MultiSpinnerListener;
import com.example.a12thproject.widgets.MultiSpinnerSearch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dialogs {

    public static String RATING = "";


    public static ArrayList<String> FILTER_OPTIONS;
    public static String[] FILTER_VALUES;


    private static PlayerFirebase playerFirebase = new PlayerFirebase();
    private static TeamFirebase teamFirebase = new TeamFirebase();
    private static CourtFirebase courtFirebase = new CourtFirebase();
    private static ReportFirebase reportFirebase = new ReportFirebase();
    private static RatingFirebase ratingFirebase = new RatingFirebase();










    public static Pair<ArrayList<String>, String[]> filterDialog(Context c, String[] filterOptions, List<?> list) {

            Dialog dialog = new Dialog(c);
            dialog.setContentView(R.layout.filter_activity);
            dialog.setCancelable(true);


            dialog.setTitle("Filter");
            dialog.show();
            dialog.setCancelable(true);

            dialog.setOnCancelListener(dialog1 -> {
                FILTER_OPTIONS = null;
                FILTER_VALUES = null;
            });

        MultiSpinnerSearch spinner = dialog.findViewById(R.id.filterSpinner);
        FILTER_OPTIONS = new ArrayList<>();

        ArrayList<KeyPairBoolData> items = new ArrayList<>();
        for(String s : filterOptions) {
            items.add(new KeyPairBoolData(s, false));
        }
        spinner.setSearchEnabled(true);
        spinner.setSearchHint("filter options");

        spinner.setEmptyTitle("No options");

        spinner.setShowSelectAllButton(true);
        spinner.setClearText("Close & Clear");

        spinner.setItems(items, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });




        Button filter = dialog.findViewById(R.id.btnSubmit);

        EditText filterValues = dialog.findViewById(R.id.etFilterValues);




            filter.setOnClickListener(v -> {
                List<KeyPairBoolData> ans = spinner.getSelectedItems();
                FILTER_OPTIONS.clear();
                for(KeyPairBoolData i: ans) {
                    FILTER_OPTIONS.add(i.getName());
                }

                String filterValuesText = filterValues.getText().toString();



                if(!filterValuesText.contains(",")) {
                    FILTER_VALUES = new String[]{filterValuesText};
                }
                else {
                    FILTER_VALUES = filterValuesText.split("[,]", 0);
                }
                if(FILTER_VALUES.length != FILTER_OPTIONS.size()) {
                    Toast.makeText(c, "Please enter the same number of values as options", Toast.LENGTH_SHORT).show();
                    return;
                }

                list.clear();
                dialog.dismiss();
            });

            return new Pair<>(FILTER_OPTIONS, FILTER_VALUES);

    }



    public static void updatePlayerDialog(Player p, Activity c, boolean onlyUsernameAndPass, String name) {
        Dialog d = new Dialog(c);
        d.setContentView(R.layout.dialog_edit_player);
        d.setTitle("Edit Player");
        d.setCancelable(true);
        d.show();

        EditText etUsername = d.findViewById(R.id.etUsername);
        EditText etPassword = d.findViewById(R.id.etPassword);
        EditText etPhone = d.findViewById(R.id.etPhone);
        EditText etName = d.findViewById(R.id.etFullname);
        EditText etGender = d.findViewById(R.id.etGender);
        EditText etDate = d.findViewById(R.id.etDate);
        EditText etHeight = d.findViewById(R.id.etHeight);
        EditText etTeam = d.findViewById(R.id.etTeam);
        EditText etPosition = d.findViewById(R.id.etPosition);



        Button submitEditBtn = d.findViewById(R.id.submitEditBtn);



        FirebaseFirestore db = FirebaseFirestore.getInstance();



        if(onlyUsernameAndPass) {
            etPhone.setVisibility(View.GONE);
            etName.setVisibility(View.GONE);
            etGender.setVisibility(View.GONE);
            etDate.setVisibility(View.GONE);
            etHeight.setVisibility(View.GONE);
            etTeam.setVisibility(View.GONE);
            etPosition.setVisibility(View.GONE);



            d.findViewById(R.id.btn_delete_player).setVisibility(View.GONE);

            etUsername.setText(p.getUsername());
            etUsername.setEnabled(false);




            Checker checker = new Checker(etPassword.getText().toString());

            submitEditBtn.setOnClickListener(v1 -> {
                checker.setStr(etPassword.getText().toString());
                if(etPassword.getText().toString().equals("") || etUsername.getText().toString().equals("")) {
                    Toast.makeText(c, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                    if(!checker.validPassword()) {
                        Toast.makeText(c, "Password must be at least 8 characters long and contain at least one number and digit and start with a  capital letter and contain at least one special character", Toast.LENGTH_SHORT).show();
                        return;
                    }

                String charset = "0123456789";
                Random random = new Random();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    int index = random.nextInt(charset.length());
                    sb.append(charset.charAt(index));
                }


                android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                smsManager.sendTextMessage(p.getPhone(), null, "your code is: " + sb.toString(), null, null);

                d.setContentView(R.layout.dialog_sms_verify_update);
                d.show();
                EditText sms_code = d.findViewById(R.id.etSmsCode);
                Button verify = d.findViewById(R.id.btn_confirm_sms);



                verify.setOnClickListener(v2 -> {
                    if(sms_code.getText().toString().equals("")) {
                        Toast.makeText(c, "Please enter the code", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(sms_code.getText().toString().equals(sb.toString())) {
                        p.setUsername(etUsername.getText().toString());
                        p.setPassword(etPassword.getText().toString());


                        db.collection("players").document(p.getUsername()).set(p);
                        Toast.makeText(c, "password changed", Toast.LENGTH_SHORT).show();

                        if(d != null) {
                            d.dismiss();
                        }
                    } else {
                        Toast.makeText(c, "Wrong code", Toast.LENGTH_SHORT).show();
                    }
                });






            });

        } else {

            Checker checker = new Checker();

           etUsername.setText(name);
           etUsername.setEnabled(false);

           db.collection("players").document(name).get().addOnCompleteListener(task -> {
                try {
                    etPhone.setText(task.getResult().get("phone").toString());
                    etPassword.setText(task.getResult().get("password").toString());
                    etName.setText(task.getResult().get("fullname").toString());
                    etGender.setText(task.getResult().get("gender").toString());
                    etDate.setText(task.getResult().get("date").toString());
                    etHeight.setText(task.getResult().get("height").toString());
                    etTeam.setText(task.getResult().get("team").toString());
                    etTeam.setEnabled(false);
                    etPosition.setText(task.getResult().getString("position").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(c, "Failed getting player, try again later.", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                }
           });


            Button deletePlayer = d.findViewById(R.id.btn_delete_player);

            deletePlayer.setOnClickListener(v1 -> {
                db.collection("players").document(etUsername.getText().toString()).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(!task.getResult().exists()) {
                            etUsername.setError("Username does not exist");
                        } else {
                            try {
                                p.setUsername(etUsername.getText().toString());

                                playerFirebase.deletePlayer(p);
                                Toast.makeText(c, "Player deleted successfully", Toast.LENGTH_SHORT).show();
                                d.dismiss();


                                db.collection("teams").whereEqualTo("captain", p.getUsername()).get().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()) {
                                        for(QueryDocumentSnapshot document : task1.getResult()) {
                                            Team t = document.toObject(Team.class);
                                            teamFirebase.deleteTeam(t);
                                        }
                                    }
                                });
                                db.collection("courts").whereEqualTo("author", p.getUsername()).get().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()) {
                                        for(QueryDocumentSnapshot document : task1.getResult()) {
                                            Court curr = document.toObject(Court.class);
                                            courtFirebase.delete(curr);
                                        }
                                    }
                                });

                                Intent i = new Intent(c, MainActivity.class);
                                c.startActivity(i);

                            } catch(Exception e) {
                                e.printStackTrace();
                                Toast.makeText(c, "Failed deleting player, try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(c, "Failed deleting player, try again later.", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                });

                if(!c.getIntent().hasExtra("admin")) {

                } else {

                }

            });


            submitEditBtn.setOnClickListener(v -> {
                // check if any is empty
                if(etUsername.getText().toString().equals("") || etPassword.getText().toString().equals("") || etPhone.getText().toString().equals("") || etName.getText().toString().equals("") || etGender.getText().toString().equals("") || etDate.getText().toString().equals("") || etHeight.getText().toString().equals("") || etTeam.getText().toString().equals("") || etPosition.getText().toString().equals("")) {
                    Toast.makeText(c, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                checker.setStr(etPassword.getText().toString());
                if(!checker.validPassword()) {
                    Toast.makeText(c, "Password must be at least 8 characters long and contain at least one number and digit and start with a  capital letter and contain at least one special character", Toast.LENGTH_SHORT).show();
                    return;
                }

                checker.setStr(etPhone.getText().toString());
                if(!checker.validPhone()) {
                    Toast.makeText(c, "Phone number must be 10 digits long", Toast.LENGTH_SHORT).show();
                    return;
                }
                checker.setStr(etHeight.getText().toString());
                if(!checker.validHeight()) {
                    Toast.makeText(c, "Height must be in the format of 1.80", Toast.LENGTH_SHORT).show();
                    return;
                }
                checker.setStr(etPosition.getText().toString());
                if(!checker.checkPosition()) {
                    Toast.makeText(c, "Position must be one of the following: PG, SG, SF, PF, C", Toast.LENGTH_SHORT).show();
                    return;
                }

                Player new_player = new Player();
                new_player.setUsername(etUsername.getText().toString());
                new_player.setPassword(etPassword.getText().toString());
                new_player.setPhone(etPhone.getText().toString());
                new_player.setFullname(etName.getText().toString());
                new_player.setGender(etGender.getText().toString());
                new_player.setDate(etDate.getText().toString());
                new_player.setHeight(etHeight.getText().toString());
                new_player.setTeam(etTeam.getText().toString());
                new_player.setPosition(etPosition.getText().toString());
                new_player.setTeam(etTeam.getText().toString());
                new_player.setRating(p.getRating());
                new_player.setNumRatings(p.getNumRatings());


                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference oldRef = storage.getReference().child("images/"+p.getUsername());
                StorageReference newRef = storage.getReference().child("images/"+new_player.getUsername());

                oldRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        newRef.putFile(oldRef.getDownloadUrl().addOnCompleteListener(task -> {}).getResult()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // File uploaded successfully. Now delete the old file.
                                oldRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Old file deleted successfully.
                                        Log.d(TAG, "File moved successfully.");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to delete the old file.
                                        Log.e(TAG, "Error deleting old file: " + e.getMessage());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to upload the file.
                                Log.e(TAG, "Error uploading file: " + e.getMessage());
                            }
                        });
                    }
                });

                if(!c.getIntent().hasExtra("admin")) {
                    String charset = "0123456789";
                    Random random = new Random();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        int index = random.nextInt(charset.length());
                        sb.append(charset.charAt(index));
                    }


                    String code = SmsManager.sendSms(c, p.getPhone(), sb.toString());

                    d.setContentView(R.layout.dialog_sms_verify_update);
                    d.show();
                    EditText sms_code = d.findViewById(R.id.etSmsCode);
                    Button verify = d.findViewById(R.id.btn_confirm_sms);

                    verify.setOnClickListener(v1 -> {
                        if(sms_code.getText().toString().equals("")) {
                            Toast.makeText(c, "Please enter the code", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(sms_code.getText().toString().equals(code)) {
                            playerFirebase.deletePlayer(p);
                            playerFirebase.addPlayer(new_player);
                            NotificationBuilder.createNotification(c, "player updated successfully", "you can go to your profile and see the changes.");
                            d.dismiss();
                        } else {
                            Toast.makeText(c, "Wrong code", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String charset = "0123456789";
                    Random random = new Random();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        int index = random.nextInt(charset.length());
                        sb.append(charset.charAt(index));
                    }


                    String code = SmsManager.sendSms(c, p.getPhone(), sb.toString());

                    d.setContentView(R.layout.dialog_sms_verify_update);
                    d.show();
                    EditText sms_code = d.findViewById(R.id.etSmsCode);
                    Button verify = d.findViewById(R.id.btn_confirm_sms);

                    verify.setOnClickListener(v1 -> {
                        if(sms_code.getText().toString().equals("")) {
                            Toast.makeText(c, "Please enter the code", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(sms_code.getText().toString().equals(code)) {
                            playerFirebase.deletePlayer(p);
                            playerFirebase.addPlayer(new_player);
                            NotificationBuilder.createNotification(c, "player updated successfully", "you can go to your profile and see the changes.");
                            d.dismiss();
                        } else {
                            Toast.makeText(c, "Wrong code", Toast.LENGTH_SHORT).show();
                        }
                    });
                }




            });


        }
    }

    public static boolean playerProfileDialog(Context c, Player p, String name, String option) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       try {
           Dialog d = new Dialog(c);
           d.setContentView(R.layout.dialog_player_profile);
           d.show();

           EditText etName = d.findViewById(R.id.dialog_player_name);
           EditText username = d.findViewById(R.id.dialog_player_username);
           EditText team = d.findViewById(R.id.dialog_player_team);
           EditText phone = d.findViewById(R.id.dialog_player_phone);
           EditText rating = d.findViewById(R.id.dialog_player_rating);
           EditText pos = d.findViewById(R.id.dialog_player_position);

           Button accept = d.findViewById(R.id.accept_btn);
           Button decline = d.findViewById(R.id.decline_btn);


           AtomicBoolean accepted = new AtomicBoolean(false);

           if(option.equals("friends")) {
               Toast.makeText(c, "You are already friends with this player", Toast.LENGTH_SHORT).show();
               accept.setOnClickListener(v -> {
                   Toast.makeText(c, "Friend added successfully", Toast.LENGTH_SHORT).show();
                   db.collection("players").document("name").collection("friends").document(p.getUsername()).set(p);
                   db.collection("players").document(p.getUsername()).collection("friends").document("name").set(p);
                   d.dismiss();
                   accepted.set(true);
               });

               decline.setOnClickListener(v -> {
                   db.collection("players").document(name).collection("friendRequests").document(p.getUsername()).delete();
                     Toast.makeText(c, "Friend request declined", Toast.LENGTH_SHORT).show();
                   d.dismiss();
                   accepted.set(false);
               });
           } else if(option.equals("team")) {
               accept.setOnClickListener(v -> {
                   // add the player to the team and remove from joinRequets array

                   db.collection("teams").document(name).collection("joinRequests").document(p.getUsername()).delete();
                   db.collection("players").document(p.getUsername()).update("team", name);
                   db.collection("teams").document(name).collection("players").document(p.getUsername()).set(p);


                   d.dismiss();
                   accepted.set(true);
               });

               decline.setOnClickListener(v -> {

                   d.dismiss();
                   accepted.set(false);

               });
           }
           else {
               Toast.makeText(c, "Failed to open dialog", Toast.LENGTH_SHORT).show();

           }
           return accepted.get();

       } catch(Exception e) {
              e.printStackTrace();
              return false;
       }

    }
    private static int onStarTouch(Dialog d, int n) {
        for(int i = 0; i <= n; i++) {
            ImageView star = d.findViewById(R.id.ratingstar1 + i);
            star.setImageResource(R.drawable.yellow_star);
        }
        for(int i = n + 1; i < 5; i++) {
            ImageView star = d.findViewById(R.id.ratingstar1 + i);
            star.setImageResource(R.drawable.white_star);
        }

        return n;
    }


    public static String reviewDialog(Context c, String from, String to, String forWho) {


        Dialog d = new Dialog(c);
        d.setContentView(R.layout.rating_dialog);

        final int[] rating = new int[1];

        d.show();
        d.setCancelable(true);

        ImageView[] stars = new ImageView[5];
        for(int i = 0; i < 5; i++) {
            stars[i] = d.findViewById(R.id.ratingstar1 + i);
        }
        for(int i = 0; i < 5; i++) {
            stars[i].setClickable(true);
            int finalI = i;
            stars[i].setOnClickListener(v -> {
                 onStarTouch(d, finalI);
                 rating[0] = finalI + 1;
            });
        }


        EditText review = d.findViewById(R.id.etRatingComment);

        Button submit = d.findViewById(R.id.btnSubmitRating);
        AtomicBoolean submitted = new AtomicBoolean(false);
        submit.setOnClickListener(v -> {

            // if the user has not rated the player, show a toast and return
            if(rating[0] == 0) {
                if(forWho.equals("p")) {
                    Toast.makeText(c, "Please rate the player (1-5)", Toast.LENGTH_SHORT).show();
                } else if(forWho.equals("c")) {
                    Toast.makeText(c, "Please rate the court (1-5)", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(c, "Please rate the team (1-5)", Toast.LENGTH_SHORT).show();
                }
            }



            Rating r1 = new Rating();
            r1.setForWho(forWho);
            r1.setFrom(from);
            r1.setTo(to);
            r1.setRating(String.valueOf(rating[0]));
            r1.setComment(review.getText().toString());



            try {
                ratingFirebase.addRating(r1);
                Toast.makeText(c, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                RATING = rating[0] + "";
                submitted.set(true);
            } catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(c, "Failed submitting rating, try again later.", Toast.LENGTH_SHORT).show();
                return;
            }
            d.dismiss();
        });

       return rating[0] + "";
    }
    public static void reportDialog(Context c, String from, String to, String forWho) {
        Dialog d = new Dialog(c);
        d.setContentView(R.layout.dialog_report);
        d.show();
        EditText comment = d.findViewById(R.id.reportText);


        Spinner reportSpinner = d.findViewById(R.id.reportSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, Report.REPORT_TYPES);
        reportSpinner.setAdapter(adapter);

        if(to.equals("admin")) {
            comment.setHint("Report reason");
            adapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, new String[]{"ISSUE"});
            reportSpinner.setAdapter(adapter);
            reportSpinner.setEnabled(false);
        }

        Button submit = d.findViewById(R.id.btnSubmitReport);

        submit.setOnClickListener(v -> {
            if(comment.getText().toString().equals("")) {
                Toast.makeText(c, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Report report = new Report();
            report.setComment(comment.getText().toString());
            report.setFrom(from);
            report.setTo(to);
            report.setType(reportSpinner.getSelectedItem().toString());
            report.setForWho(forWho);

            reportFirebase.addReport(report);

            Toast.makeText(c, "Report submitted successfully", Toast.LENGTH_SHORT).show();
            d.dismiss();

        });
        


    }

}
