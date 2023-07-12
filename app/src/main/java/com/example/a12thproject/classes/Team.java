package com.example.a12thproject.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Team implements Serializable {


    private String name;
    private double rating;
    private double numRatings;
    private String captain;
    private String id;
    private String location;

    public Team() {}
    public Team(String[] players, String name, String captain) {
        this.captain = captain;
        this.name = name;
        this.id = "0";

    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setNumRatings(double numRatings) {
        this.numRatings = numRatings;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }


    // function that checks if a player is on the team




    // getters
    public String getLocation() {return location;}
    public double getRating() {
        return rating;
    }
    public double getNumRatings() {
        return numRatings;
    }
    public String getName() {
        return name;
    }

    public String getCaptain() {
        return captain;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }









}
