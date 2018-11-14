package com.example.gueye.memoireprevention2018.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.ModeleService.Common;
import com.example.gueye.memoireprevention2018.ModeleService.MyResponse;
import com.example.gueye.memoireprevention2018.ModeleService.Notification;
import com.example.gueye.memoireprevention2018.ModeleService.Sender;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.Remote.APIService;
import com.example.gueye.memoireprevention2018.adaptaters.MessageAdaptater;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussionsActivity extends AppCompatActivity {

    private Toolbar tbDiscussion;
    private CircleImageView civProfileImageDest;
    private TextView tvUsernameDest;
    private RecyclerView rvDiscussion;

    private EditText etMsg;
    private ImageView ivSendMsg;

    private List mChat   = new ArrayList<>(); ;

    private String userIdDest;
    private String usernameDest;
    private String profileImageDest;

    private ValueEventListener seenListener;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mFirebaseAuth;

    private MessageAdaptater messageAdaptater;

    private DatabaseReference reference;
    private String  currentUserId;
    private String profileImageCurrentUser = null;

    private FirebaseDatabase firebaseDatabase ;

    APIService mService;
    private String tokenId;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussions);

        tbDiscussion = (Toolbar)findViewById(R.id.tb_discussion);
        setSupportActionBar(tbDiscussion);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        /*if(mFirebaseAuth.getCurrentUser() != null){

            currentUserId  = mFirebaseAuth.getCurrentUser().getUid();
        }else {

            Toast.makeText(this, "current user null", Toast.LENGTH_SHORT).show();
        }*/

        mService = Common.getFCMClient();
        tokenId = getIntent().getStringExtra(Const.TOKEN_ID);
        Common.currentToken =  tokenId;
        //Common.currentToken = FirebaseInstanceId.getInstance().getToken();

        //Log.d( "Message Token", Common.currentToken );

        SharedPreferences preferences = getApplication().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        currentUserId = preferences.getString("user_id", null);


        usernameDest = getIntent().getStringExtra(Const.USERNAME_DEST);
        profileImageDest = getIntent().getStringExtra(Const.USER_PROFILE_DEST);
        userIdDest = getIntent().getStringExtra(Const.USER_ID_DEST);
        //profileImageCurrentUser = getIntent().getStringExtra(Const.CURRENT_USER_IMAGE_PROFILE);


        tbDiscussion.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        civProfileImageDest = (CircleImageView)findViewById(R.id.civ_profile_dest_discussion);
        tvUsernameDest = (TextView) findViewById(R.id.tv_username_dest_discussion);
        rvDiscussion = (RecyclerView)findViewById(R.id.rv_discussion);

        messageAdaptater = new MessageAdaptater(mChat, DiscussionsActivity.this,profileImageDest);
        rvDiscussion.setAdapter(messageAdaptater);

        
      setupRecyclevView();

        etMsg = (EditText)findViewById(R.id.et_msg_discussion);
        ivSendMsg = (ImageView)findViewById(R.id.iv_send_message);


      getUserDestInformation();


      ivSendMsg.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              String message = etMsg.getText().toString();

              if(!TextUtils.isEmpty(message)){

                 sendMessageUser(currentUserId, userIdDest,message);


              }else{

                  Toast.makeText(DiscussionsActivity.this, "You can't send empty message ", Toast.LENGTH_SHORT).show();
              }

              etMsg.setText("");

          }
      });



    }



    private void sendMessageUser(String currentUserId, final String userIdDest, final String message) {

        String idChat = currentUserId+"-"+userIdDest;

//        msgHashMap.put("sender",currentUserId);
//        msgHashMap.put("receiver",userIdDest);
//        msgHashMap.put("date", System.currentTimeMillis());
//        msgHashMap.put("isseen",false);

        final HashMap msgHashMap = new HashMap();

        msgHashMap.put("id",idChat);
        msgHashMap.put("message",message);
        msgHashMap.put("date", System.currentTimeMillis());
        msgHashMap.put("isseen",false);

        DatabaseReference databaseReference = firebaseDatabase.getReference("Chats");

      databaseReference.push().setValue(msgHashMap);

      sendNotificationPush(message,"Nouveau message");


        final DatabaseReference chatRef = firebaseDatabase.getReference("chatlists")
              .child(currentUserId)
              .child(userIdDest);

      chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {


              if (!dataSnapshot.exists()){

                  chatRef.child("id").setValue(userIdDest);
              }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

    }

    private void sendNotificationPush(String description,String typeAlerte) {

        Notification notification = new Notification(description,typeAlerte);

        Sender sender = new Sender( Common.currentToken,notification);
        mService.sendNotification(sender).enqueue( new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                if(response.body().success == 1)
                    Toast.makeText( DiscussionsActivity.this, "Success", Toast.LENGTH_SHORT ).show();
                else
                    Toast.makeText( DiscussionsActivity.this, "Failed", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

                Log.e( "Error ", t.getMessage());

            }
        } );

    }


    private void setupRecyclevView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvDiscussion.setLayoutManager(linearLayoutManager);

    }


    //check if user has seen message


    private void chechIfUserHasSeenChat(final String userIdDest){

         reference = FirebaseDatabase.getInstance().getReference("Chats");

      seenListener =  reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot ==null) return;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chat chat = snapshot.getValue(Chat.class);

                    String idChat = userIdDest+"-"+currentUserId;

                    if(chat.getId().equals(idChat)){

                        HashMap<String , Object> hashMap = new HashMap<>();

                        hashMap.put("isseen",true);

                        snapshot.getRef().updateChildren(hashMap);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void readMessages(final String currentUserId , final String userIdDest , final String profileImageDest){



      com.google.firebase.database.Query databaseReference = firebaseDatabase.getReference("Chats").orderByChild("date");

        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chat chatMessage = snapshot.getValue(Chat.class);

                    String messageSends = currentUserId+"-"+userIdDest;
                    String messageReceives=userIdDest+"-"+currentUserId ;

                    if(chatMessage.getId().equals(messageSends) || chatMessage.getId().equals(messageReceives)){

                        mChat.add(chatMessage);

                        messageAdaptater.notifyDataSetChanged();
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        chechIfUserHasSeenChat(userIdDest);

    }



    private void getUserDestInformation() {


        tvUsernameDest.setText(usernameDest);
        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile);

        Glide.with(DiscussionsActivity.this).applyDefaultRequestOptions(placeholderOption).load(profileImageDest).into(civProfileImageDest);
      readMessages (currentUserId,userIdDest,profileImageDest);

    }



    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
    }
}
