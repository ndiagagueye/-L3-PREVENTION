package com.example.gueye.memoireprevention2018.modele;
import java.util.Date;

/**
 * Created by gueye on 14/08/18.
 */

public class Comments extends CommentsId{

    private String message, user_id;
    private String timestamp;
    public Comments(){}

    public Comments(String message, String user_id, String timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
