package com.cbsjchen0601.models;

import java.io.Serializable;

public class Yaks implements Serializable {

    private static final String TAG = "Yaks";

    private String ydescription;
    private String yimage;
    private String date;
    private int votes;
    private String location;
    private String yID;

    public Yaks() {}

    public Yaks(String ydescription, String yimage, int votes, String date, String key) {
        this.ydescription = ydescription;
        this.yimage = yimage;
        this.votes = votes;
        this.date = date;
        this.yID = key;
    }

    public String getYdescription() {
        return ydescription;
    }

    public void setYdescription(String ydescription) {
        this.ydescription = ydescription;
    }

    public String getYimage() {
        return yimage;
    }

    public void setYimage(String yimage) {
        this.yimage = yimage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getyID() {
        return yID;
    }

    public void setyID(String yID) {
        this.yID = yID;
    }

}