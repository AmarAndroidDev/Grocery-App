package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlaceOrderCompleteCelebration extends AppCompatActivity {
    private AppCompatButton shopMore;
    private TextView orderId, viewAllItem;
    String  currentDay,currentTime,currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_complete_celebration);
        getSupportActionBar().hide();
        String orderIddd = getIntent().getStringExtra("orderId");
        int sum = getIntent().getIntExtra("sum", 1);
        int saved = getIntent().getIntExtra("saved", 1);
        shopMore = findViewById(R.id.shop_more);
        orderId = findViewById(R.id.orderId);
        viewAllItem = findViewById(R.id.viewallitem);
        orderId.setText("ORDER NO.:- " + orderIddd);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        long data = Long.parseLong(orderIddd);
     currentDate = formatter.format(data);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        currentDay = dayFormat.format(calendar.getTime());
        currentTime = timeFormat.format(calendar.getTime());
        String finalTim=currentDay.substring(0,3)+currentDate+"("+currentTime+")";
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.details).setVisibility(View.VISIBLE);
                findViewById(R.id.relativeLayout2).setVisibility(View.VISIBLE);
                findViewById(R.id.bottomLay).setVisibility(View.VISIBLE);
            }
        },1650);
        viewAllItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceOrderCompleteCelebration.this, UserOrderDetailsActivity.class);
                UserInfo orderInfo = new UserInfo();
                orderInfo.setOrderId(orderIddd);
                orderInfo.setStatus("InProgress");
                orderInfo.setDate(currentDate);
                orderInfo.setSum(sum);
                orderInfo.setcTimePrgrs(finalTim);
                orderInfo.setSaved(""+saved);
                intent.putExtra("orderDetails", orderInfo);
                intent.putExtra("checkingUserOrSeller", "orders");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        shopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceOrderCompleteCelebration.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlaceOrderCompleteCelebration.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}