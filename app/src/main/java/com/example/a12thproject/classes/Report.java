package com.example.a12thproject.classes;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Map;

public class Report implements Serializable {

    public static final String[] REPORT_TYPES = {"Inappropriate Content", "issue", "Spam", "Other"};


    private String forWho;
    // p for player, t for team, c for court

    private String type;
    private String comment;
    private String from;
    private String to;



    private String id;

    public Report(String type, String comment, String from, String to, String forWho) {
        this.type = type;
        this.comment = comment;
        this.from = from;
        this.to = to;
        this.id = "0";
        this.forWho = forWho;
    }
    public Report() {}


    public void setForWho(String forWho) {
        this.forWho = forWho;
    }


    // set
    public void setId(String id) {
        this.id = id;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(String to) {
        this.to = to;
    }
    // get
    public String getType() {
        return type;
    }
    public String getComment() {
        return comment;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getId() {
        return id;
    }
    public String getForWho() {
        return forWho;
    }

    // toString
    @Override
    public String toString() {
        return "Report{" +
                "type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

    public static Report dsToReport(DocumentSnapshot ds) {
        try {
            Report r = new Report();
            r.setId(ds.getString("id"));
            r.setType(ds.getString("type"));
            r.setComment(ds.getString("comment"));
            r.setFrom(ds.getString("from"));
            r.setTo(ds.getString("to"));
            r.setForWho(ds.getString("forWho"));
            return r;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Report mapToReport(Map<String, Object> map) {
        Report r = new Report();
        r.setId((String) map.get("id"));
        r.setType((String) map.get("type"));
        r.setComment((String) map.get("comment"));
        r.setFrom((String) map.get("from"));
        r.setTo((String) map.get("to"));
        r.setForWho((String) map.get("forWho"));
        return r;
    }



}
