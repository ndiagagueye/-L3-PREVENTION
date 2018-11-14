package com.example.gueye.memoireprevention2018.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.modele.Position;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.gueye.memoireprevention2018.utils.Const.USER_PROFESSION;
import static com.example.gueye.memoireprevention2018.utils.Const.USER_TEL;

public class OtherProfleActivity extends AppCompatActivity {

    private TextView usename, telephone, profession;
    private String tokenId = null;
    private RelativeLayout getLocation;
    private String mImage = null;
    private String mUsername = null;
    private String mUserId = null;
    private String mTel = null;

    private FirebaseFirestore mFirestore;
    private Button sendSMS;

    private double longitude ;
    private double latitude ;

    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_other_profle );

        init();
    }

    private void init() {

        //SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        //userId = preferences.getString("user_id", null);
        mFirestore = FirebaseFirestore.getInstance();

        usename = findViewById( R.id.other_profile_username );
        telephone = findViewById( R.id.other_profile_telephone );
        profession = findViewById( R.id.other_profession );
        getLocation = findViewById(R.id.other_location);
        profileImage = findViewById(R.id.other_profile_image);
        sendSMS = findViewById( R.id.send_sms );

        tokenId = getIntent().getStringExtra(Const.TOKEN_ID);
        mImage = getIntent().getStringExtra(Const.USER_PROFILE_DEST);
        mUserId = getIntent().getStringExtra(Const.USER_ID_DEST);
        mUsername = getIntent().getStringExtra( Const.USERNAME_DEST);
        mTel = getIntent().getStringExtra(Const.USER_TEL);

        if (mUserId != null){
            mFirestore.collection("Benevol").document(mUserId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){

                        if(documentSnapshot.getLong("profession") != null){

                            long position = documentSnapshot.getLong("profession");
                            profession.setText( Const.DEFAULT_TYPES_USER[(int) position] );

                        }

                    }
                }
            } );
        }


        if (tokenId != null && mImage != null && mUsername != null && mUserId != null && mTel != null){
            usename.setText(mUsername);
            telephone.setText(mTel);
            RequestOptions plasholderOption = new RequestOptions();
            plasholderOption.placeholder( R.drawable.back );

            Glide.with(OtherProfleActivity.this ).setDefaultRequestOptions( plasholderOption ).load(mImage).into( profileImage );

        }

        getLocationUser();

        sendSMS.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d( "Other", "Username "+mUsername );
                Log.d( "Other", "TokenId "+tokenId );
                Log.d( "Other", "Image "+mImage );
                Log.d( "Other", "Username "+mUserId );
                Log.d( "Other", "Telephone "+mTel );

                Intent smsIntent = new Intent( OtherProfleActivity.this, DiscussionsActivity.class);
                if (tokenId != null && mImage != null && mUsername != null && mUserId != null){
                    smsIntent.putExtra(Const.USER_ID_DEST,mUserId);
                    smsIntent.putExtra(Const.USER_PROFILE_DEST,mImage);
                    smsIntent.putExtra(Const.USERNAME_DEST,mUsername);
                    smsIntent.putExtra(Const.TOKEN_ID, tokenId);
                    startActivity(smsIntent);
                }else{
                    Toast.makeText( OtherProfleActivity.this, "Impossible de le joindre!", Toast.LENGTH_SHORT ).show();
                }

            }
        } );

    }

    public  void getLocationUser(){

        getLocation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUserId != null){

                    mFirestore.collection("Users").document(mUserId).collection("Positions").document(mUserId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){

                                String address = documentSnapshot.getString("address");
                                latitude = documentSnapshot.getDouble("latitude");
                                longitude = documentSnapshot.getDouble("longitude");

                                String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + address + ")";
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intent);
                                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                                Toast.makeText( OtherProfleActivity.this, ""+address, Toast.LENGTH_SHORT ).show();
                            }else {
                                Toast.makeText( OtherProfleActivity.this, "document not found", Toast.LENGTH_SHORT ).show();
                            }

                        }
                    } );
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}