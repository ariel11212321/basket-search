package com.example.a12thproject.adapters;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;

import com.example.a12thproject.R;
import com.example.a12thproject.classes.Court;
import com.example.a12thproject.classes.MapHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CourtAdapter extends ArrayAdapter<Court> {
    Context context;
    List<Court> objects;



    public CourtAdapter(Context context, int resource, List<Court> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.onerowcourt, parent, false);
        } else {
            view = convertView;
        }
        TextView tvName =  view.findViewById(R.id.court_name_one_row);
        TextView distance = view.findViewById(R.id.tvDistance);


        Court temp = objects.get(position);
        tvName.setText(temp.getName());

        android.location.LocationManager locationManager = (android.location.LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "please allow location permissions", Toast.LENGTH_SHORT).show();
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location dest = MapHelper.createLocationFromString(temp.getLocation());

        distance.setText(MapHelper.calculateDistance(dest, lastKnownLocation) + " kilometers away from you");







        return view;
    }

}