package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import com.example.groceryappp.Activity.Fragment.AdminOrderFragment;
import com.example.groceryappp.Activity.Fragment.AdminProductFragment;
import com.example.groceryappp.Activity.Fragment.CartFragment;

import com.example.groceryappp.Activity.Fragment.ProfileFragment;

import com.example.groceryappp.Activity.Fragment.UserHomeFragment;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminHomeActivity extends AppCompatActivity {
    private MeowBottomNavigation bottomNavigation;
    private boolean isHomeDoubleClick = false;
    private boolean isProfileDoubleClick = false;
    private boolean isCartDoubleClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        getSupportActionBar().hide();
        viewInitialize();

            replaceFragment(new AdminProductFragment(), 0, null);

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.profilee));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_house_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_shopping_cart_checkout_24));


        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()) {
                    case 1:

                        Bundle bundle = new Bundle();
                        bundle.putString("USER_TYPE", "Seller");
                        replaceFragment(new ProfileFragment(), 2, bundle);
                        isProfileDoubleClick = true;
                        break;
                    case 2:
                        replaceFragment(new AdminProductFragment(), 0, null);
                        isHomeDoubleClick = true;
                        break;
                    case 3:
                        replaceFragment(new AdminOrderFragment(), 1, null);
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
                            replaceFragment(new AdminOrderFragment(), 1, null);
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


        final String FCM_TOPIC = "PUSH_NOTIFICATIONs";
/////subscribr fcm topic
        FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void viewInitialize() {
        bottomNavigation = findViewById(R.id.bottomNavigation);

    }

    private void replaceFragment(Fragment fragment, int flag, Bundle bundle) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (flag == 0) {
            transaction.add(R.id.root_Layout, fragment).addToBackStack(null);
        }
        if (flag == 2) {
            fragment.setArguments(bundle);
            transaction.replace(R.id.root_Layout, fragment).addToBackStack(null);
        } else {
            transaction.replace(R.id.root_Layout, fragment).addToBackStack(null);
        }

        transaction.commit();

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        String who = getIntent().getStringExtra("WHO");
        if (who != null) {
            if (who.equals("profile")) {
                Bundle bundle = new Bundle();
                bundle.putString("USER_TYPE", "Seller");
                replaceFragment(new ProfileFragment(), 2, bundle);

            }
            if (who.equals("admin_order")) {
                replaceFragment(new AdminOrderFragment(), 1, null);
            }
        }

    }
}