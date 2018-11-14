package com.example.gueye.memoireprevention2018.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.ChatAdaptater;
import com.example.gueye.memoireprevention2018.adaptaters.ChatMessageAdaptater;
import com.example.gueye.memoireprevention2018.adaptaters.ListMembersAdaptater;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.modele.ChatMessage;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class ChatMessageActivity extends AppCompatActivity {

    private RecyclerView rvChatMessage;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private List<Chat> chatList;

    private TextView tvNoDiscussionCheck ;

    private List<String> userIdList;

    private List< List<Chat> > listDiscussions;
    private ChatMessageAdaptater mChatAdaptater ;

    private FloatingActionButton flNewChatMessage;

    public static final String TAG  = "ChatMessageActivity";

    private boolean isAdded =false;
    private List mUsers ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        rvChatMessage = (RecyclerView) findViewById(R.id.rv_chat_app);
        flNewChatMessage = (FloatingActionButton) findViewById(R.id.fl_new_chat_message);
        tvNoDiscussionCheck = (TextView) findViewById(R.id.tv_no_dicussions_get);

        userIdList = new ArrayList<>();
        mUsers = new ArrayList();
        chatList =new ArrayList<>();
        listDiscussions = new ArrayList<>();

        mAuth =FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getApplication().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        currentUserId = preferences.getString("user_id", null);

        mChatAdaptater = new ChatMessageAdaptater(chatList,mUsers,this,currentUserId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvChatMessage.setLayoutManager(linearLayoutManager);
        rvChatMessage.setAdapter(mChatAdaptater);

        showDiscussions();


        flNewChatMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToMembersActivity();
            }
        });

    }

    private void sendUserToMembersActivity() {

        Intent membersIntent = new Intent(ChatMessageActivity.this, ListMembersActivity.class);

        startActivity(membersIntent);
    }

    private void showDiscussions(){

        Query mDatabaseReference = mDatabase.getReference("Chats").orderByChild("date");

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot == null) return;

                chatList.clear();
                userIdList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Chat chatMessage = snapshot.getValue(Chat.class);


                    String idSender = chatMessage.getId().split("-")[0];
                    
                    String idReceiver = chatMessage.getId().split("-")[1];
                    
                    if(idReceiver.equals(currentUserId) ){
                        
                        chatList.add(chatMessage);

                        Log.d(TAG, "onDataChange: chatMESSAGE DATE is "+chatMessage.getDate());


                       if (!userIdList.contains(idSender)) userIdList.add(idSender);


                        Log.d(TAG, "onDataChange: chat message ajouté ");
                    }


                    if (idSender.equals(currentUserId)){

                        Log.d(TAG, "onDataChange: chatMESSAGE DATE is "+chatMessage.getDate());

                        chatList.add(chatMessage);

                        if(!userIdList.contains(idReceiver)) userIdList.add(idReceiver);
                    }


                }

                
                if (chatList.size() != 0){

                    Log.d(TAG, "onDataChange: la tailles de chatlist est "+ chatList.size());

                    Log.d(TAG, "onDataChange: nombre de discussion est "+ userIdList.size());
                    getUsersList(userIdList);

                }else {

                    tvNoDiscussionCheck.setVisibility(View.VISIBLE);
                    Toast.makeText(ChatMessageActivity.this, "Vous n'avez pas encore de discussion ", Toast.LENGTH_SHORT).show();
                }

                

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void getUsersList(final List <String> userIdList ){



        mFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots == null) return;

                mUsers.clear();

                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        Toast.makeText(ChatMessageActivity.this, "User added ", Toast.LENGTH_SHORT).show();

                        String userId = documentChange.getDocument().getId();

                        Log.d(TAG, "onEvent: userId "+ userId);

                        Users users = documentChange.getDocument().toObject(Users.class).withId(userId);


                        for(String idUser : userIdList){


                            if(userId.equals(idUser)){


                                mUsers.add(users);
                            }
                        }

                        mChatAdaptater.notifyDataSetChanged();
                    }

                    Toast.makeText(ChatMessageActivity.this, "Nombre user "+ mUsers.size(), Toast.LENGTH_SHORT).show();

                }


            }
        });



    }


    private void showRecentsDiscussion()
    {


    }


}
