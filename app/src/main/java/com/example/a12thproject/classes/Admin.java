package com.example.a12thproject.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Admin implements Serializable {

    private String name;
    private String password;
    private String email;
    private long id;


    public Admin(String name, String password, String email) {
        this(name, password);
        this.email = email;
    }
    public Admin(String name, String password) {
        this.name = name;
        this.password = password;
        this.id = 0;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    // setters
    public void setName(String name) {
        this.name = name;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    // getters
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }
    // toString
    @NonNull
    public String toString() {
        return "Admin: " + name + "\n" + " Password: " + password;
    }
}
