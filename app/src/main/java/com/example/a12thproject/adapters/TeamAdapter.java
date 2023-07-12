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

import com.example.a12thproject.R;
import com.example.a12thproject.classes.Team;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TeamAdapter extends ArrayAdapter<Team> {
    Context context;
    List<Team> objects;



    public TeamAdapter(Context context, int resource, List<Team> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.onerowteam, parent, false);
        } else {
            view = convertView;
        }
        TextView tvName = (TextView) view.findViewById(R.id.tvTeamName);
        TextView tvCaptain = (TextView) view.findViewById(R.id.tvTeamCaptain);
        ImageView iv = (ImageView) view.findViewById(R.id.tvTeamImage);
        Team temp = objects.get(position);
        tvName.setText(temp.getName());
        tvCaptain.setText(temp.getCaptain());

        for(int i = 0; i < temp.getRating(); i++) {
            ImageView ivStar = view.findViewById(R.id.onerowteam_star1 + i);
            ivStar.setVisibility(View.VISIBLE);
            ivStar.setImageResource(R.drawable.yellow_star);
        }


        iv.setVisibility(View.VISIBLE);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("imageTeams/"+objects.get(position).getName());
        final long ONE_MEGABYTE = 1024 * 1024;
        profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            iv.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
            // Handle any errors

        });





        return view;
    }
}