package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.groceryappp.Activity.Fragment.PrdouctFragment;
import com.example.groceryappp.Activity.Fragment.ProfileFragment;
import com.example.groceryappp.Activity.Fragment.SellerOrderFragment;
import com.example.groceryappp.R;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminHomeActivity extends AppCompatActivity {
private boolean available=false;
    private ViewPager2 pager2;
    private TabLayout layoout;
    CardView orders,product;
    ImageView edtProfile,search;

    private TextView shopStatus;
    private SwitchCompat btnSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        getSupportActionBar().hide();
        viewInitialize();
        edtProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, ProfileFragment.class);
                intent.putExtra("userType", "seller");
                startActivity(new Intent(intent));
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, AdminSearchActivity.class));
            }
        });
        //checking shop open or not;
        SharedPreferences sharedPreferences = getSharedPreferences("ShopStatus", MODE_PRIVATE);
if (sharedPreferences.contains("status")){
    String value= sharedPreferences.getString("status",null);

    if (value.equals("Shop Open")){
        btnSwitch.setChecked(true);
    }else {
        btnSwitch.setChecked(false);
    }
}else
    btnSwitch.setChecked(false);
  /* btnSwitch.setChecked(true);
        shopStatus.setText("Shop open");
        shopStatus.setTextColor(Color.WHITE);
*/


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
///////////calling product fraagment
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.rooot_layout, new PrdouctFragment()).commit();

        /*String [] Title={"PRODUCT","ORDER","PROFILE"};
        pager2.setAdapter(new FragmentAdapter(AdminHomeActivity.this));
        ///////  ((tab, position) ->tab.setText(Title[position]) )///we can pass this new 3 rd parameter
        new TabLayoutMediator(layoout, pager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(Title[position]);

            }
        }).attach();
*/


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.root_layout, new PrdouctFragment()).commit();
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.root_layout).setVisibility(View.GONE);
                findViewById(R.id.order_layout).setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.order_layout, new SellerOrderFragment()).commit();


            }
        });
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.root_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.order_layout).setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.root_layout, new PrdouctFragment()).commit();

            }
        });
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("Shop Status", MODE_PRIVATE);
                if (preferences.contains("status")) {
                    String status = preferences.getString("status", "");
                    if (status.equals("Shop Closed")) {
                        showDialoggg("Want to close Shop", "Shop Closed");

                    } else if (status.equals("Shop Open")) {
                        showDialoggg("Want to open Shop", "Shop Open");
                    }


                }

             showDialoggg("Shop Open","Shop Open");

            }
        });
    }

    private void showDialoggg(String title,String textstatus) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(AdminHomeActivity.this).setCancelable(false);
        dialog.setMessage("Are you sure ?").setTitle(title).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shopStatus.setText(textstatus);
                SharedPreferences sharedPreferences=getSharedPreferences("Shop Status",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("status",textstatus);
                editor.apply();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    protected void onResume() {


        super.onResume();


        SharedPreferences sharedPreferences=getSharedPreferences("ShopStatus",MODE_PRIVATE);
        if (sharedPreferences.contains("shopOpen")){
            if (sharedPreferences.getBoolean("shopOpen",true)){
                shopStatus.setText("Shop open");
                shopStatus.setTextColor(Color.WHITE);
            }
            shopStatus.setText("shop closed");
            shopStatus.setTextColor(Color.BLACK);
        }
        else {

        }
    }
    private void viewInitialize() {
        orders=findViewById(R.id.orders);
        product=findViewById(R.id.product);
        edtProfile=findViewById(R.id.edit_profile);
        btnSwitch=findViewById(R.id.switch_btn);
        shopStatus=findViewById(R.id.shop_status);
        search=findViewById(R.id.search);
    }


}