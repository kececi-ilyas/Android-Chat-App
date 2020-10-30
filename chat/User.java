package com.example.chat;

import android.util.Log;

public class User {

    public String id;
    public String username;
    public String email;
    public String name;

    public User(){

    }

    public void yazdir(){
        Log.i("Userr", id);
        //Log.i("Userr", username);
        Log.i("Userr", email);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
