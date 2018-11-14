package com.example.gueye.memoireprevention2018.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.ListMembersAdaptater;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class ListMembersActivity extends AppCompatActivity {


    //private FirebaseUser currentUser;
    private String currentUser;
    private FirebaseFirestore firestore;
    private RecyclerView mRecyclerView;

    private boolean isChat = false;

    private ListMembersAdaptater listMembersAdaptater;
    private ArrayList userList = new ArrayList();

    private List<Users> mUsersList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_members);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_members_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        isChat = getIntent().getBooleanExtra( Const.IS_CHAT,false );

        listMembersAdaptater = new ListMembersAdaptater( mUsersList,this,isChat );
        mRecyclerView.setAdapter(listMembersAdaptater);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        currentUser = preferences.getString("user_id", null);

        Toast.makeText( this, "UserId "+currentUser, Toast.LENGTH_SHORT ).show();
        Log.d( TAG, "onCreate: userId "+currentUser );

        getUsersList();

    }

    private void getUsersList() {

        Toast.makeText(ListMembersActivity.this, "getting user liste", Toast.LENGTH_SHORT).show();

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots == null) return;

                userList.clear();

                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        Toast.makeText(ListMembersActivity.this, "User added ", Toast.LENGTH_SHORT).show();

                        String userId = documentChange.getDocument().getId();

                        Log.d(TAG, "onEvent: userId "+ userId);

                        Users users = documentChange.getDocument().toObject(Users.class).withId(userId);

                        if (!currentUser.equals( users.getUser_id() )){

                            Toast.makeText(ListMembersActivity.this , "", Toast.LENGTH_SHORT).show();

                            mUsersList.add(users);

                            listMembersAdaptater.notifyDataSetChanged();
                        }


                    }

                    Toast.makeText(ListMembersActivity.this, "Nombre user "+ userList.size(), Toast.LENGTH_SHORT).show();

                }

            }
        });


    }
}
