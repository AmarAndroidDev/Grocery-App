package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.groceryappp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlaceOrderCompleteCelebration extends AppCompatActivity {
AppCompatButton shopMore;
TextView orderId,viewAllTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_complete_celebration);

        getSupportActionBar().hide();
        String orderIddd=getIntent().getStringExtra("orderId");
        int sum=getIntent().getIntExtra("sum",1);

        shopMore=findViewById(R.id.shop_more);
        orderId=findViewById(R.id.orderId);
        viewAllTime=findViewById(R.id.viewallitem);
        orderId.setText("Your order number - "+orderIddd);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        long data = Long.parseLong(orderIddd);
        String Date= formatter.format(data);
        viewAllTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PlaceOrderCompleteCelebration.this,UserOrderDetailsActivity.class);
                intent.putExtra("orderId",orderIddd);
                intent.putExtra("sum",sum);
                intent.putExtra("date",Date);
                intent.putExtra("order status","InProgress");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        shopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PlaceOrderCompleteCelebration.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(PlaceOrderCompleteCelebration.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}