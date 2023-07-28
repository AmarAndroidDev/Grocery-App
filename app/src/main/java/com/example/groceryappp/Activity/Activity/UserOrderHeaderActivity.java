package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.groceryappp.Activity.Adapter.UserOrderHeaderAdapter;

import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Receiver.InternetConnectivityReceiver;
import com.example.groceryappp.Activity.Utills.FirebaseCallback;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserOrderHeaderActivity extends AppCompatActivity implements InternetConnectivityReceiver.ConnectivityListener {

    private InternetConnectivityReceiver connectivityReceiver;
    private RecyclerView rcvHeader;
    private ArrayList<UserInfo> list;
    private UserOrderHeaderAdapter adapter;
    private ImageView back;

    private FirebaseFirestore database;


    private TextView sum, savedPrice;
    private RelativeLayout emptyCart;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_header);
        getSupportActionBar().hide();
        viewInitialize();
        userId= SharedPreferenceManager.getInstance(UserOrderHeaderActivity.this).getUserId();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pgbarHome).setVisibility(View.GONE);
            }
        },1000);
        database = FirebaseFirestore.getInstance();

        list = new ArrayList<>();
        //set up recylerview
        adapter = new UserOrderHeaderAdapter(list, this, "orders");
        rcvHeader.setLayoutManager(new LinearLayoutManager(this));
        rcvHeader.setAdapter(adapter);
        FirebaseCallback.fetchUserInfoFromFirebase(userId, "orders", this, new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<SingleProductDetails> productDetails) {

            }

            @Override
            public void onCallbackInfo(ArrayList<UserInfo> info) {
                list.addAll(info);
                adapter.notifyDataSetChanged();

                if (info.size() == 0) {
                    emptyCart.setVisibility(View.VISIBLE);

                } else {
                    emptyCart.setVisibility(View.GONE);

                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserOrderHeaderActivity.this, HomeActivity.class);
        intent.putExtra("WHO", "user");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        super.onBackPressed();
    }

    private void viewInitialize() {
        rcvHeader = findViewById(R.id.rcv_order_header);
        back = findViewById(R.id.back);
        emptyCart = findViewById(R.id.emptycart);

    }

    public void onResume() {
        super.onResume();
        connectivityReceiver = new InternetConnectivityReceiver(this);
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
    }

    @Override
    public void onInternetConnected() {

    }


}