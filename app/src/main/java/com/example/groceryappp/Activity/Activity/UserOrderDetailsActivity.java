package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.groceryappp.Activity.Adapter.CartAdapter;
import com.example.groceryappp.Activity.Adapter.UserOrderHeaderAdapter;
import com.example.groceryappp.Activity.AllModel.SingleOrderDetails;
import com.example.groceryappp.Activity.AllModel.UserOrderHeader;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserOrderDetailsActivity extends AppCompatActivity {
    FirebaseFirestore database;
    FirebaseAuth auth;
    CartAdapter adapter;
    AlertDialog.Builder alertDialog;
    ArrayList<SingleOrderDetails> list;
    int total;
    private AppCompatButton cancelOrder;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;
    private RecyclerView rcv;
    private CardView card, card2;
    private RelativeLayout orderRelay;
    private ImageView delete, back;

    private String orderStatusIntent, orderId, date;
    private TextView txtOrderid, paymentMode, customerName, number, customerAdress, orderStatus, totalAmount, txtDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_details);
        getSupportActionBar().hide();


        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        viewInitialize();
        fetchingDialog();
        orderId = getIntent().getStringExtra("orderId");
        orderStatusIntent = getIntent().getStringExtra("order status");
        total = getIntent().getIntExtra("sum", 1);
        date = getIntent().getStringExtra("date");
        totalAmount.setText("₹" + String.valueOf(total));
        txtDate.setText(date);
        if (orderStatusIntent.equalsIgnoreCase("Processing")) {
            orderStatus.setText(orderStatusIntent);
            orderStatus.setTextColor(Color.BLUE);

        } else if (orderStatusIntent.equalsIgnoreCase("Completed")) {
            orderStatus.setText(orderStatusIntent);
            orderStatus.setTextColor(Color.GREEN);

        } else if (orderStatusIntent.equalsIgnoreCase("Cancel")) {
            orderStatus.setText(orderStatusIntent);
            orderStatus.setTextColor(Color.RED);
            cancelOrder.setVisibility(View.GONE);

        }
        orderStatus.setText(orderStatusIntent);
        txtOrderid.setText(orderId);
        paymentMode.setText("Cash On Delivery");
        list = new ArrayList<>();
        alertDialog = new AlertDialog.Builder(UserOrderDetailsActivity.this);


        //set up recylerview
        adapter = new CartAdapter(UserOrderDetailsActivity.this, list);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(adapter);

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();


            }
        });
        ////geting order details
      /*  dialog = new Dialog(UserOrderDetailsActivity.this);
        dialog.setContentView(R.layout.google_pg_layout);
        dialog.setCancelable(false);
        dialog.setTitle("Fetching detail");
        dialog.show();
*/
///fetching orders single item
        database.collection("CurrentUser").document(auth.getUid()).collection("orders").document(orderId).collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    SingleOrderDetails header = snapshot.toObject(SingleOrderDetails.class);
                    list.add(header);

                    card.setVisibility(View.VISIBLE);
                    orderRelay.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        ////fetching shipping details
        database.collection("CurrentUser").document(auth.getUid()).collection("orders").document(orderId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String adress = String.valueOf(documentSnapshot.get("adress"));
                String name = String.valueOf(documentSnapshot.get("name"));
                String Number = String.valueOf(documentSnapshot.get("number"));
                customerName.setText(name);
                customerAdress.setText(adress);
                number.setText(Number);
                progressDialog.dismiss();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();

            }
        });
        ////delete the product
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserOrderDetailsActivity.this);
                dialog.setTitle("Delete Order").setIcon(R.drawable.baseline_delete_24);
                dialog.setMessage("Are you sure want to delete ?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        progressDialog2 = new ProgressDialog(UserOrderDetailsActivity.this);
                        progressDialog2.setMessage("Deleting Order...");
                        progressDialog2.setCancelable(false);
                        progressDialog2.show();
                        database.collection("CurrentUser").document(auth.getUid()).collection("orders").document(orderId).collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                //now 1st delete item data then  go for order delete
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    database.collection("CurrentUser").document(auth.getUid()).collection("orders").document(orderId).collection("items").document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.collection("CurrentUser").document(auth.getUid()).collection("orders").document(orderId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog2.dismiss();
                                                    adapter.notifyDataSetChanged();
                                                    finish();
                                                    startActivity(new Intent(UserOrderDetailsActivity.this, UserOrderHeaderActivity.class));

                                                }
                                            });
                                        }
                                    });
                                }


                            }
                        });

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();


            }
        });

    }

    private void showAlertDialog() {
        alertDialog.setTitle("Cancel Order");
        alertDialog.setMessage("Are you sure want to cancel ?");
        alertDialog.setIcon(R.drawable.baseline_delete_24);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateUserOrderStatusDB();


                orderStatus.setText("Cancel");
                orderStatus.setTextColor(Color.RED);
                cancelOrder.setVisibility(View.GONE);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void updateUserOrderStatusDB() {
        database.collection("CurrentUser").document(auth.getUid()).collection("orders").document(orderId).update("status", "Cancel").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //now update in seller DB
                database.collection("CurrentUser").document(
                        "YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All orders").document(orderId).update("status", "Cancel").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        PreparedNotification(orderId);
                        Toast.makeText(UserOrderDetailsActivity.this, "Order Canceled", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(UserOrderDetailsActivity.this, UserOrderHeaderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void PreparedNotification(String orderId) {
        ////when user place order ,send notification to seller


        ///prepare data for nootificartion
        final String FCM_TOPIC = "PUSH_NOTIFICATIONS";
        String NOTIFICATION_TOPIC = "/topics/" + FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order No." + orderId;///timestamp is the order id
        String NOTIFICATION_MESSAGE = "Cancelled";
        String NOTIFICATION_TYPE = "Cancel Order";

        //PREPARE JSON(what to send where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            ///WHAT TO SEND
            notificationBody.put("notificationtype", NOTIFICATION_TYPE);
            notificationBody.put("buyerid", auth.getUid());
            notificationBody.put("sellerid",
                    "YC7vLsrOpiVkMBqOcseWHL1BLTH3");
            notificationBody.put("orderid", orderId);//timestamp is the order id
            notificationBody.put("notificationtitle", NOTIFICATION_TITLE);
            notificationBody.put("notificationmessage", NOTIFICATION_MESSAGE);
            ///WHERE TO SEND
            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBody);


        } catch (Exception e) {
            Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        // timestamp is the order id
        sendFcmNotification(notificationJo, orderId);

    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//after sending notification navigate to orderdetials activity

               /* Intent intent = new Intent(UserOrderDetailsActivity.this, UserOrderHeaderActivity.class);
                intent.putExtra("ordrId", orderId);
                intent.putExtra("ordertype", "placeOrder");
                intent.putExtra("orderTo",
                        "YC7vLsrOpiVkMBqOcseWHL1BLTH3");
                finish();
                startActivity(intent);*/


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if failed sending notification navigate to orderdetials activity

            /*    Intent intent = new Intent(UserOrderDetailsActivity.this, UserOrderHeaderActivity.class);
                intent.putExtra("ordrId", orderId);
                intent.putExtra("orderTo", "FC8zN067TtZDFi6rwi8QRX9PgN52");
                finish();
                startActivity(intent);
*/


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

    private void fetchingDialog() {
        progressDialog = new ProgressDialog(UserOrderDetailsActivity.this);
        progressDialog.setMessage("Loading  Items....");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void viewInitialize() {
        rcv = findViewById(R.id.single_cart_details);
        txtOrderid = findViewById(R.id.orderidd);
        paymentMode = findViewById(R.id.payment_mode);
        customerAdress = findViewById(R.id.full_adress);
        customerName = findViewById(R.id.customer_name);
        number = findViewById(R.id.ph_no);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back);
        card = findViewById(R.id.card);
        orderRelay = findViewById(R.id.relative);
        card2 = findViewById(R.id.cardView3);
        cancelOrder = findViewById(R.id.cancel);
        orderStatus = findViewById(R.id.orderStatus);
        totalAmount = findViewById(R.id.total_amount);
        txtDate = findViewById(R.id.date);
    }
}