package com.example.gueye.memoireprevention2018.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.gueye.memoireprevention2018.ModeleService.Common;
import com.example.gueye.memoireprevention2018.ModeleService.MyResponse;
import com.example.gueye.memoireprevention2018.ModeleService.Notification;
import com.example.gueye.memoireprevention2018.ModeleService.Sender;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.Remote.APIService;
import com.example.gueye.memoireprevention2018.services.GoogleMapsServices;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListSecoursActivity extends AppCompatActivity {

    private LinearLayout policeBtn, sapeurBtn, ambulanceBtn;
    private static final int REQUEST_CALL = 1;
    private android.support.v7.widget.Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_list_secours );

        init();
    }



    private void init() {

        policeBtn = findViewById( R.id.police_btn );
        sapeurBtn = findViewById( R.id.sapeur_btn );
        ambulanceBtn = findViewById( R.id.ambulence_btn );
        mToolbar = findViewById( R.id.list_secours_toolbar );

        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Urgences" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        policeBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBtnSecour( "17" );
            }
        } );

        ambulanceBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBtnSecour( "15" );
            }
        } );

        sapeurBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBtnSecour( "18" );
            }
        } );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void actionBtnSecour(final String clee) {

        new AlertDialog.Builder( this )
                //.setTitle("Deconnection")
                .setMessage( "Voulez-vous vraiment vous joindre l'agence?" ).setPositiveButton( "Appeler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (ContextCompat.checkSelfPermission( ListSecoursActivity.this, android.Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( ListSecoursActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL );
                } else {
                    String dial = "tel:" + clee;
                    startActivity( new Intent( Intent.ACTION_CALL, Uri.parse( dial ) ) );
                    finish();
                }


            }
        } ).setNegativeButton( "Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        } ).show();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }




}
