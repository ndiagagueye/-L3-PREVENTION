package com.example.gueye.memoireprevention2018.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.BenevolAdapter;
import com.example.gueye.memoireprevention2018.modele.Benevol;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListBenevolActivity extends AppCompatActivity {

    private EditText mSearchField;
    private CircleImageView mBtnSearch;
    private RecyclerView mListBenevol;
    private android.support.v7.widget.Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mUserDatabase;
    private List<Users> usersList;
    private List<Benevol> benevolList;
    private BenevolAdapter mBenevolAdapter;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_list_benevol );

        init();
    }



    @Override
    protected void onStart() {
        super.onStart();

        usersList.clear();
        benevolList.clear();

        mFirestore.collection( "Benevol").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots == null) {
                    return;
                }
                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String userId = doc.getDocument().getId();
                            final Benevol benevol = doc.getDocument().toObject( Benevol.class ).withId( userId );

                            //String userId = doc.getDocument().getString( "user_id" );

                            mFirestore.collection( "Users" ).document( userId ).get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Users user = task.getResult().toObject( Users.class );
                                        if (user.getUser_id() != null){
                                            if (!user.getUser_id().equals(mUserId)){
                                                usersList.add( user );
                                                benevolList.add( benevol );
                                                mBenevolAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            } );
                        }
                    }

                } else {
                    return;
                }
            }
        } );
    }


    private void init(){

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        mUserId = preferences.getString("user_id", null);

        mSearchField = findViewById( R.id.search_field);
        mBtnSearch = findViewById( R.id.btn_search);
        mListBenevol = findViewById( R.id.result_recycle_view);
        mListBenevol.setHasFixedSize(true);
        mListBenevol.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById( R.id.benevol_toolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle("Bénévoles" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        mFirestore = FirebaseFirestore.getInstance();
        mListBenevol = findViewById( R.id.result_recycle_view);

        usersList = new ArrayList<>();
        benevolList = new ArrayList<>();

        mBenevolAdapter = new BenevolAdapter(this, usersList, benevolList);
        mListBenevol.setHasFixedSize(true);
        mListBenevol.setLayoutManager(new LinearLayoutManager(this));
        mListBenevol.setAdapter(mBenevolAdapter);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();
                //Toast.makeText( ListBenevolActivity.this, ""+searchText, Toast.LENGTH_SHORT ).show();
                //search(searchText);
            }
        });

    }


}
