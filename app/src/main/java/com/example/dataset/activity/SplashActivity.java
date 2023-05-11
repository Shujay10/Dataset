package com.example.dataset.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.dataset.MyClass;
import com.example.dataset.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int splashTimer = 500;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Intent toLogin;
    Intent toMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        toLogin = new Intent(SplashActivity.this, LoginActivity.class);
        toMain = new Intent(SplashActivity.this, MainActivity.class);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        splashHandler();

    }

    private void splashHandler(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(mUser == null) {
                startActivity(toLogin);
                finish();
                }
                else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String Name = user.getDisplayName();
                    MyClass.setUserName(Name);
                    startActivity(toMain);
                    finish();
                }

            }
        },splashTimer);

    }
}