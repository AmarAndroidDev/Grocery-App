package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.groceryappp.Activity.HomeActivity;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                isLogin();
            }
        }, 1000);


    }

    private void isLogin() {
        if (auth.getCurrentUser() != null) {
            checkUserType();
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }


    private void checkUserType() {
        //if usertype is user then send to the usermainactivity,if seller then seller activityt
        database.collection("CurrentUser").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String type = String.valueOf(documentSnapshot.get("accountType"));
                if (type.equals("Seller")) {
Intent intent=new Intent(SplashActivity.this, AdminHomeActivity.class);

                    startActivity(intent);
                    finish();

                }
                if (type.equals("user")) {

                    Intent intent=new Intent(SplashActivity.this, HomeActivity.class);

                    startActivity(intent);
                    finish();
                }

            }
        });


    }

}