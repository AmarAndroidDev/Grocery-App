package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.HomeActivity;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth auth;
    String uri;
    String Namee, Emaill;
    FirebaseFirestore database;
    String userType;
    private TextView logout, orders;
    private CircleImageView profile;
    private ProgressDialog dialog;
    private TextView name, email, number, fullAdress;
    private String usName, usNumber, usEmial, usAdress, usUri;
    private String sName, sNumber, sEmial, sAdress, sUri;
    private double usLattiude, usLongitude, selerLattiude, sellerLongitude;
    private ImageView edAdres, edprofilee, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        viewInitialize();
        ////checking if user then show ordrs unless hide ordres
        userType = getIntent().getStringExtra("userType");
        fetchingDialog();
        if (userType.equals("seller")) {

            orders.setVisibility(View.GONE);
            String id = auth.getUid();
            fetchingSellerDetails();
        } else {
            fetchingUserDetail();

        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(ProfileActivity.this);
                dialog1.setIcon(R.drawable.baseline_logout);
                dialog1.setTitle("Logout").setMessage("Are you sure ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        auth.signOut();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

            }
        });
        if (userType.equals("seller")) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                }
            });
        } else {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
            });
        }

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, UserOrderHeaderActivity.class);

                intent.putExtra("ordertype", "setting");
                startActivity(intent);

            }
        });
        edprofilee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("seller")) {
                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    intent.putExtra("userType", "seller");
                    intent.putExtra("number", sNumber);
                    intent.putExtra("name", sName);
                    intent.putExtra("profileImg", sUri);
                    startActivityForResult(intent, 6);
                } else {
                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    intent.putExtra("userType", "user");
                    intent.putExtra("number", usNumber);
                    intent.putExtra("name", usName);
                    intent.putExtra("profileImg", usName);
                    startActivityForResult(intent, 6);
                }


            }
        });
        edAdres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AdressActivity.class);

                  startActivityForResult(intent,2);


            }
        });
    }

    private void fetchingSellerDetails() {
        database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                sName = String.valueOf(documentSnapshot.get("name"));
                sNumber = String.valueOf(documentSnapshot.get("number"));
                sEmial = String.valueOf(documentSnapshot.get("email"));
                sAdress = String.valueOf(documentSnapshot.get("fulladress"));
                selerLattiude = Double.parseDouble(String.valueOf(documentSnapshot.get("latitude")));
                sellerLongitude = Double.parseDouble(String.valueOf(documentSnapshot.get("longitude")));
                fullAdress.setText(sAdress);
                uri = String.valueOf(documentSnapshot.get("profilepic"));
                Glide.with(ProfileActivity.this).load(uri).into(profile);
                number.setText(sNumber);
                email.setText(sEmial);
                name.setText(sName);
                dialog.dismiss();

            }
        });

    }

    private void fetchingUserDetail() {
        database.collection("CurrentUser").document(auth.getUid()).get().addOnSuccessListener(

        new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

               usName = String.valueOf(documentSnapshot.get("name"));
                usNumber = String.valueOf(documentSnapshot.get("number"));
                usEmial = String.valueOf(documentSnapshot.get("email"));
                usAdress = String.valueOf(documentSnapshot.get("fullAd"));

                fullAdress.setText(usAdress);
                uri = String.valueOf(documentSnapshot.get("profilePic"));


                Glide.with(ProfileActivity.this).load(uri).into(profile);
                number.setText(usNumber);
                email.setText(usEmial);
                name.setText(usName);
                dialog.dismiss();
            }
        });

    }
    private void fetchingDialog() {
        dialog = new ProgressDialog(ProfileActivity.this);
        dialog.setMessage("Fetching Details");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 6) {
                String nameUser = data.getStringExtra("name");
                String numberUser = data.getStringExtra("number");
                name.setText(nameUser);
                number.setText(numberUser);
            }
            if (requestCode == 2) {
                String nameUser = data.getStringExtra("name");
                String numberUser = data.getStringExtra("number");
                name.setText(nameUser);
                number.setText(numberUser);


            }
        }
    }
/*
    @Override
    protected void onResume() {

        super.onResume();
fetchingDialog();
        if (userType.equals("seller")){
            orders.setVisibility(View.GONE);
            String id=auth.getUid();
            fetchingSellerDetails();
        }
        else {
            fetchingUserDetail();

        }

    }*/

    private void viewInitialize() {
        number = findViewById(R.id.user_number);
        profile = findViewById(R.id.user_profile);
        email = findViewById(R.id.user_email);
        name = findViewById(R.id.user_name);
        fullAdress = findViewById(R.id.fulladress);
     //   logout = findViewById(R.id.logout);
        edAdres = findViewById(R.id.edAdress);
        orders = findViewById(R.id.orders);
        edprofilee = findViewById(R.id.edprofile);
        back = findViewById(R.id.back);
        logout = findViewById(R.id.logout);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        finish();
    }
}