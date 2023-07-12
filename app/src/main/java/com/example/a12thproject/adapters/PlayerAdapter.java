package com.example.a12thproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.a12thproject.R;
import com.example.a12thproject.classes.Player;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

public class PlayerAdapter extends ArrayAdapter<Player>  {
    Context context;
    List<Player> objects;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public PlayerAdapter(Context context, int resource, List<Player> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.onerowplayer, parent, false);
        } else {
            view = convertView;
        }
        TextView tvName = (TextView) view.findViewById(R.id.username2);
        TextView tvPass = (TextView) view.findViewById(R.id.skill);
        ImageView iv = (ImageView) view.findViewById(R.id.image);
        tvName.setText(objects.get(position).getFullname());
        tvPass.setText(objects.get(position).getUsername());


        View finalView = view;
        db.collection("players").document(objects.get(position).getUsername()).collection("ratings").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                double sum = 0;
                int count = 0;
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    if(document.exists()) {
                        sum += Double.parseDouble(document.getString("rating"));
                        count++;
                    }
                }
                if(count > 0) {
                    sum /= count;
                    for(int i = 0; i < sum; i++) {
                        ImageView star = finalView.findViewById(R.id.rstar1 + i);
                        star.setImageResource(R.drawable.yellow_star);
                        objects.get(position).setRating(sum);
                        objects.get(position).setNumRatings(count);
                    }
                } else {
                    for(int i = 0; i < 5; i++) {
                        ImageView star = finalView.findViewById(R.id.rstar1 + i);
                        star.setImageResource(R.drawable.white_star);
                    }
                }
            }
        });



        iv.setVisibility(View.VISIBLE);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("images/"+objects.get(position).getUsername());
        final long ONE_MEGABYTE = 1024 * 1024;
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context)
                    .load(uri)
                    .into(iv);
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            iv.setImageResource(R.drawable.profile);
        });



        return view;
    }


}