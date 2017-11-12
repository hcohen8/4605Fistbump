package com.microsoft.band.sdk.sampleapp.accelerometer;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by test on 11/8/17.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String phone;
    public ArrayList<String> contacts = new ArrayList<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String phone, String email) {
        this.name = username;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
