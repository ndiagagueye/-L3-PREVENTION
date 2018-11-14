package com.example.gueye.memoireprevention2018.activities;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.ChatViewPageAdaptater;
import com.example.gueye.memoireprevention2018.fragments.ChatFragment;
import com.example.gueye.memoireprevention2018.fragments.MembersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private TabLayout tabLayoutChat;
    private ViewPager vpChat;
    private Toolbar toolbarChat;

    private RecyclerView mChatRecyclerView;

    private ChatViewPageAdaptater chatViewPageAdaptater;
    private FirebaseDatabase mDatabase;
    private FirebaseFirestore firebaseFirestore;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbarChat = (Toolbar) findViewById(R.id.tb_chat_app);

        toolbarChat.setTitle("Chat");

        mDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        currentUserId = preferences.getString("user_id", null);


        tabLayoutChat = (TabLayout) findViewById(R.id.tl_chat_app);
        vpChat =(ViewPager)findViewById(R.id.vp_chat_app);

        chatViewPageAdaptater = new ChatViewPageAdaptater(getSupportFragmentManager());

        chatViewPageAdaptater.addFragment(new MembersFragment(), "Membres");
        chatViewPageAdaptater.addFragment(new ChatFragment(), "Discussions");


        vpChat.setAdapter(chatViewPageAdaptater);
        tabLayoutChat   .setupWithViewPager(vpChat);

    }

}
