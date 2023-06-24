package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.groceryappp.Activity.Adapter.HeadLineCircular;
import com.example.groceryappp.Activity.Adapter.MixVegPriceDetails;
import com.example.groceryappp.Activity.AllModel.Headline;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.Fragment.CartFragment;
import com.example.groceryappp.Activity.Fragment.ProfileFragment;
import com.example.groceryappp.Activity.Fragment.UserHomeFragment;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    CircleImageView edtProfile;

Dialog dialog;
ProgressDialog progressDialog;
Toolbar toolbar;
ImageView profilePic;
AppCompatEditText search;

    private RecyclerView vegHeadline, allvegDeatails;
    private ArrayList<Headline> list;

    private ArrayList<SingleProductDetails> list2;
    private ViewFlipper offer;
    FirebaseFirestore database;
    FirebaseAuth auth;
    MixVegPriceDetails vegtableAdapter;
    private ImageView back, profile;
    private List<DocumentSnapshot> listSize;
    private HeadLineCircular adapter;
    private CardView cartsize;

    private TextView size,deliveryadress;
    private MeowBottomNavigation bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        viewInitialize();
database=FirebaseFirestore.getInstance();
auth=FirebaseAuth.getInstance();
bottomNavigation=findViewById(R.id.bottomNavigation);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.root_layout,new UserHomeFragment()).commit();
        ;

        final  String FCM_TOPIC="PUSH_NOTIFICATIONS";
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
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ViewAllActivity.class));
            }
        });
////fetching adress details
        database.collection("CurrentUser").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String city= String.valueOf(documentSnapshot.get("city"));
                String pin= String.valueOf(documentSnapshot.get("pincode"));
                deliveryadress.setText(city+","+pin);
                String profile= (String) documentSnapshot.get("profilePic");
                Glide.with(HomeActivity.this).load(profile).into(profilePic);


            }
        });

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.profilee));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_house_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_shopping_cart_checkout_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                // your codes
                switch (item.getId()){
                    case 1:
                        Bundle bundle = new Bundle();
                        bundle.putString("USER_TYPE", "USER");
                        ProfileFragment fragment = new ProfileFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction3=getSupportFragmentManager().beginTransaction();
                        transaction3.replace(R.id.cart_layout,fragment).commit();
                        findViewById(R.id.cart).setVisibility(View.VISIBLE);
                        findViewById(R.id.toolbar).setVisibility(View.GONE);
                        break;


                        case 2:
                            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.root_layout,new UserHomeFragment()).commit();
                            findViewById(R.id.cart).setVisibility(View.GONE);
                            findViewById(R.id.toolbar).setVisibility(View.VISIBLE);

                        break;
                        case 3:

                            FragmentTransaction transaction2=getSupportFragmentManager().beginTransaction();
                            transaction2.replace(R.id.cart_layout,new CartFragment()).commit();
                            findViewById(R.id.cart).setVisibility(View.VISIBLE);
                            findViewById(R.id.toolbar).setVisibility(View.GONE);
                        break;
                }
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
             switch (item.getId()){
                 case 1:
                     break;
                     case 2:
                     break;
                     case 3:
                     break;
             }
            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                // your codes
            }
        });
    }

    private void viewInitialize() {
        deliveryadress=findViewById(R.id.deliveryAd);
        edtProfile=findViewById(R.id.edit_profile);
        profilePic=findViewById(R.id.profile_pic);
        search=findViewById(R.id.search);
    }
     /*   database=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        viewInitilize();
//////////////////////////////////pending if net off then creashing//////////////////////////////



        final  String FCM_TOPIC="PUSH_NOTIFICATIONS";
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


search.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(HomeActivity.this, SearchActivity.class));
    }
});
edtProfile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
        intent.putExtra("userType","user");
        startActivity(intent);
    }
});
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.root_layout,new UserHomeFragment()).commit();

cart.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        loadProgressBar();
        startActivity(new Intent(HomeActivity.this, CartActivity.class));
       *//* toolbar.setVisibility(View.GONE);
        findViewById(R.id.root_layout).setVisibility(View.GONE);
        findViewById(R.id.root_layout_cart).setVisibility(View.VISIBLE);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.root_layout_cart,new CartFragment()).commit();*//*
dialog.dismiss();


    }
});*//*shop.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        toolbar.setVisibility(View.VISIBLE);
        findViewById(R.id.root_layout_cart).setVisibility(View.GONE);
        findViewById(R.id.root_layout).setVisibility(View.VISIBLE);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.root_layout,new UserHomeFragment()).commit();
    }
});*//*


     *//*   cartsize=findViewById(R.id.cartSize);
        size=findViewById(R.id.size);*//*
        String[] title = new String[]{"Home", "Cart", "Order", "Profile"};
        ///fetching size
        database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listSize = queryDocumentSnapshots.getDocuments();

                if (listSize.size() == 0) {
                    cartsize.setVisibility(View.GONE);

                } else {
                    cartsize.setVisibility(View.VISIBLE);
                    size.setText(String.valueOf(listSize.size()));
                }


            }
        });

        ///fetching size click of minus
        LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cartsize.setVisibility(View.VISIBLE);



                int sizeee = intent.getIntExtra("cartsize", 11);
                int fullSize = sizeee + 1;
                size.setText(String.valueOf(fullSize));
            }
        }, new IntentFilter("CartSize"));

        LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cartsize.setVisibility(View.VISIBLE);


                int sizeee = intent.getIntExtra("cartsizeminus", 11);
                int fullSize = sizeee - 1;
                size.setText(String.valueOf(fullSize));
                if (fullSize == 0) {

                    cartsize.setVisibility(View.GONE);
                }

            }
        }, new IntentFilter("CartSizeMinus"));

      *//*  pager2.setAdapter(new UserHomeFragmentAdapter(this));
        new TabLayoutMediator(tableLayout, pager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               *//**//* int[] icon= new int[]{R.drawable.apple2,R.drawable.apple2,R.drawable.apple2,R.drawable.apple2,R.drawable.apple2};

                tab.setText(title[position]);

                tab.setIcon(icon[0]);*//**//*

                switch (position) {
                    case 0:
                        tab.setText("Home");
                        //tab.setIcon(R.drawable.home);
                        break;
                    case 1:
                        tab.setText("Cart");
                        //tab.setIcon(R.drawable.cart);
                        break;
                   *//**//* case 2:
                        tab.setText("Profile");
                        //tab.setIcon(R.drawable.profilee);
                        break;*//**//*
                    case 2:
                        tab.setText("Order");
                        //tab.setIcon(R.drawable.orders);

                }
            }
        }).attach();*//*


    }

    @Override
    public void onBackPressed() {
finish();
    }

    private void loadProgressBar() {
        dialog=new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.google_pg_layout);
        dialog.setCancelable(false);
       dialog.show();
    }


    private void viewInitilize() {
        cart=findViewById(R.id.cart);
       cartsize =findViewById(R.id.cartSize);
    size =findViewById(R.id.size);
        search=findViewById(R.id.search);
        toolbar=findViewById(R.id.tool);
        edtProfile=findViewById(R.id.edit_profile);
        deliveryadress=findViewById(R.id.addelivery);
    }*/

}