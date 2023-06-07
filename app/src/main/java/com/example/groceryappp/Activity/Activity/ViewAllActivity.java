package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.Adapter.ProductDetailsAdapter;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
public class ViewAllActivity extends AppCompatActivity {
private RecyclerView singlePrdctRcv;
private CardView checkout;
private FirebaseFirestore database;

private TextView totalPrice;
   private ProductDetailsAdapter vegtableAdapter;
    private ArrayList<SingleProductDetails> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        getSupportActionBar().hide();
        viewInitialize();

        database=FirebaseFirestore.getInstance();
//        checkout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent=new Intent(ViewAllActivity.this,CartActivity.class);
////                startActivity(intent);
//
//
//            }
//        });
        String type=getIntent().getStringExtra("type");
        ////getting Vegetables
        if (type!=null && type.equalsIgnoreCase("vegetable")){
            database.collection("CurrentUser").document("ODR0CUvoQ5ecbEaU2ZWVENCLGEt2").collection("All Item").whereEqualTo("type","vegetable").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                for (QueryDocumentSnapshot q:task.getResult()){
                                                                                    // SingleProductDetails singleProductDetails=q.toObject(SingleProductDetails.class);
                                                                                    //  list2.add(singleProductDetails);
                                                                                    list.add(new SingleProductDetails(q.get("Name").toString(), Integer.parseInt(q.get("price").toString()) ,q.get("qty").toString()));

                                                                                }
                                                                                vegtableAdapter.notifyDataSetChanged();

                                                                            }
                                                                        }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        ////getting Fruits
        if (type!=null && type.equalsIgnoreCase("fruit")){
            database.collection("All Item").whereEqualTo("type","fruit").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                for (QueryDocumentSnapshot q:task.getResult()){
                                                                                    // SingleProductDetails singleProductDetails=q.toObject(SingleProductDetails.class);
                                                                                    //  list2.add(singleProductDetails);
                                                                                    list.add(new SingleProductDetails(q.get("Name").toString(),Integer.parseInt(q.get("price").toString()),q.get("qty").toString()));

                                                                                }
                                                                                vegtableAdapter.notifyDataSetChanged();

                                                                            }
                                                                        }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        singlePrdctRcv.setLayoutManager(new LinearLayoutManager(this));
    list  =new ArrayList<>();
        vegtableAdapter=new ProductDetailsAdapter(this,list);
        singlePrdctRcv.setAdapter(vegtableAdapter);

    }



    private void viewInitialize() {
        singlePrdctRcv=findViewById(R.id.single__prdt_rcv);
        checkout=findViewById(R.id.checkout);
        totalPrice=findViewById(R.id.total_price);
    }
}