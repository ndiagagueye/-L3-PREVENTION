package com.example.gueye.memoireprevention2018.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.gueye.memoireprevention2018.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseUser fUser;
    private static int SPLASH_TIME = 2000; //This is 8 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash_screen );

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fUser == null){

            androidBallSpinFadeLoaderProgressBar();
            new Timer().schedule( new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class) );
                    finish();
                    overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                }
            } , SPLASH_TIME );

        }

    }




    private void androidBallSpinFadeLoaderProgressBar() {
        findViewById(R.id.material_design_ball_spin_fade_loader).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fUser != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class) );
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
            finish();

        }

    }
}
