package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.groceryappp.Activity.Adapter.CartAdapter;
import com.example.groceryappp.Activity.AllModel.AdressModel;
import com.example.groceryappp.Activity.AllModel.SingleOrderDetails;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView rcvCart;

    ImageView cart,back;
    ArrayList<SingleOrderDetails> list;
    CartAdapter adapter;
    FirebaseFirestore database;
    SingleOrderDetails details;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    AdressModel model;
    String name;
    int sumPlus,sumMinus;
    LinearLayout emptyCart;
    int tempSum;
    AppCompatButton shopNow;

    TextView sum, savedPrice, addAdress, txtAdress;
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tempSum= intent.getIntExtra("sum",0);
            sum.setText("₹" +tempSum);
        }
    };

    public BroadcastReceiver receiver6 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int  plusmarketprice=intent.getIntExtra("plusmarketprice",0);
            int finalsaved=plusmarketprice-sumPlus;
            savedPrice.setText("₹"+finalsaved);
        }
    };
    public BroadcastReceiver receiver7 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int minusmarketprice=intent.getIntExtra("minusmarketprice",0);

            int finalsaved=minusmarketprice-sumMinus;
            savedPrice.setText("₹"+finalsaved);

        }
    };
    public BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sumPlus=intent.getIntExtra("sumplus",0);
            sum.setText("₹" + sumPlus);

        }
    };
    public BroadcastReceiver receiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int totalmarketprice=intent.getIntExtra("totalmarketprice",0);
            int finalsaved=totalmarketprice-tempSum;
            savedPrice.setText("₹" + finalsaved);
        }
    };
    public BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sumMinus=intent.getIntExtra("summinus",0);
            sum.setText("₹" + sumMinus);

        }
    };
    public BroadcastReceiver receiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String value= intent.getStringExtra("finalprice");
            if (value.equals("0")){
                placeOrderActivity.setVisibility(View.GONE);
                emptyCart.setVisibility(View.VISIBLE);
            }


        }
    };




    RelativeLayout placeOrderActivity;
    List<DocumentSnapshot> cartList;
    String add;
    private CardView CheckOut;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().hide();
        viewInitialize();
        fetchingDialog();
        list = new ArrayList<>();
        cartList = new ArrayList<>();
        rcvCart.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        adapter = new CartAdapter(CartActivity.this, list);
        rcvCart.setAdapter(adapter);
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        details = new SingleOrderDetails();
        //getting cart Size
back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        onBackPressed();
    }
});
        CheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CartActivity.this, UserPlaceOrderActivity.class);
                intent.putExtra("sum", sum.getText().toString().replace("₹",""));
                intent.putExtra("list", list);
                startActivity(intent);




            }
        });

/////receiveing sum amount of order
        LocalBroadcastManager.getInstance(CartActivity.this).registerReceiver(receiver5, new IntentFilter("TotalMarketPrice"));
        LocalBroadcastManager.getInstance(CartActivity.this).registerReceiver(receiver, new IntentFilter("Sum"));
        LocalBroadcastManager.getInstance(CartActivity.this).registerReceiver(receiver7, new IntentFilter("MinusMarketPrice"));
        LocalBroadcastManager.getInstance(CartActivity.this).registerReceiver(receiver6, new IntentFilter("PlusMarketPrice"));

        LocalBroadcastManager.getInstance(CartActivity.this).registerReceiver(receiver2, new IntentFilter("SumPlus"));
        LocalBroadcastManager.getInstance(CartActivity.this).registerReceiver(receiver3, new IntentFilter("SumMinus"));
        LocalBroadcastManager.getInstance(CartActivity.this).registerReceiver(receiver4, new IntentFilter("FinalPrice"));

        ///receiving saved amount



        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    if (documentSnapshot.exists()){
                        details = documentSnapshot.toObject(SingleOrderDetails.class);
                        details.setViewType(SingleOrderDetails.VIEW_TYPE_SINGLE_CART);
                        //details.setIdd(documentSnapshot.getId());
                        list.add(details);

                        if (list.size()!=0){
                            placeOrderActivity.setVisibility(View.VISIBLE);
                            emptyCart.setVisibility(View.GONE);



                        }
                    }else {
                        placeOrderActivity.setVisibility(View.GONE);
                        emptyCart.setVisibility(View.VISIBLE);
                    }


                }

                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        });





 

    }


    private void viewInitialize() {
        cart = findViewById(R.id.cart);
        back = findViewById(R.id.back);

        rcvCart = findViewById(R.id.rcv_cart);
        sum =findViewById(R.id.sum);
        savedPrice = findViewById(R.id.savePrice);
        placeOrderActivity = findViewById(R.id.placeOrderLayout);
        emptyCart = findViewById(R.id.emptycart);



        CheckOut =findViewById(R.id.checkout);





    }
    private void fetchingDialog() {
        progressDialog = new ProgressDialog(CartActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
   super.onBackPressed();
    }
}
