package com.example.groceryappp.Activity.Fragment;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.groceryappp.Activity.Adapter.UserOrderHeaderAdapter;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Receiver.InternetConnectivityReceiver;
import com.example.groceryappp.Activity.Utills.FirebaseCallback;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminOrderFragment extends Fragment implements InternetConnectivityReceiver.ConnectivityListener {
    String orderType;
    private InternetConnectivityReceiver connectivityReceiver;
    private RecyclerView rcvAdminOrder;
    private ArrayList<UserInfo> list;
    private UserOrderHeaderAdapter adapter;
    private ImageView back;

    private FirebaseFirestore database;
    private ProgressBar pgbar;
    private FirebaseAuth auth;
    private TextView sum, savedPrice;
    private LinearLayout emptyCart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_order, container, false);
        viewInitialize(view);

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();
        adapter = new UserOrderHeaderAdapter(list, getContext(), "All orders");
        rcvAdminOrder.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        //this reverse layout set true because it will show top of list ,which   list is  added
        rcvAdminOrder.setAdapter(adapter);
        gettingUserOrder();
        //set up recylerview


        return view;
    }


    private void gettingUserOrder() {
        FirebaseCallback.fetchUserInfoFromFirebase("NAPuTkYOldg4M8FHUUwKeNvkBK73", "All orders", getContext(), new FirebaseCallback() {
                    @Override
                    public void onCallback(ArrayList<SingleProductDetails> productDetails) {

                    }

                    @Override
                    public void onCallbackInfo(ArrayList<UserInfo> info) {
                        list.addAll(info);
                        if (list.size() != 0) {
                            emptyCart.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                        pgbar.setVisibility(View.GONE);
                    }

                });

                database.collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").collection("All orders").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

             /*   if (list.size() == 0) {
                    emptyCart.setVisibility(View.VISIBLE);
                } else {
                    emptyCart.setVisibility(View.GONE);
                }*/
                    }
                });

    }


    private void viewInitialize(View view) {
        rcvAdminOrder = view.findViewById(R.id.rcvAdminOrder);
        back = view.findViewById(R.id.back);
        emptyCart = view.findViewById(R.id.emptycart);
        pgbar = view.findViewById(R.id.pgbar);
    }

    public void onResume() {
        super.onResume();
        gettingUserOrder();
        connectivityReceiver = new InternetConnectivityReceiver(this);
        getContext().registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    public void onPause() {
        super.onPause();
        if (connectivityReceiver != null) {
            getContext().unregisterReceiver(connectivityReceiver);
        }
    }

    @Override
    public void onInternetConnected() {

    }



}