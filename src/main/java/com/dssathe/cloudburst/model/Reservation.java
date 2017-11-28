package com.dssathe.cloudburst.model;

public class Reservation {
    private String ami;
    private String hours;
    private String image;
    private String publicIP;
    private String username;
    private String password;

    public String getAmi() {
        return ami;
    }

    public String getImage() {
        return image;
    }

    public String getHours() {
        return hours;
    }

    public String getPublicIP() {
        return publicIP;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setAmi(String ami) {
        this.ami = ami;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void save() {
        // Insert into DB
    }
}
