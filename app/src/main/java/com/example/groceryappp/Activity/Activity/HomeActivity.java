package com.example.groceryappp.Activity.Activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.groceryappp.Activity.Fragment.CartFragment;
import com.example.groceryappp.Activity.Fragment.ProfileFragment;
import com.example.groceryappp.Activity.Fragment.UserHomeFragment;

import com.example.groceryappp.Activity.Receiver.InternetConnectivityReceiver;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;


public class HomeActivity extends AppCompatActivity implements InternetConnectivityReceiver.ConnectivityListener {
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    private boolean isHomeDoubleClick = false;

    private InternetConnectivityReceiver connectivityReceiver;
    private boolean isProfileDoubleClick = false;
    private boolean isCartDoubleClick = false;

    private long lastClickTime = 0;
    private BottomSheetDialog bottomSheetDialog;

    private MeowBottomNavigation bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        bottomNavigation = findViewById(R.id.bottomNavigationnn);

    /*    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.root_layout, new UserHomeFragment()).commit();*/


        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.profilee));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_house_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_shopping_cart_checkout_24));

        replaceFragment(new UserHomeFragment(), 0, null);
    }


    private void replaceFragment(Fragment fragment, int flag, Bundle bundle) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (flag == 0) {
            transaction.add(R.id.cart_layout, fragment).addToBackStack(null);
        }
        if (flag == 2) {
            fragment.setArguments(bundle);
            transaction.replace(R.id.cart_layout, fragment).addToBackStack(null);
        } else {
            transaction.replace(R.id.cart_layout, fragment).addToBackStack(null);
        }

        transaction.commit();

    }

    @Override
    public void onBackPressed() {

    }


    public void onResume() {
        super.onResume();
        String who = getIntent().getStringExtra("WHO");
        if (who != null) {
            Bundle bundle = new Bundle();
            bundle.putString("USER_TYPE", "User");
            replaceFragment(new ProfileFragment(), 2, bundle);
        }
        if (who != null&&who.equals("order")) {
            replaceFragment(new CartFragment(), 1, null);
            isCartDoubleClick = true;
        }

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
        {


            final String FCM_TOPIC = "PUSH_NOTIFICATIONS";
            /////subscriber fcm topic
            FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
                @Override
                public void onClickItem(MeowBottomNavigation.Model item) {
                    switch (item.getId()) {
                        case 1:

                            Bundle bundle = new Bundle();
                            bundle.putString("USER_TYPE", "User");
                            replaceFragment(new ProfileFragment(), 2, bundle);
                            isProfileDoubleClick = true;
                            break;


                        case 2:
                            replaceFragment(new UserHomeFragment(), 0, null);
                            isHomeDoubleClick = true;
                            break;

                        case 3:

                            replaceFragment(new CartFragment(), 1, null);
                            isCartDoubleClick = true;
                            break;


                    }
                }
            });
            bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
                @Override
                public void onReselectItem(MeowBottomNavigation.Model item) {
                    switch (item.getId()) {
                        case 1:
                            if (!isProfileDoubleClick) {
                                Bundle bundle = new Bundle();
                                bundle.putString("USER_TYPE", "User");
                                replaceFragment(new ProfileFragment(), 2, bundle);
                                isProfileDoubleClick = true;
                                break;
                            } else {
                                break;
                            }
                        case 2:
                            if (!isHomeDoubleClick) {
                                replaceFragment(new UserHomeFragment(), 0, null);
                                isHomeDoubleClick = true;
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (!isCartDoubleClick) {
                                replaceFragment(new CartFragment(), 1, null);
                                isCartDoubleClick = true;
                                break;
                            } else {
                                break;
                            }

                    }
                }
            });
            bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
                @Override
                public void onShowItem(MeowBottomNavigation.Model item) {
                    switch (item.getId()) {
                        case 1:

                            break;


                        case 2:

                            break;

                        case 3:

                            break;

                    }
                }
            });
        }
    }
}
