package com.example.gueye.memoireprevention2018.modele;

/**
 * Created by gueye on 05/10/18.
 */

public class Position {

    private double longitude, latitude;
    private String userId;

    public  Position(){}

    public Position(double longitude, double latitude, String userId) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.userId = userId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
