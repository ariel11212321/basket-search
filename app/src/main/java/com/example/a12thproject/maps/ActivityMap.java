package com.example.a12thproject.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.a12thproject.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ActivityMap extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap map;
    public static String lng = "0";
    public static String lat = "0";


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        int googlePlayServicesAvailability = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);

        if (googlePlayServicesAvailability != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "cannot connect to google maps services", Toast.LENGTH_SHORT).show();
            finish();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);


        androidx.appcompat.widget.SearchView sv = findViewById(R.id.idSearchView);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = sv.getQuery().toString();
                if(location == null || location.equals("")){
                    return false;
                }
                Geocoder geocoder = new Geocoder(ActivityMap.this, Locale.getDefault());
                try {
                    List<android.location.Address> addresses = geocoder.getFromLocationName(location, 1);

                    if (addresses.size() > 0) {
                        for(int i = 0; i < addresses.size(); i++){

                            android.location.Address address = addresses.get(i);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            map.addMarker(new MarkerOptions().position(latLng).title("Marker in your location"));
                            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        }



                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;




        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng).title("Marker in your location"));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
        // let user zoom in and out
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                map.clear();
                map.addMarker(new MarkerOptions().position(map.getCameraPosition().target).title("Marker in your location"));
            }
        });
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);




        // save marker location
         lat = String.valueOf(map.getCameraPosition().target.latitude);
         lng = String.valueOf(map.getCameraPosition().target.longitude);




        // set the map on the users current location

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    lng = String.valueOf(marker.getPosition().longitude);
                    lat = String.valueOf(marker.getPosition().latitude);



                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("location", lat+","+lng);
                    setResult(RESULT_OK, resultIntent);
                    finish();




                    return false;
                }
            });




    }






}
