package com.example.a12thproject.classes;

import java.io.Serializable;

public class Game implements Serializable {
    private String[] players;
    private static int MAX_CAPACITY = 10;
    private int capacity;
    private String id;
    private String creator;
    private String password;



    public Game() {
        players = new String[MAX_CAPACITY];
        capacity = 0;
        id = "";
        creator = "";
        password = "";

    }


    public void addPlayer(String username, int team) {
        if(team < 1 || team > 2 || capacity >= MAX_CAPACITY) {
            return;
        }
        if(team == 1) {
            for(int i = 0; i < 5; i++) {
                if(players[i] == null) {
                    players[i] = username;
                    capacity++;
                    return;
                }
            }
        }
        else {
            for(int i = 5; i < 10; i++) {
                if(players[i] == null) {
                    players[i] = username;
                    capacity++;
                    return;
                }
            }
        }
    }
    public void removePlayer(String username) {
        for(int i = 0; i < MAX_CAPACITY; i++) {
            if(players[i] != null && players[i].equals(username)) {
                players[i] = null;
                capacity--;
                return;
            }
        }
    }
    public boolean isFull() {
        return capacity == MAX_CAPACITY;
    }
    public boolean isEmpty() {
        return capacity == 0;
    }
    public boolean contains(String username) {
        for(int i = 0; i < MAX_CAPACITY; i++) {
            if(players[i] != null && players[i].equals(username)) {
                return true;
            }
        }
        return false;
    }
    public String[] getPlayers() {
        return players;
    }
    public int getCapacity() {
        return capacity;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }



}
