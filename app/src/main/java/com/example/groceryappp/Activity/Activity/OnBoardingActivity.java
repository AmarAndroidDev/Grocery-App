package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.groceryappp.Activity.Adapter.ViewPagerAdapter;
import com.example.groceryappp.Activity.AllModel.ViewPagerModel;
import com.example.groceryappp.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class OnBoardingActivity extends AppCompatActivity {
    private ArrayList<ViewPagerModel> listviewpager;
    private DotsIndicator indicator;
    private ViewPager pager2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_on_boarding);
        //adding viewpager
        pager2 =findViewById(R.id.viewpager1);
        indicator =findViewById(R.id.dots_indicator);
        listviewpager = new ArrayList<ViewPagerModel>();
        listviewpager.add(new ViewPagerModel(R.drawable.onboard1,""));

        listviewpager.add(new ViewPagerModel(R.drawable.img_14,"DIRECT FROM FARM"));
        listviewpager.add(new ViewPagerModel(R.drawable.img_17,""));
        listviewpager.add(new ViewPagerModel(R.drawable.onboard2,"SHOP FROM YOUR LOCALITY"));
        pager2.setAdapter(new ViewPagerAdapter(OnBoardingActivity.this, listviewpager));
        indicator.attachTo(pager2);
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OnBoardingActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


    }
}