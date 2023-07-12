package com.example.a12thproject.classes;

import java.io.Serializable;

public class Rating implements Serializable {
    private String from;
    private String to;
    private String rating;
    private String comment;
    private String forWho;
    private String id;

    public Rating(String from, String to, String forWho, String rating, String comment) {
        this.from = from;
        this.to = to;
        this.rating = rating;
        this.comment = comment;
        this.forWho = forWho;
        this.id = "0";
    }
    public Rating() {}

    // setters
    public void setForWho(String forWho) {
        this.forWho = forWho;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    // getters
    public String getForWho() {
        return forWho;
    }
    public String getId() {return id; }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getRating() {
        return rating;
    }
    public String getComment() {
        return comment;
    }

}
