package com.example.groceryappp.Activity.Fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.groceryappp.Activity.Activity.UserPlaceOrderActivity;
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

public class CartFragment extends Fragment {

    RecyclerView rcvCart;

    ImageView cart;
    ArrayList<SingleOrderDetails> list;
    CartAdapter adapter;
    FirebaseFirestore database;
    SingleOrderDetails details;

    FirebaseAuth auth;
    AdressModel model;
    String name;
    int sumPlus,sumMinus;
    LinearLayout emptyCart;
    int tempSum;
    AppCompatButton shopNow;

    TextView sum, addAdress, txtAdress;
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

        }
    };
    public BroadcastReceiver receiver7 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
int minusmarketprice=intent.getIntExtra("minusmarketprice",0);

            int finalsaved=minusmarketprice-sumMinus;


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
    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        viewInitialize(view);

        list = new ArrayList<>();
        cartList = new ArrayList<>();
        rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(getContext(), list);
        rcvCart.setAdapter(adapter);
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        details = new SingleOrderDetails();
        //getting cart Size

        CheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent intent = new Intent(getContext(), UserPlaceOrderActivity.class);
                      String summ=sum.getText().toString().replace("₹","");
                        intent.putExtra("sum",summ );
                        intent.putExtra("list", list);
                        startActivity(intent);




            }
        });

/////receiveing sum amount of order
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver5, new IntentFilter("TotalMarketPrice"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("Sum"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver7, new IntentFilter("MinusMarketPrice"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver6, new IntentFilter("PlusMarketPrice"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver2, new IntentFilter("SumPlus"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver3, new IntentFilter("SumMinus"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver4, new IntentFilter("FinalPrice"));

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

                if (list.size()==0){
                    emptyCart.setVisibility(View.VISIBLE);
                }
            }
        });





        return view;

    }


    private void viewInitialize(View view) {
        cart = view.findViewById(R.id.cart);

        rcvCart = view.findViewById(R.id.rcv_cart);
        sum = view.findViewById(R.id.sum);

        placeOrderActivity = view.findViewById(R.id.placeOrderLayout);
        emptyCart = view.findViewById(R.id.emptycart);



        CheckOut = view.findViewById(R.id.checkout);
        




    }


    @Override
    public void onResume() {
        emptyCart.setVisibility(View.GONE);
        super.onResume();
    }
}