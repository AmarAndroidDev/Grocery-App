package com.example.groceryappp.Activity.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.groceryappp.Activity.Adapter.SellerOrderHeaderAdapter;
import com.example.groceryappp.Activity.AllModel.UserOrderHeader;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SellerNewOrderFragment extends Fragment {
    ArrayList<UserOrderHeader> list;
    SellerOrderHeaderAdapter adapter;
    private LinearLayout emptyOrder;
    RecyclerView rcvHeader;
    FirebaseFirestore database;
    FirebaseAuth auth;

    String buyerId;

    public SellerNewOrderFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
 View view =inflater.inflate(R.layout.fragment_seller_new_order, container, false);

        list=new ArrayList<>();
        viewInitialize(view);
        //set up recylerview
        adapter=new SellerOrderHeaderAdapter(list,getContext());
        rcvHeader.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvHeader.setAdapter(adapter);
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
///fetching oreders of seller
        database.collection("CurrentUser").document(auth.getUid()).collection("All orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot snapshot:task.getResult()) {


                    UserOrderHeader header=snapshot.toObject(UserOrderHeader.class);
                    list.add(header);
                    if (list.size()!=0){
                        emptyOrder.setVisibility(View.GONE);

                    }else {
                        emptyOrder.setVisibility(View.VISIBLE);
                    }

                }
                adapter.notifyDataSetChanged();
            }
        });



        String orderId =getActivity().getIntent().getStringExtra("orderId");
        String seller =getActivity().getIntent().getStringExtra("sellerId");
        buyerId =getActivity().getIntent().getStringExtra("buyerId");

        return view;
    }
    private void viewInitialize(View view) {
        rcvHeader=view.findViewById(R.id.rcvadminorder);
        emptyOrder=view.findViewById(R.id.emptyOrder);
    }

    private void PreparedNotification(String orderId, String message) {
        ////when seller change order status,send notification to user

        ///prepare data for nootificartion
        final String FCM_TOPIC = "PUSH_NOTIFICATION";
        String NOTIFICATION_TOPIC = "/topics/" + FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your OrderId" + orderId;///timestamp is the order id
        String NOTIFICATION_MESSAGE = "" + message;
        String NOTIFICATION_TYPE = "ORDERSTATUSCHANGED";


        //PREPARE JSON(what to send where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            ///WHAT TO SEND
            notificationBody.put("notificationtype", NOTIFICATION_TYPE);
            notificationBody.put("selleruid", auth.getUid());
            notificationBody.put("buyerid", buyerId);
            notificationBody.put("orderid", orderId);//timestamp is the order id
            notificationBody.put("notificationtitle", NOTIFICATION_TITLE);
            notificationBody.put("notificationmessage", NOTIFICATION_MESSAGE);
            ///WHERE TO SEND
            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBody);



        } catch (Exception e) {
            Toast.makeText(getContext(), "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        // timestamp is the order id
        sendFcmNotification(notificationJo);

    }

    private void sendFcmNotification(JSONObject notificationJo) {
        JsonObjectRequest request = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//notification send

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //notification failed


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //put required header
                final String FCM_SERVER_KEY = "AAAAsHj7t8M:APA91bF4YypGkDBuj8E7h7s7oZwkrJi9EcsPZxZw5py59o21WFAqTg5i3WN-b3B0r5uSa7iDJMXypcaHyG_Wt5bRRUke5ZtVd_FQfr3mP8aQXR6e0NdVGLrSYVpq4plPZR1slZ7WvwCK";


                Map<String, String> map = new HashMap<>();


                map.put("Content-Type", "application/json");
                map.put("Authorization", "key=" + FCM_SERVER_KEY);

                return map;


            }

        };
        ////Enque request
        Volley.newRequestQueue(getContext()).add(request);
    }
}