package com.example.gueye.memoireprevention2018.modele;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class LikeId {

    @Exclude
    public String likeId;

    public <T extends LikeId> T withId(@NonNull final String id) {
        this.likeId = id;
        return (T) this;
    }
}
