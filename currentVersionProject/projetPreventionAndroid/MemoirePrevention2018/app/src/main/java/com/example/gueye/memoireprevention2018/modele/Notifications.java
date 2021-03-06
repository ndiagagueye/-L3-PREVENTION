package com.example.gueye.memoireprevention2018.modele;

/**
 * Created by gueye on 18/09/18.
 */

public class Notifications extends NotificationId{

    String from, description, image, date;
    int type ;
    double longitude, latitude;


    public Notifications() {

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

    public String getFromNotif() {
        return from;
    }

    public void setFromNotif(String from) {
        this.from = from;
    }

    public int getTypeNotif() {
        return type;
    }

    public void getTypeNotif(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return date;
    }

    public void setTime(String date) {
        this.date = date;
    }

    public Notifications(String from, int type, String image, String description, String date) {
        this.from = from;
        this.type = type;
        this.description = description ;
        this.date = date ;
        this.image = image  ;
    }
}