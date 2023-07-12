package com.example.a12thproject.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Court implements Serializable {
    private String name;

    private String phone;
    private String website;
    private double rating;
    private int numRatings;
    private String description;
    private String author;

    private String available;

    private String id;





    public void setAvailable(String available) {
        this.available = available;
    }
    public String getAvailable() {
        return available;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;


    public Court() {}

    // setters
    public void setDescription(String description) {
        this.description = description;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }
    public void setId(String id) {
        this.id = id;
    }
    // getters

    public String getDescription() {
        return description;
    }
    public String getAuthor() {
        return author;
    }
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
    public String getWebsite() {
        return website;
    }
    public double getRating() {
        return rating;
    }
    public int getNumRatings() {
        return numRatings;
    }
    public String getId() {
        return id;
    }






}
