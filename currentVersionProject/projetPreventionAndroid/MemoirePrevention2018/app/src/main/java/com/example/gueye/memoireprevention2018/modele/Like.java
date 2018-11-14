package com.example.gueye.memoireprevention2018.modele;

/**
 * Created by gueye on 08/10/18.
 */

public class Like extends LikeId {
    String timestamp, user_id, username, image;

    public Like() {
    }

    public Like(String timestamp, String user_id, String username, String image) {
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.username = username;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
