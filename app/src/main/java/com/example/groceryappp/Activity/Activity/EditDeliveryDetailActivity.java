package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.groceryappp.R;

public class EditDeliveryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delivery_detail);
        getSupportActionBar().hide();
    }
}