package com.example.a12thproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.a12thproject.R;
import com.example.a12thproject.classes.Rating;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RatingAdapter extends ArrayAdapter<Rating> {

    List<Rating> objects;
    Context context;


    public RatingAdapter(@NonNull Context context, int resource, List<Rating> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;

    }
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.onerowrating, parent, false);
        } else {
            view = convertView;
        }
        TextView tvName = (TextView) view.findViewById(R.id.tvRating);
        TextView tvPass = (TextView) view.findViewById(R.id.tvComment);
        TextView tvFrom = (TextView) view.findViewById(R.id.tvFrom);
        ImageView iv = (ImageView) view.findViewById(R.id.ivPlayerPic);

        tvName.setText(objects.get(position).getRating());
        tvPass.setText(objects.get(position).getComment());
        tvFrom.setText(objects.get(position).getFrom());

        iv.setVisibility(View.VISIBLE);
        final long ONE_MEGABYTE = 1024 * 1024;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("images/" + objects.get(position).getFrom());
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            iv.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
            // doesn't exist / no profile pic / no internet connection / etc
        });




        return view;
    }

}

