package com.proxima.Wubble.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.parse.Parse;
import com.parse.ParseUser;
import com.proxima.Wubble.R;

import static com.proxima.Wubble.Constants.PARSE_APP_ID;
import static com.proxima.Wubble.Constants.PARSE_CLIENT_KEY;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
        setContentView(R.layout.activity_splash);

        Runnable openLogin = new Runnable() {
            @Override
            public void run() {
                Intent toMain = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(toMain);
                finish();
            }
        };

        Runnable openMain = new Runnable() {
            @Override
            public void run() {
                Intent toMain = new Intent(SplashActivity.this, FeedActivity.class);
                startActivity(toMain);
                finish();
            }
        };

        ParseUser currentUser = ParseUser.getCurrentUser();
        Handler handler = new Handler();
        if (currentUser == null) {
            handler.postDelayed(openLogin, 1000);
        } else {
            handler.postDelayed(openMain, 1000);
        }


    }


}
