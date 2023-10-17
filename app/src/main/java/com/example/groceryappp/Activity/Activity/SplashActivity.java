package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        logo=findViewById(R.id.logo);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale);
        logo.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
startActivity(new Intent(SplashActivity.this,ESign));
              // isLogin();

            }
        }, 1000);


    }

    private void isLogin() {
       /* if (auth.getCurrentUser() != null) {
            checkUserType();
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));

            finish();
        }*/
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);

// Check if a key is available in SharedPreferences
    if (sharedPreferences.contains("USER_ID")){
        String userId= SharedPreferenceManager.getInstance(SplashActivity.this).getUserId();
        if (userId!=null){
            checkUserType(userId);
        }

    }else {
        startActivity(new Intent(SplashActivity.this,OnBoardingActivity.class));

        finish();
    }

    }


    private void checkUserType(String userId) {
        //if usertype is user then send to the usermainactivity,if seller then seller activityt
        FirebaseClient.getInstance().collection("CurrentUser").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String type = String.valueOf(documentSnapshot.get("accountType"));
                if (type.equals("Seller")) {
                    Intent intent = new Intent(SplashActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);

                    startActivity(intent);
                    finish();
                }


            }
        });


    }

}