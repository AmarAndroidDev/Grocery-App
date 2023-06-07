package com.example.groceryappp.Activity.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.DialogTitle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.groceryappp.Activity.Adapter.CartAdapter;
import com.example.groceryappp.Activity.AllModel.SingleOrderDetails;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellerOrderDetailsActivity extends AppCompatActivity {
    FirebaseFirestore database;
    FirebaseAuth auth;
    CartAdapter adapter;
    private RecyclerView rcv;
    ArrayList<SingleOrderDetails> list;
    private AppCompatButton editOrder,map;
    private ImageView back;
    String imguri;
    String orderId,userId,sellerlatitude,sellerlongitude,buyerLatitude,buyerLongitude;
    private TextView txtOrderid,paymentMode,customerName,number,customerAdress,orderstatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_details);
        getSupportActionBar().hide();
        database=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        viewInitialize();
  orderId=getIntent().getStringExtra("orderId");
        String name=getIntent().getStringExtra("name");
        String customerPh=getIntent().getStringExtra("number");
        String adress=getIntent().getStringExtra("adress");
        String status=getIntent().getStringExtra("orderstatus");
        userId=getIntent().getStringExtra("uid");
        buyerLatitude=getIntent().getStringExtra("latitude");
        buyerLongitude=getIntent().getStringExtra("longitude");
        imguri=getIntent().getStringExtra("imguri");
        orderstatus.setText(status);

        customerName.setText(name);
        customerAdress.setText(adress);
        number.setText(customerPh);
        txtOrderid.setText(orderId);
        paymentMode.setText("Cash On Delivery");
        list=new ArrayList<>();
        //set up recylerview
        adapter=new CartAdapter(SellerOrderDetailsActivity.this,list);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(adapter);
///fetching orders single item
        database.collection("CurrentUser").document(auth.getUid()).collection("All orders").document(orderId).collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot:queryDocumentSnapshots.getDocuments()){
                    SingleOrderDetails header=snapshot.toObject(SingleOrderDetails.class);
                    list.add(header);
                }
                adapter.notifyDataSetChanged();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ////fetching seller adress lattitude and longitude
        database.collection("CurrentUser").document("FC8zN067TtZDFi6rwi8QRX9PgN52").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               sellerlatitude= String.valueOf(documentSnapshot.get("latitude"));
             sellerlongitude= String.valueOf(documentSnapshot.get("longitude"));


            }
        });

        editOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionDialog();
            }
        }); 
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMap();
            }
        });


    }



    private void showOptionDialog() {
       AlertDialog.Builder dialog=new AlertDialog.Builder(this);
       dialog.setTitle("Edit Order Status");
       String option[]={"Processing","Completed","Cancel"};
       dialog.setItems(option, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               String selectedOption=option[i];
               saveSatusToFirebaseDB(selectedOption);
               String msg="Order is now"+selectedOption;
               PreparedNotification(orderId,msg);

           }
       }).show();
    }

    private void saveSatusToFirebaseDB(String selectedOption) {
        ///first update in seller db
        database.collection("CurrentUser").document(auth.getUid()).collection("All orders").document(orderId).update("status",selectedOption).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                ///now update in user db
                database.collection("CurrentUser").document(userId).collection("orders").document(orderId).update("status",selectedOption).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        orderstatus.setText(selectedOption);
                        if (selectedOption=="Processing"){
                            orderstatus.setTextColor(Color.BLUE);
                        } if (selectedOption=="Completed"){
                            orderstatus.setTextColor(Color.GREEN);
                        }else {
                            orderstatus.setTextColor(Color.RED);
                        }

                        Toast.makeText(SellerOrderDetailsActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void PreparedNotification(String orderId,String message) {
        ////when seller change order ,send notification to user


        ///prepare data for nootificartion
        final String FCM_TOPIC = "PUSH_NOTIFICATIONS";
        String NOTIFICATION_TOPIC = "/topics/" + FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order" + orderId;///timestamp is the order id
        String NOTIFICATION_MESSAGE = ""+message;
        String NOTIFICATION_TYPE = "Order Status Changed";

        //PREPARE JSON(what to send where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            ///WHAT TO SEND
            notificationBody.put("notificationtype", NOTIFICATION_TYPE);
            notificationBody.put("buyerid",
                    "H1XGfjOILTM8d97mICWHzbXyRKv1");
            notificationBody.put("sellerid", auth.getUid());
            notificationBody.put("orderid", orderId);//timestamp is the order id
            notificationBody.put("notificationtitle", NOTIFICATION_TITLE);
            notificationBody.put("notificationmessage", NOTIFICATION_MESSAGE);
            ///WHERE TO SEND
            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBody);



        }
        catch (Exception e) {
            Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        // timestamp is the order id
        sendFcmNotification(notificationJo);

    }

    private void sendFcmNotification(JSONObject notificationJo) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


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
        ///enque request
        Volley.newRequestQueue(this).add(request);


    }
    private void showMap() {
        String adresss="https://maps.google.com/map?saddr="+sellerlatitude+","+sellerlongitude+"&addr="+buyerLatitude+","+buyerLongitude;
       Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(adresss));
        startActivity(intent);
    }

    private void viewInitialize() {
        rcv=findViewById(R.id.single_cart_details);
        txtOrderid=findViewById(R.id.orderidd);
        back=findViewById(R.id.back);
        paymentMode=findViewById(R.id.payment_mode);
        customerAdress=findViewById(R.id.full_adress);
        customerName=findViewById(R.id.customer_name);
        number=findViewById(R.id.ph_no);
        orderstatus=findViewById(R.id.order_status);
        editOrder=findViewById(R.id.edit_order);
        map=findViewById(R.id.map);
    }
}