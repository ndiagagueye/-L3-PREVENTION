package com.example.gueye.memoireprevention2018.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileBenevolActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String mToken_id, mImage ;
    private TextView mProfilename;
    private TextView mProfileTel;
    private TextView mTypeBenevol;
    private CircleImageView mProfileImage;
    private Button mContact;
    private static final int REQUEST_CALL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile_benevol );
        init();
        contacter();
    }

    private void init() {

        mProfileImage = findViewById( R.id.benevol_image);
        mProfilename = findViewById( R.id.benevol_name);
        mProfileTel = findViewById( R.id.benevol_telephone);
        mTypeBenevol = findViewById( R.id.type_benevol);
        mContact = findViewById( R.id.benevol_contacter );

        mToolbar = findViewById( R.id.profile_benevol_toolbar);
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Profile" );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProfilename.setText(getIntent().getStringExtra("username"));
        mProfileTel.setText( getIntent().getStringExtra("telephone") );
        mTypeBenevol.setText(getIntent().getStringExtra("profession"));

        Toast.makeText( this, getIntent().getStringExtra("type"), Toast.LENGTH_SHORT ).show();

        mImage = getIntent().getStringExtra("image");
        mToken_id = getIntent().getStringExtra("token_id");
        RequestOptions plasholderOption = new RequestOptions();
        plasholderOption.placeholder( R.drawable.back );

        Glide.with(ProfileBenevolActivity.this).setDefaultRequestOptions( plasholderOption ).load(mImage).into( mProfileImage );


    }

    private void contacter(){

        mContact.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(ProfileBenevolActivity.this)
                        //.setTitle("Deconnection")
                        .setMessage("Contacter")
                        .setPositiveButton("Appeler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if (ContextCompat.checkSelfPermission(ProfileBenevolActivity.this,
                                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ProfileBenevolActivity.this,
                                            new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                                } else {
                                    String dial = "tel:" + getIntent().getStringExtra("telephone").trim();
                                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                                    finish();
                                }

                            }
                        })

                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .show();
                Toast.makeText( ProfileBenevolActivity.this, "Cliked", Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
