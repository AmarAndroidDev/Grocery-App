package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Adapter.MixVegPriceDetails;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SingleProductActivity extends AppCompatActivity {
private TextView title,price,marketPrice,offerPrice;
private ImageView imgPrdct;
private SingleProductDetails listInfo;
private MixVegPriceDetails adapter;
private RecyclerView similarRcv;
private RatingBar ratingBar;
private AppCompatButton  submit;
    private ArrayList<SingleProductDetails> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);
        getSupportActionBar().hide();
        title = findViewById(R.id.title_veg);
        price = findViewById(R.id.price_veg);
        marketPrice = findViewById(R.id.marketPrice);
        imgPrdct = findViewById(R.id.img_prdct);
        offerPrice = findViewById(R.id.offer_price);
        similarRcv = findViewById(R.id.similarRcv);
        submit = findViewById(R.id.submit);
        ratingBar = findViewById(R.id.ratingBar);
        list = new ArrayList<>();
        similarRcv.setLayoutManager(new LinearLayoutManager(SingleProductActivity.this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MixVegPriceDetails(this, list);
        similarRcv.setAdapter(adapter);
        listInfo = (SingleProductDetails) getIntent().getSerializableExtra("prdct_info");
        if (listInfo != null) {
            title.setText(listInfo.getName() + " ," + " " + listInfo.getQty());
            price.setText("Rs." + listInfo.getPrice());
            marketPrice.setText("Mrp-Rs." + listInfo.getMarktPrice());

            Glide.with(this).load(listInfo.getImgUri()).into(imgPrdct);
            float discountPrice = listInfo.getMarktPrice() - listInfo.getPrice();
            float offerPercent = discountPrice * 100 / listInfo.getMarktPrice();
            offerPrice.setText(Math.round(offerPercent) + "% Off");
            gettingCateogoryProduct(listInfo.getCategory());
findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        onBackPressed();
    }
});
            submit.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    if (b){
submit.setEnabled(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            submit.setBackgroundTintList(getColorStateList(R.color.buttonBg));
                        }
                    }
                }
            });

submit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        findViewById(R.id.giveYourRev).setVisibility(View.GONE);
        findViewById(R.id.thanks).setVisibility(View.VISIBLE);
      submit.setVisibility(View.GONE);
      ratingBar.setIsIndicator(true);

    }
});
        }
    }
    private void gettingCateogoryProduct(String value) {
        FirebaseClient.getInstance().collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").collection("All Item").whereEqualTo("category", value).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                                                              @Override
                                                                                                                                                                                              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                                                  for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                                                      SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                                                      list.add(singleProductDetails);

                                                                                                                                                                                                  }
                                                                                                                                                                                                  adapter.notifyDataSetChanged();

                                                                                                                                                                                              }
                                                                                                                                                                                          }

        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SingleProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // Log.e("GROCERY", e.getMessage());
            }
        });


    }
}