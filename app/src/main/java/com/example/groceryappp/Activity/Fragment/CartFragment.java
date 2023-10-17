package com.example.groceryappp.Activity.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.groceryappp.Activity.Activity.HomeActivity;
import com.example.groceryappp.Activity.Adapter.CartAdapter;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Activity.UserPlaceOrderActivity;
import com.example.groceryappp.Activity.Utills.FirebaseCallback;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class CartFragment extends Fragment {
    private RelativeLayout placeOrderActivity;
    private RecyclerView rcvCart;
    private SharedPreferenceManager sharedPreferenceManager;
    private ImageView cart;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<SingleProductDetails> list;
    private CartAdapter adapter;
    LinearLayout  animationView;
    private String userId;
    private TextView sum;
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tempSum = intent.getIntExtra("sum", 0);
            sum.setText("₹" + tempSum);
        }
    };
    public BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sumMinus = intent.getIntExtra("summinus", 0);
            sum.setText("₹" + sumMinus);

        }
    };
    ;
    private CardView CheckOut;
    private int sumPlus, sumMinus, tempSum;
    public BroadcastReceiver receiver6 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int plusmarketprice = intent.getIntExtra("plusmarketprice", 0);
            int finalsaved = plusmarketprice - sumPlus;

        }
    };
    public BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sumPlus = intent.getIntExtra("sumplus", 0);
            sum.setText("₹" + sumPlus);

        }
    };
    private RelativeLayout emptyCart;
    public BroadcastReceiver receiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String value = intent.getStringExtra("finalprice");
            if (value.equals("0")) {
                placeOrderActivity.setVisibility(View.GONE);
                emptyCart.setVisibility(View.VISIBLE);
            }
        }
    };

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

        rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(getContext(), list, CartAdapter.VIEW_TYPE_SINGLE_CART);
        rcvCart.setAdapter(adapter);
        SharedPreferences preferences=getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getContext());
        if (sharedPreferenceManager != null && preferences.contains("USER_ID")) {
            userId=SharedPreferenceManager.getInstance(getContext()).getUserId();
        }


        if (userId != null) {
            gettingUserCartList();
        } else {
            emptyCart.setVisibility(View.VISIBLE);

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.pgbarHome).setVisibility(View.GONE);
            }
        },1000);
        //getting cart Size
        CheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkingSellerCriteria();
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swiperRefresh);
        swipeRefreshLayout.setColorSchemeColors(getContext().getColor(R.color.buttonBg));
        // swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getContext().getColor(R.color.bg2));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        /////receiveing sum amount of order
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("Sum"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver6, new IntentFilter("PlusMarketPrice"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver2, new IntentFilter("SumPlus"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver3, new IntentFilter("SumMinus"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver4, new IntentFilter("FinalPrice"));
        ///receiving saved amount


        return view;
    }

    private void checkingSellerCriteria() {
        /*showProgressDialog(getContext(), "Checking cart details...");*/
        //1sst check cart amount of seller satisfy or not
       animationView.setVisibility(View.VISIBLE);

        FirebaseClient.getInstance().collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long mincart = (long) documentSnapshot.get("MinCart");
                FirebaseCallback.fetchProductDetailsFromFirebase(userId, "cart", getContext(), new FirebaseCallback() {
                    @Override
                    public void onCallback(ArrayList<SingleProductDetails> productDetails) {
                        list.clear();
                        if (Long.parseLong(sum.getText().toString().replace("₹", "")) >= mincart) {
                            Intent intent = new Intent(getContext(), UserPlaceOrderActivity.class);
                            String summ = sum.getText().toString().replace("₹", "");
                            intent.putExtra("sum", summ);
                            intent.putExtra("list", productDetails);
                            startActivity(intent);

                            //view.findViewById(R.id.checkingCrt).setVisibility(View.GONE);
                        } else {
                           // view.findViewById(R.id.checkingCrt).setVisibility(View.GONE);
                            showMinCartBottomSheetDialog(mincart);
                            animationView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCallbackInfo(ArrayList<UserInfo> info) {

                    }
                });

            }
        });
    }

    private void gettingUserCartList() {
        FirebaseCallback.fetchProductDetailsFromFirebase(userId, "cart", getContext(), new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<SingleProductDetails> productDetails) {
                if (productDetails.size() != 0) {
                    list.addAll(productDetails);
                    adapter.notifyDataSetChanged();
                    placeOrderActivity.setVisibility(View.VISIBLE);
                    emptyCart.setVisibility(View.GONE);


                } else {
                    emptyCart.setVisibility(View.VISIBLE);


                }

            }

            @Override
            public void onCallbackInfo(ArrayList<UserInfo> info) {

            }
        });

    }

    private void viewInitialize(View view) {
        cart = view.findViewById(R.id.cart);
        rcvCart = view.findViewById(R.id.rcv_cart);
        sum = view.findViewById(R.id.sum);
        placeOrderActivity = view.findViewById(R.id.placeOrderLayout);
        emptyCart = view.findViewById(R.id.emptycart);
        CheckOut = view.findViewById(R.id.checkout);
        animationView=view.findViewById(R.id.checkingCrt);



    }

    @Override
    public void onResume() {


        super.onResume();
    }

    private void showMinCartBottomSheetDialog(long mincart) {

        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.mincart_details, null);
        dialog.setContentView(view);
        dialog.show();
        TextView sellerMinCart, useCart;
        sellerMinCart = view.findViewById(R.id.sellerMinCart);
        useCart = view.findViewById(R.id.useCart);

        sellerMinCart.setText("Min Amount - ₹" + mincart);
        long remainsValue = mincart - Long.parseLong(sum.getText().toString().replace("₹", ""));
        useCart.setText("Add more item worth " + "₹" + remainsValue);
        view.findViewById(R.id.shop_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), HomeActivity.class));

            }
        });


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
       // Toast.makeText(getContext(), "destroycartFregment", Toast.LENGTH_SHORT).show();
        Log.e("frag", "destroycartFregment");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("frag", "pausecart");
    }
}

