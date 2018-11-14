package com.example.gueye.memoireprevention2018.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.ChatMessageActivity;
import com.example.gueye.memoireprevention2018.activities.DiscussionsActivity;
import com.example.gueye.memoireprevention2018.activities.ListMembersActivity;
import com.example.gueye.memoireprevention2018.adaptaters.ChatMessageAdaptater;
import com.example.gueye.memoireprevention2018.adaptaters.ListMembersAdaptater;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.modele.ChatList;
import com.example.gueye.memoireprevention2018.modele.ChatMessage;
import com.example.gueye.memoireprevention2018.modele.UserId;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    DatabaseReference reference;
    private RecyclerView rvChatMessage;
    private String currentUserId;
    private List<Chat> chatList;

    private TextView tvNoDiscussionCheck ;

    private List<String> userIdList;

    private List< List<Chat> > listDiscussions;
    private ChatMessageAdaptater mChatAdaptater ;

    private FloatingActionButton flNewChatMessage;

    public static final String TAG  = "ChatMessageActivity";

    private boolean isAdded =false;
    private List<Users> mUsers ;

    public ChatFragment() {
        // Required empty public constructor
    }
    ChatMessageAdaptater chatMessageAdaptater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        currentUserId = preferences.getString("user_id", null);
        rvChatMessage = (RecyclerView)view.findViewById(R.id.rv_chat_app);
        flNewChatMessage = (FloatingActionButton) view.findViewById(R.id.fl_new_chat_message);
        tvNoDiscussionCheck = (TextView) view.findViewById(R.id.tv_no_dicussions_get);

        userIdList = new ArrayList<>();
        mUsers = new ArrayList();
        chatList =new ArrayList<>();



        mChatAdaptater = new ChatMessageAdaptater(chatList,mUsers,getActivity().getApplicationContext(),currentUserId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvChatMessage.setLayoutManager(linearLayoutManager);
        rvChatMessage.setAdapter(mChatAdaptater);

        showDiscussions();


        flNewChatMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToMembersActivity();
            }
        });



        return view;
    }

    private void sendUserToMembersActivity() {

        Intent membersIntent = new Intent(getActivity(), ListMembersActivity.class);

        membersIntent.putExtra( Const.IS_CHAT,true );

        getActivity().getApplicationContext().startActivity(membersIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


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
                    Toast.makeText(getContext(), "Vous n'avez pas encore de discussion ", Toast.LENGTH_SHORT).show();
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


                }


            }
        });



    }

}
