package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.groceryappp.Activity.AllModel.AdressModel;
import com.example.groceryappp.Activity.AllModel.SingleItemDetails;
import com.example.groceryappp.Activity.AllModel.SingleOrderDetails;
import com.example.groceryappp.Activity.AllModel.UserOrderHeader;
import com.example.groceryappp.Activity.Fragment.CartFragment;
import com.example.groceryappp.Activity.HomeActivity;
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

public class UserPlaceOrderActivity extends AppCompatActivity {
    FirebaseFirestore database;
    ArrayList<SingleOrderDetails> list;
    private RadioButton cashOnDelivery;
    FirebaseAuth auth;
  ProgressDialog dialog;
  AlertDialog.Builder alertDialog;
    int finalsaved;
    int finalMrp=0;
    int tempmrp=0;
    String fullAdress;
    TextView txtresponse;
    ProgressDialog progressDialog;
    String userName, number1;
    int summ;
    String sellerId,lattitude,longitude;
    private AppCompatButton placeOrder;
    private TextView totalPrice, fulladress, username, number,totalItem,totalSaved,total,totalMrp,deliveryFee;
    private AdressModel model;
    private ImageView editAdress,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_place_order);
        viewInitialize();
        getSupportActionBar().hide();
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();
        list = (ArrayList<SingleOrderDetails>) getIntent().getSerializableExtra("list");

        totalItem.setText(String.valueOf(list.size())+"(Items)");
        for (int i = 0; i < list.size(); i++) {


             finalMrp= list.get(i).getMarktPrice()+finalMrp;





        }

        totalMrp.setText(String.valueOf("₹"+finalMrp));
        deliveryFee.setText("₹"+"10");


        ///getting Intent
       summ= Integer.parseInt(getIntent().getStringExtra("sum"));
        finalsaved=finalMrp-summ;
        totalPrice.setText(String.valueOf("₹"+summ));

        totalSaved.setText(String.valueOf("₹"+finalsaved));
        editAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPlaceOrderActivity.this, EditDeliveryDetailActivity.class);

                startActivityForResult(intent, 1);
            }
        });
back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
finish();

    }
});
        fetchingDialog();
        ////fetching adress details
        database.collection("CurrentUser").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName = String.valueOf(documentSnapshot.get("username"));
                number1 = String.valueOf(documentSnapshot.get("number"));

                fullAdress = String.valueOf(documentSnapshot.get("fulladress"));
                lattitude = String.valueOf(documentSnapshot.get("latitude"));
                longitude = String.valueOf(documentSnapshot.get("longitude"));


                username.setText(userName);
                number.setText(number1);
                fulladress.setText(fullAdress);
progressDialog.dismiss();

            }
        });

        ////getting selller id for save order to seller database
        database.collection("CurrentUser").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    if (snapshot.get("acounttype").equals("Seller")) {
                        sellerId = String.valueOf(snapshot.get("uid"));
                    }
                }
            }
        });


        placeOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (checkPaymentOption()){
                    showAlertForPlaceOrder();
                }
           else{
                    Toast.makeText(UserPlaceOrderActivity.this, "Please Select Payment Mode", Toast.LENGTH_SHORT).show();

                }

            }



        });
    }

    private void showAlertForPlaceOrder() {
        alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm Order").setMessage("Are you sure to place order ?").setIcon(R.drawable.confirm_order).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                    dialog = new ProgressDialog(UserPlaceOrderActivity.this);
                    dialog.setCancelable(false);
                    dialog.setTitle("Order Placing");
                    dialog.setIcon(R.drawable.order_placing);
                    dialog.setMessage("Please wait a moment....");
                    dialog.show();
                    String timestamp = "" + System.currentTimeMillis();

                    UserOrderHeader header=new UserOrderHeader(userName,timestamp,timestamp,number1,fullAdress,"InProgress",longitude,lattitude,summ);


///here i save order details to firebase
                    database.collection("CurrentUser").document(auth.getUid()).collection("orders").document(timestamp).set(header).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            for (int i = 0; i < list.size(); i++) {
                                String name = list.get(i).getName();
                                int price = list.get(i).getPrice();
                                int unit = list.get(i).getUnit();
                                String id = list.get(i).getIdd();
                                int totalprice = list.get(i).getTotalprice();
                                String img = list.get(i).getImgUrl();
                                String qty = list.get(i).getQty();

                                SingleItemDetails details = new SingleItemDetails(name, price, qty, totalprice, img, unit);
                                ///here i set details of cart like how much qty,how much item
                                database.collection("CurrentUser").document(auth.getUid()).collection("orders").document(timestamp).collection("items").document(id).set(details)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //here save the orderheader into sellr db
                                                UserOrderHeader header = new UserOrderHeader(name, summ, timestamp, timestamp, number1);
                                                header.setAdress(fullAdress);
                                                header.setStatus("InProgress");
                                                header.setUid(auth.getUid());
                                                header.setLattitude(lattitude);
                                                header.setLongitude(longitude);
                                                database.collection("CurrentUser").document(sellerId).collection("All orders").document(timestamp).set(header).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });

                                                ///here save all order item into th seller db
                                                database.collection("CurrentUser").document(sellerId).collection("All orders").document(timestamp).collection("items").document(id).set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });


                                                ///here after saving the orders details clear the cart details
                                                database.collection("CurrentUser").document(auth.getUid()).collection("cart").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                      /*  Toast.makeText(UserPlaceOrderActivity.this, "Successfully Order Placed", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(UserPlaceOrderActivity.this, UserOrderHeaderActivity.class);
                                                        intent.putExtra("orderId", timestamp);
                                                        intent.putExtra("sum", summ);
                                                        startActivity(intent);
                                                         finish();
*/
                                                    }
                                                });
