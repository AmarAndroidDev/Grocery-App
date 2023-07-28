package com.example.groceryappp.Activity.Activity;

import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.hideProgressDialog;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.showProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Receiver.InternetConnectivityReceiver;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserPlaceOrderActivity extends AppCompatActivity implements InternetConnectivityReceiver.ConnectivityListener {
    AppCompatButton btnUpdate, currentAddress;
    int finalMrp = 0;
    TextInputEditText nameDial, numberDial;
    String deliverySlot, currentDay,currentTime,       timestamp;
    LinearLayout layout;
    private InternetConnectivityReceiver connectivityReceiver;
    private FirebaseFirestore database;
    private String City, Street, Pin, Landmark, FloorNo, fullAd;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<SingleProductDetails> list;

    private AlertDialog.Builder alertDialog;
    private UserInfo header;
    private int finalsaved = 0, summ = 0;
    private ProgressDialog progressDialog;
    private String userName, number1, fullAdress, lattitude, longitude,currentDate,finalTim,userId;
    private RadioButton cashOnDelivery;
    private double lat, lng;
    private AppCompatButton placeOrder;
    private TextView totalPrice, fulladress, username, number, totalItem, totalSaved, total, totalMrp, deliveryFee, fulladdDial;
    private ImageView editAdress, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_place_order);
        viewInitialize();
        getSupportActionBar().hide();
        database = FirebaseFirestore.getInstance();
        userId= SharedPreferenceManager.getInstance(UserPlaceOrderActivity.this).getUserId();

        list = new ArrayList<>();
        list = (ArrayList<SingleProductDetails>) getIntent().getSerializableExtra("list");
        totalItem.setText(String.valueOf(list.size()) + "(Items)");
        for (int i = 0; i < list.size(); i++) {
            finalMrp = list.get(i).getMarktPrice() * list.get(i).getUnit() + finalMrp;
        }
        totalMrp.setText(String.valueOf("₹" + finalMrp));
        deliveryFee.setText("₹" + "10");
        ////
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ////
        ///getting Intent
        summ = Integer.parseInt(getIntent().getStringExtra("sum"));
        finalsaved = finalMrp - summ;
        totalPrice.setText(String.valueOf("₹" + summ));
        totalSaved.setText(String.valueOf("₹" + finalsaved));
        editAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());


        currentDay = dayFormat.format(calendar.getTime());
    currentTime = timeFormat.format(calendar.getTime());
        timestamp = "" + System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        long data = Long.parseLong(timestamp);
        currentDate=formatter.format(data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              findViewById(R.id.pgbarHome).setVisibility(View.GONE);
              placeOrder.setVisibility(View.VISIBLE);
            }
        },1000);
        ////fetching adress details
        database.collection("CurrentUser").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName = String.valueOf(documentSnapshot.get("name"));
                number1 = String.valueOf(documentSnapshot.get("number"));
                fullAdress = String.valueOf(documentSnapshot.get("fullAd"));
                lattitude = String.valueOf(documentSnapshot.get("latitude"));
                longitude = String.valueOf(documentSnapshot.get("longitude"));
                username.setText(userName);
                number.setText(number1);
                fulladress.setText(fullAdress);


            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (checkPaymentOption()) {
                    showDeliverySlotDialog();

                } else {
                    Toast.makeText(UserPlaceOrderActivity.this, "Please Select Payment Mode", Toast.LENGTH_SHORT).show();

                }

            }


        });
    }

    private void showDeliverySlotDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheetfororderplaced, null);
        BottomSheetDialog dialog1 = new BottomSheetDialog(this);
        dialog1.setContentView(view);

        dialog1.show();
        RadioButton rbSelfPick, rb1to5, rbTom, rbCustom;
        MaterialButton confirmOrder;
        rbSelfPick = view.findViewById(R.id.rbselfPick);
        rb1to5 = view.findViewById(R.id.rB1to5);
        rbTom = view.findViewById(R.id.rbTom);
        rbCustom = view.findViewById(R.id.rBtnCustom);
        confirmOrder = view.findViewById(R.id.confirmOrder);
        FirebaseClient.getInstance().collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if ((boolean) documentSnapshot.get("1To5hr")) {
                    rb1to5.setEnabled(true);
                    rb1to5.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                }
                if ((long) documentSnapshot.get("Customization") > 1) {
                    rbCustom.setVisibility(View.VISIBLE);
                    rbCustom.setText("Delivery in " + documentSnapshot.get("Customization") + "Hr");
                    rbCustom.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                }
                if ((boolean) documentSnapshot.get("SelfPickup")) {
                    rbSelfPick.setEnabled(true);
                    rbSelfPick.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                }
                if ((boolean) documentSnapshot.get("delvTomorrow")) {
                    rbTom.setEnabled(true);
                    rbTom.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                }
                if (rbCustom.isChecked()) {
                    deliverySlot = "Delivery in " + documentSnapshot.get("Customization") + "Hr";
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rb1to5.isChecked()) {
                    deliverySlot = "Delivery in 1-5hr";
                }
                if (rbSelfPick.isChecked()) {
                    deliverySlot = "Self pick up";
                }
                if (rbTom.isChecked()) {
                    deliverySlot = "Delivery Tomorrow";
                }

                if (rbCustom.isChecked() || rb1to5.isChecked() || rbSelfPick.isChecked() || rbTom.isChecked()) {
                    showAlertForPlaceOrder(deliverySlot);
                    dialog1.cancel();

                } else {
                    Toast.makeText(UserPlaceOrderActivity.this, "Please select Delivery slot", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }

    private void showAlertForPlaceOrder(String DeliverySlot) {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm Order").setMessage("Are you sure to place order ?").setIcon(R.drawable.baseline_add_shopping_cart_24).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                findViewById(R.id.orderPlacing).setVisibility(View.VISIBLE);
              placeOrder.setVisibility(View.GONE);
                //showProgressDialog(UserPlaceOrderActivity.this, "Order Placing....");

            /*    if (username.getText() != null && number.getText() != null && fulladress.getText() != null) {
                    header = new UserInfo(username.getText().toString(), timestamp, timestamp, number.getText().toString(), fulladress.getText().toString(), "InProgress", "", "",summ, DeliverySlot, auth.getUid(),currentDay,currentTime);
                } else {
                    header = new UserInfo(userName, timestamp, timestamp, number1, fulladress.getText().toString(), "InProgress", longitude, lattitude, summ, DeliverySlot, auth.getUid(),currentDay,currentTime);

                }*/


               finalTim=currentDay.substring(0,3)+currentDate+"("+currentTime+")";
             if (lat>0.0&&lng>0.0){
                 header = new UserInfo(username.getText().toString(), timestamp, timestamp, number.getText().toString(), fulladress.getText().toString(), "InProgress", ""+lng, ""+lat,summ, DeliverySlot,userId,""+finalsaved,finalTim,"","","");
             }
                header = new UserInfo(username.getText().toString(), timestamp, timestamp, number.getText().toString(), fulladress.getText().toString(), "InProgress", longitude, lattitude,summ, DeliverySlot,userId,""+finalsaved,finalTim,"","","");
///here i save order details to firebase
                database.collection("CurrentUser").document(userId).collection("orders").document(timestamp).set(header).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        for (int i = 0; i < list.size(); i++) {
                            String name = list.get(i).getName();
                            int price = list.get(i).getPrice();
                            int unit = list.get(i).getUnit();
                            String id = list.get(i).getId();
                            int totalprice = list.get(i).getTotalprice();
                            String img = list.get(i).getImgUri();
                            String qty = list.get(i).getQty();
                            SingleProductDetails details = new SingleProductDetails(name,qty,img,price,unit,totalprice);

                            ///here i set details of cart like how much qty,how much item in user
                            database.collection("CurrentUser").document(userId).collection("orders").document(timestamp).collection("items").document(id).set(details)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").collection("All orders").document(timestamp).set(header).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").collection("All orders").document(timestamp).collection("items").document(id).set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            ///here after saving the orders details clear the cart details
                                                            database.collection("CurrentUser").document(userId).collection("cart").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    findViewById(R.id.orderPlacing).setVisibility(View.GONE);
                                                                    Toast.makeText(UserPlaceOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            findViewById(R.id.orderPlacing).setVisibility(View.GONE);
                                                            Toast.makeText(UserPlaceOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });



                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    findViewById(R.id.orderPlacing).setVisibility(View.GONE);
                                                    Toast.makeText(UserPlaceOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            ///here save all order item into th seller db

                                            ///send the notification
                                            PreparedNotification(timestamp);
hideProgressDialog();

                                        }


                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            findViewById(R.id.orderPlacing).setVisibility(View.GONE);
                                            Toast.makeText(UserPlaceOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (cashOnDelivery.isChecked()) {
            return true;
        } else return false;
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
            notificationBody.put("buyerid", userId);
            notificationBody.put("sellerid", "NAPuTkYOldg4M8FHUUwKeNvkBK73");
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

                Intent intent = new Intent(UserPlaceOrderActivity.this, PlaceOrderCompleteCelebration.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("sum", summ);
                intent.putExtra("saved", finalsaved);
                intent.putExtra("ordertype", "placeOrder");
                intent.putExtra("orderTo", "NAPuTkYOldg4M8FHUUwKeNvkBK73");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if failed sending notification navigate to orderdetials activity

                Intent intent = new Intent(UserPlaceOrderActivity.this, PlaceOrderCompleteCelebration.class);
                intent.putExtra("ordrId", orderId);
                intent.putExtra("sum", summ);
                intent.putExtra("saved", finalsaved);
                intent.putExtra("orderTo", "NAPuTkYOldg4M8FHUUwKeNvkBK73");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
    

    private void showBottomSheetDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheetdeliverychange, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
        dialog.setCancelable(false);

        nameDial = view.findViewById(R.id.name);
        numberDial = view.findViewById(R.id.number);
        currentAddress = view.findViewById(R.id.current_Address);
        btnUpdate = view.findViewById(R.id.btn_adress_update);
        fulladdDial = view.findViewById(R.id.fullAddress);
        layout = view.findViewById(R.id.layout);
        ImageView cancel = view.findViewById(R.id.cancel);
        // Set your desired image to the ImageView
        // imageView.setImageResource(R.drawable.your_image);
        btnUpdate.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });
        currentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(UserPlaceOrderActivity.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            hideProgressDialog();
                            fetchLastLocation();

///first create user authentication in firebase,so that we can login next time

                        } else {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 101);
                            Toast.makeText(UserPlaceOrderActivity.this, "Please allow permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameDial.getText().toString().isEmpty()) {
                    nameDial.setError("Name Required");
                    return;
                }
                if (numberDial.getText().toString().isEmpty()) {
                    numberDial.setError("Number Required");

                    return;
                }
                if (numberDial.getText().length() != 10) {
                    numberDial.setError("Number Should 10 digit");
                    return;
                } else {
                    username.setText(nameDial.getText().toString());
                    number.setText(numberDial.getText());
                    fulladress.setText(fulladdDial.getText());
                    dialog.cancel();
                }


            }
        });


    }

    private void fetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            // Use the latitude and longitude values
                            // for your desired purpose
                            Geocoder geocoder = new Geocoder(UserPlaceOrderActivity.this, Locale.getDefault());
                            List<Address> list;
                            try {
                                list = geocoder.getFromLocation(lat, lng, 1);
                                ///GETTING ADRESS
                                City = list.get(0).getLocality();
                                Pin = list.get(0).getPostalCode();
                                Street = list.get(0).getThoroughfare() + "," + list.get(0).getSubLocality();
                                Landmark = list.get(0).getLocality();
                                FloorNo = list.get(0).getFeatureName();
                                fullAd = FloorNo + "," + Street + "," + Landmark + "," + City + "," + Pin;
                                fulladdDial.setText(fullAd);
                                btnUpdate.setEnabled(true);
                                currentAddress.setVisibility(View.GONE);
                                layout.setVisibility(View.VISIBLE);
                                int color = Color.parseColor("#19543E");
                                ColorStateList colorStateList = ColorStateList.valueOf(color);
                                btnUpdate.setBackgroundTintList(colorStateList);
                                hideProgressDialog();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                });
    }

    public void onResume() {
        super.onResume();
        connectivityReceiver = new InternetConnectivityReceiver(this);
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
    }

    @Override
    public void onInternetConnected() {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserPlaceOrderActivity.this, HomeActivity.class);
        intent.putExtra("WHO", "order");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}