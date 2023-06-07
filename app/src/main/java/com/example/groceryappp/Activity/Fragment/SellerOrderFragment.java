package com.example.groceryappp.Activity.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.groceryappp.Activity.Activity.AdminHomeActivity;
import com.example.groceryappp.Activity.Adapter.SellerOrderHeaderAdapter;
import com.example.groceryappp.Activity.Adapter.UserOrderHeaderAdapter;
import com.example.groceryappp.Activity.AllModel.AdminOrderDetails;
import com.example.groceryappp.Activity.AllModel.UserOrderHeader;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellerOrderFragment extends Fragment {

CardView newOrder,completeOrders;
ImageView back;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_seller, container, false);
        FragmentManager manager=getParentFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.rootLayoutOrder,new SellerNewOrderFragment()).commit();

viewInitialize(view);
newOrder.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FragmentManager manager=getParentFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.rootLayoutOrder,new SellerNewOrderFragment()).commit();
    }
});
back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(getContext(),AdminHomeActivity.class);
        intent.setAction(String.valueOf(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        intent.setAction(String.valueOf(Intent.FLAG_ACTIVITY_NEW_TASK));
        startActivity(intent);

    }
});
completeOrders.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FragmentManager manager=getParentFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.rootLayoutOrder,new SellerCompleteOrderFragment()).commit();
    }
});

        //intent.putExtra("buyerid", auth.getUid());

      /*  send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PreparedNotification(orderId, "processing");
            }
        });
*/



        return view;
    }

    private void viewInitialize(View view) {
        newOrder=view.findViewById(R.id.newOrders);
        completeOrders=view.findViewById(R.id.completeOrders);
        back=view.findViewById(R.id.back);
    }



}