///send the notification
                                                PreparedNotification(timestamp);


                                            }


                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });


                            }


                        }
                    });


                }



        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private boolean checkPaymentOption() {
        if (cashOnDelivery.isChecked()){
            return true;
        }else return false;
    }

    private void PreparedNotification(String orderId) {
        ////when user place order ,send notification to seller


        ///prepare data for nootificartion
        final String FCM_TOPIC = "PUSH_NOTIFICATIONS";
        String NOTIFICATION_TOPIC = "/topics/" + FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order" + orderId;///timestamp is the order id
        String NOTIFICATION_MESSAGE = "Congratulation...!You Have A New Order.";
        String NOTIFICATION_TYPE = "New Order";

        //PREPARE JSON(what to send where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            ///WHAT TO SEND
            notificationBody.put("notificationtype", NOTIFICATION_TYPE);
            notificationBody.put("buyerid", auth.getUid());
            notificationBody.put("sellerid", "ODR0CUvoQ5ecbEaU2ZWVENCLGEt2");
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
        sendFcmNotification(notificationJo, orderId);

    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//after sending notification navigate to orderdetials activity
                dialog.dismiss();
                Intent intent = new Intent(UserPlaceOrderActivity.this, PlaceOrderCompleteCelebration.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("sum", summ);
                intent.putExtra("ordertype", "placeOrder");
                intent.putExtra("orderTo", "ODR0CUvoQ5ecbEaU2ZWVENCLGEt2");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if failed sending notification navigate to orderdetials activity
                dialog.dismiss();
                Intent intent = new Intent(UserPlaceOrderActivity.this, PlaceOrderCompleteCelebration.class);
                intent.putExtra("ordrId", orderId);
                intent.putExtra("sum", summ);
                intent.putExtra("orderTo", "ODR0CUvoQ5ecbEaU2ZWVENCLGEt2");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);


                startActivity(intent);
                finish();



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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

            }


        }
    }


    private void viewInitialize() {
        totalItem = findViewById(R.id.totalitem);
        placeOrder = findViewById(R.id.placeOrder);
        totalMrp = findViewById(R.id.total_mrp);
        deliveryFee = findViewById(R.id.deliveryfee);
        totalSaved = findViewById(R.id.totalsaved);
        totalPrice = findViewById(R.id.totalprice);
        fulladress = findViewById(R.id.full_adress);
        username = findViewById(R.id.customer_name);
        number = findViewById(R.id.ph_no);

        back = findViewById(R.id.back);
        cashOnDelivery = findViewById(R.id.cash_on_delivery);

        editAdress = findViewById(R.id.edit_adress);
    }
    private void fetchingDialog() {
        progressDialog = new ProgressDialog((UserPlaceOrderActivity.this));
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}