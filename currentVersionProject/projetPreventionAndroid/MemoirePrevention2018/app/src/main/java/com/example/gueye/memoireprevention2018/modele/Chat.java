package com.example.gueye.memoireprevention2018.modele;

import java.util.Date;

public class Chat {

    private  String id;
    private String message;
    private long  date;
    private boolean isseen;

    public Chat(String id, String message, long date, boolean isseen) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.isseen = isseen;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public Chat(String id, String message) {
        this.id = id;
        this.message = message;

    }

    public Chat(String id, String message, long date) {

        this(id,message);
        this.date = date;
    }

    public Chat() {
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
