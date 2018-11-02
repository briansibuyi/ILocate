package com.example.codetribe.ilocate;

/**
 * Created by CodeTribe on 2017/10/02.
 */

public class userInformation {

    String username, email, uiid;
    Double userLat, userLong;

    userInformation() {

    }

    public userInformation(String username, String email, Double userLat, Double userLong) {
        this.username = username;
        this.email = email;
        this.userLat = userLat;
        this.userLong = userLong;
    }

    public userInformation(String email, Double userLat, Double userLong) {
        this.email = email;
        this.userLat = userLat;
        this.userLong = userLong;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLong() {
        return userLong;
    }

    public void setUserLong(Double userLong) {
        this.userLong = userLong;
    }

    public String getUiid() {
        return uiid;
    }

    public void setUiid(String uiid) {
        this.uiid = uiid;
    }
}