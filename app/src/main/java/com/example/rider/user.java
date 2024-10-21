package com.example.rider;

public class user {
    public String fullName;
    public String username;
    public String email;
    public String contactNo;
    public String password;

    public user() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public user(String fullName, String username, String email, String contactNo, String password) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.contactNo = contactNo;
        this.password = password;
    }
}
