package com.example.a12thproject.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.a12thproject.classes.Team;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Player implements Serializable {


    private String username;
    private String password;
    private String fullname;
    private String phone;
    private String position;
    private String date;
    private double rating;
    private double numRatings;
    private String team;
    private String gender;
    private String id;
    private String height;
    private String location;





    public Player() {}

    public Player(String username, String password, String fullname, String phone, String position, String date) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.phone = phone;
        this.position = position;
        this.date = date;
        this.id = "0";

    }
    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // setters
    public void setLocation(String location) {
        this.location = location;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public void setGender(String gender) {this.gender=gender;}
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setNumRatings(double numRatings) {
        this.numRatings = numRatings;
    }
    public void setTeam(String team) {
        this.team = team;
    }
    public void setId(String id) {
        this.id = id;
    }
    // getters
    public String getLocation() {
        return location;
    }
    public String getHeight() {
        return height;
    }
    public String getGender() {return this.gender;}
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getFullname() {
        return fullname;
    }
    public String getPhone() {
        return phone;
    }
    public String getPosition() {
        return position;
    }
    public String getDate() {
        return date;
    }
    public double getRating() {
        return rating;
    }
    public double getNumRatings() {
        return numRatings;
    }
    public String getTeam() {
        return team;
    }
    public String getId() {
        return id;
    }






    public void update() {
        // check if one of the fields is empty, if it is, update to default value
        if (this.username == null) {
            this.username = "no username";
        }
        if (this.password == null) {
            this.password = "no password";
        }
        if (this.fullname == null) {
            this.fullname = "no fullname";
        }
        if (this.phone == null) {
            this.phone = "no phone";
        }
        if (this.position == null) {
            this.position = "no position";
        }
        if (this.date == null) {
            this.date = "no age";
        }
        if (this.team == null) {
            this.team = "no team";
        }
        if (this.id == null) {
            this.id = "no id";
        }
        if(this.height == null) {
            this.height = "no height";
        }
        if(gender == null) {
            gender = "no gender";
        }



    }



    public static Player mapToPlayer(Map<String, Object> player) {
        Player p = new Player();
        p.setDate(player.get("date").toString());
        p.setFullname(player.get("fullname").toString());
        p.setGender(player.get("gender").toString());
        p.setId(player.get("id").toString());
        p.setNumRatings((double)player.get("numRatings"));
        p.setPassword(player.get("password").toString());
        p.setPhone(player.get("phone").toString());
        p.setPosition(player.get("position").toString());
        p.setRating((double) player.get("rating"));
        p.setTeam(player.get("team").toString());
        p.setUsername(player.get("username").toString());
        return p;
    }
    public static Map<String, Object> playerToMap(Player p) {
        Map<String, Object> player = new HashMap<>();
        player.put("date", p.getDate());
        player.put("fullname", p.getFullname());
        player.put("gender", p.getGender());
        player.put("id", p.getId());
        player.put("numRatings", p.getNumRatings());
        player.put("password", p.getPassword());
        player.put("phone", p.getPhone());
        player.put("position", p.getPosition());
        player.put("rating", p.getRating());
        player.put("team", p.getTeam());
        player.put("username", p.getUsername());
        return player;
    }

    public Object toMap() {
        return (Object) playerToMap(this);
    }

    private boolean isCaptain(Team t) {
        return t.getCaptain().equals(this.username);
    }
    private boolean isCaptain(Map<String, Object> data) {
        return Objects.equals(data.get("captain"), this.username);
    }
    public String toString() {
        return "username: " + this.username + " fullname: " + this.fullname + " phone: " + this.phone + " position: " + this.position + " date: " + this.date + " rating: " + this.rating + " numRatings: " + this.numRatings + " team: " + this.team;
    }
}
