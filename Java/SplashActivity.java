package com.google.firebase.quickstart.fcm;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Youssef on 05-Jul-16.
 */
public class SplashActivity extends AppCompatActivity {

    //Duration of wait
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    //Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            Intent i;
            /**TODO
             * Check if signed in or not.
             */
            i = new Intent(SplashActivity.this, ChooseEntityActivity.class);
            SplashActivity.this.startActivity(i);
            SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
