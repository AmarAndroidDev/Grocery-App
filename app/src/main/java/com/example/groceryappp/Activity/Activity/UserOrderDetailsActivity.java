package com.example.groceryappp.Activity.Activity;

import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.hideProgressDialog;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.showProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class UserOrderDetailsActivity extends AppCompatActivity {
    int total;
    private FirebaseFirestore database;

    private CartAdapter adapter;

    private AlertDialog.Builder alertDialog;
    private ArrayList<SingleProductDetails> list;
    private UserInfo header;
    private AppCompatButton cancelOrder, editSellerOrder;
    private RecyclerView rcv;
    private CardView card, card2;
    private RelativeLayout orderRelay;
    private ImageView delete, back;
    private String orderStatus, orderId, date, checkingUserOrSeller, status,currentDay,currentTime,currentDate,userId;
    private TextView txtOrderid, paymentMode, customerName, number, customerAdress, totalAmount, txtDate,txtSaved;
////for delivery status maintain
    private ImageView tickInProgress,tickPacked,tickOutDelivery,tickDelivered;
    private CardView cardProgress,cardPacked,cardOutDelivery,cardDelivered;
    private TextView timProgress,timepPacked,timOutDelivery,timDelivered,txtOrderPack,txtOutForDelivery,txtDelivered,track1,track2,track3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_details);
        getSupportActionBar().hide();
        database = FirebaseFirestore.getInstance();
        userId= SharedPreferenceManager.getInstance(UserOrderDetailsActivity.this).getUserId();


        viewInitialize();
        header = (UserInfo) getIntent().getSerializableExtra("orderDetails");
        checkingUserOrSeller = getIntent().getStringExtra("checkingUserOrSeller");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        long data= Long.parseLong(header.getOrderId());
        currentDate=formatter.format(data);
        currentDay = dayFormat.format(calendar.getTime());
        currentTime = timeFormat.format(calendar.getTime());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pgbarHome).setVisibility(View.GONE);
            }
        },1000);

        if (header.getStatus().equalsIgnoreCase("order cancel")) {
            cancelOrder.setVisibility(View.GONE);
            editSellerOrder.setVisibility(View.GONE);

        }
        if (header.getStatus().equalsIgnoreCase("order delivered")) {
            cancelOrder.setVisibility(View.GONE);
            editSellerOrder.setVisibility(View.GONE);

        }
        if (checkingUserOrSeller.equals("All orders")) {
            editSellerOrder.setVisibility(View.VISIBLE);
            cancelOrder.setVisibility(View.GONE);
        } else {

            editSellerOrder.setVisibility(View.GONE);
            cancelOrder.setVisibility(View.VISIBLE);
        }
        orderId = header.getOrderId();
        total = header.getSum();
        date = header.getDate();
        totalAmount.setText("₹" + String.valueOf(total));
        txtDate.setText(date);
txtSaved.setText(header.getSaved());
timProgress.setText(header.getcTimePrgrs());

        txtOrderid.setText(orderId);
        paymentMode.setText("Cash On Delivery");
        list = new ArrayList<>();
        alertDialog = new AlertDialog.Builder(UserOrderDetailsActivity.this);


        //set up recylerview
        adapter = new CartAdapter(UserOrderDetailsActivity.this, list);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(adapter);
        editSellerOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSellerOrderStatusUpdate();
            }
        });
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("order cancel");
            }
        });
///fetching orders single item
        database.collection("CurrentUser").document(userId).collection(checkingUserOrSeller).document(orderId).collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    SingleProductDetails header = snapshot.toObject(SingleProductDetails.class);
                    list.add(header);

                    card.setVisibility(View.VISIBLE);
                    orderRelay.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }
        });
        ////fetching shipping details
        database.collection("CurrentUser").document(userId).collection(checkingUserOrSeller).document(orderId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserInfo info=documentSnapshot.toObject(UserInfo.class);


                status = info.getStatus();
                customerName.setText(info.getName());
                customerAdress.setText(info.getFullAd());
                number.setText(info.getNumber());

                if (status.equalsIgnoreCase("order packed")) {
                    tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                 track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));

                  cardPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
timepPacked.setText(info.getcTimePacked());
timepPacked.setVisibility(View.VISIBLE);
                    txtOrderPack.setTextColor(Color.parseColor("#19543E"));
                    findViewById(R.id.txt2).setVisibility(View.VISIBLE);

                }


                if (status.equalsIgnoreCase("out for delivery")) {
                   track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                 track2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                    tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                    txtOrderPack.setTextColor(Color.parseColor("#19543E"));
                    cardPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    timepPacked.setVisibility(View.VISIBLE);
                    timepPacked.setText(info.getcTimePacked());
                    findViewById(R.id.txt2).setVisibility(View.VISIBLE);
                    findViewById(R.id.txt3).setVisibility(View.VISIBLE);
                 tickOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
               cardOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    timOutDelivery.setVisibility(View.VISIBLE);
                    txtOutForDelivery.setTextColor(Color.parseColor("#19543E"));
                    timOutDelivery.setText(info.getcTimeOutDelivery());

                }
                if (status.equalsIgnoreCase("order delivered")) {
                    tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                    findViewById(R.id.txt2).setVisibility(View.VISIBLE);
                    findViewById(R.id.txt3).setVisibility(View.VISIBLE);
                    findViewById(R.id.txt5).setVisibility(View.VISIBLE);
                    cardPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    timepPacked.setText(info.getcTimePacked());
                    timepPacked.setVisibility(View.VISIBLE);
                    txtOrderPack.setTextColor(Color.parseColor("#19543E"));
                    track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                    track2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                    tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                    txtOrderPack.setTextColor(Color.parseColor("#19543E"));
                    cardPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));

                    tickOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    cardOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    timOutDelivery.setVisibility(View.VISIBLE);
                    txtOutForDelivery.setTextColor(Color.parseColor("#19543E"));
                    timOutDelivery.setText(info.getcTimeOutDelivery());

                    track3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));

                 tickDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                 cardDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                    txtDelivered.setTextColor(Color.parseColor("#19543E"));
                 timDelivered.setVisibility(View.VISIBLE);
                  timDelivered.setText(info.getcTimeDelivered());

                    cancelOrder.setVisibility(View.GONE);
                    editSellerOrder.setVisibility(View.GONE);
                }
                if (status.equalsIgnoreCase("order cancel")) {
              tickInProgress.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                 tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                  tickDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                 tickOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                 cardDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    findViewById(R.id.imgCancel).setBackground(getDrawable(R.drawable.baseline_cancel_24));
                    txtDelivered.setText("Canceled");
                    txtDelivered.setTextColor(Color.RED);
                    cancelOrder.setVisibility(View.GONE);
                   track1.setBackgroundColor(Color.RED);
                track2.setBackgroundColor(Color.RED);
                 track3.setBackgroundColor(Color.RED);
                    editSellerOrder.setVisibility(View.GONE);
                } else {

                    return;
                }
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
                        showProgressDialog(UserOrderDetailsActivity.this, "Deleting Order...");

                        if (checkingUserOrSeller.equals("All orders")) {
                            database.collection("CurrentUser").document(userId).collection("All orders").document(orderId).collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    //now 1st delete item data then  go for order delete
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                        database.collection("CurrentUser").document(userId).collection("All orders").document(orderId).collection("items").document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.collection("CurrentUser").document(userId).collection("All orders").document(orderId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        hideProgressDialog();
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

                        } else {
                            database.collection("CurrentUser").document(header.getUid()).collection("orders").document(orderId).collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    //now 1st delete item data then  go for order delete
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                        database.collection("CurrentUser").document(userId).collection("orders").document(orderId).collection("items").document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.collection("CurrentUser").document(userId).collection("orders").document(orderId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        hideProgressDialog();
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

    private void showSellerOrderStatusUpdate() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheetsellerstatusupdate, null);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        RadioButton orderPacked, orderDeliverd, orderCancel,outForDelivery;
        MaterialButton updateOrder;
        orderPacked = view.findViewById(R.id.orderPacked);
        orderDeliverd = view.findViewById(R.id.orderDeliver);
        orderCancel = view.findViewById(R.id.orderCancel);
        updateOrder = view.findViewById(R.id.updateOrder);
        outForDelivery = view.findViewById(R.id.outForDelivery);
        if (header.getStatus().equalsIgnoreCase("order packed")) {
            orderPacked.setEnabled(false);
            orderPacked.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            view.findViewById(R.id.tickOrderPack).setVisibility(View.VISIBLE);
            outForDelivery.setEnabled(true);
            orderDeliverd.setEnabled(false);

        }   if (header.getStatus().equalsIgnoreCase("out for delivery")) {
            orderPacked.setEnabled(false);
            orderPacked.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            view.findViewById(R.id.tickOrderPack).setVisibility(View.VISIBLE);
            outForDelivery.setEnabled(false);
            orderDeliverd.setEnabled(true);
            outForDelivery.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            view.findViewById(R.id.tickOutDelivery).setVisibility(View.VISIBLE);

        }
        if (header.getStatus().equalsIgnoreCase("order cancel")) {

        }else if  (header.getStatus().equalsIgnoreCase("inprogress")) {
            outForDelivery.setEnabled(false);
            orderDeliverd.setEnabled(false);
        }
        updateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusOrder = null;
                if (orderPacked.isChecked()) {
                    statusOrder = "Order Packed";
                }
                if (orderDeliverd.isChecked()) {
                    statusOrder = "Order Delivered";
                }
                if (orderCancel.isChecked()) {
                    statusOrder = "Order Cancel";
                }
                if (outForDelivery.isChecked()) {
                    statusOrder = "Out For Delivery";
                }

                showAlertDialog(statusOrder);
                bottomSheetDialog.cancel();
            }
        });


    }

    private void showAlertDialog(String orderStatus) {
        alertDialog.setTitle(orderStatus);
        alertDialog.setMessage("Are you sure ?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog(UserOrderDetailsActivity.this, "Updating order status..");
                if (orderStatus.equals("order cancel"))
                {
                    cancelOrderInDatabase("order cancel");
                }else {
                    updateUserOrderStatusDB(orderStatus);
                }


              /*  orderStatus.setText("Cancel");
                orderStatus.setTextColor(Color.RED);*/

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void cancelOrderInDatabase(String cancelStatus) {
        FirebaseClient.getInstance().collection("CurrentUser").document(header.getUid()).collection("orders").document(orderId).update("status", cancelStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //now update in seller DB
                FirebaseClient.getInstance().collection("CurrentUser").document(
                        "NAPuTkYOldg4M8FHUUwKeNvkBK73").collection("All orders").document(orderId).update("status", cancelStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        PreparedNotification(orderId);
                        hideProgressDialog();
                            tickInProgress.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            tickDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            tickOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            cardDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            findViewById(R.id.imgCancel).setBackground(getDrawable(R.drawable.baseline_cancel_24));
                            txtDelivered.setText("Canceled");
                            txtDelivered.setTextColor(Color.RED);
                            cancelOrder.setVisibility(View.GONE);
                            track1.setBackgroundColor(Color.RED);
                            track2.setBackgroundColor(Color.RED);
                            track3.setBackgroundColor(Color.RED);
                            editSellerOrder.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserOrderDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                });


            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    private void updateUserOrderStatusDB(String orderstatus) {
        String  Timekey = null;
        if (orderstatus.equalsIgnoreCase("order packed")) {
            Timekey="cTimePacked";
        }
        if (orderstatus.equalsIgnoreCase("out for delivery")) {
            Timekey="cTimeOutDelivery";
        }
        if (orderstatus.equalsIgnoreCase("order delivered")) {
            Timekey="cTimeDelivered";
        }
       String finalTim=currentDay.substring(0,3)+currentDate+"("+currentTime+")";

        String finalTimekey = Timekey;
        FirebaseClient.getInstance().collection("CurrentUser").document(header.getUid()).collection("orders").document(orderId).update("status", orderstatus, finalTimekey,finalTim).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //now update in seller DB
                FirebaseClient.getInstance().collection("CurrentUser").document(
                        "NAPuTkYOldg4M8FHUUwKeNvkBK73").collection("All orders").document(orderId).update("status", orderstatus , finalTimekey,finalTim).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        PreparedNotification(orderId);
                        hideProgressDialog();
                        if (orderstatus.equalsIgnoreCase("order packed")) {
                            tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                            txtOrderPack.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            cardPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            timepPacked.setText(finalTim);
                            timepPacked.setVisibility(View.VISIBLE);

                        }


                        if (orderstatus.equalsIgnoreCase("out for delivery")) {
                            track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                            track2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                            tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                            txtOrderPack.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            cardPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            timepPacked.setText(finalTim);
                            timepPacked.setVisibility(View.VISIBLE);

                            tickOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            cardOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            txtOutForDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            timOutDelivery.setText(finalTim);
                            timOutDelivery.setVisibility(View.VISIBLE);
                        }
                        if (orderstatus.equalsIgnoreCase("order delivered")) {
                            track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                            track2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                            tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            track1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009E60")));
                            txtOrderPack.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            cardPacked.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            timepPacked.setText(finalTim);
                            timepPacked.setVisibility(View.VISIBLE);

                            tickOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            cardOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            txtOutForDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            timOutDelivery.setText(finalTim);
                            timOutDelivery.setVisibility(View.VISIBLE);
                            tickDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            cardDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            txtDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#19543E")));
                            timDelivered.setText(finalTim);
                            timDelivered.setVisibility(View.VISIBLE);

                            cancelOrder.setVisibility(View.GONE);
                            editSellerOrder.setVisibility(View.GONE);
                        }
                        if (orderstatus.equalsIgnoreCase("order cancel")) {
                            tickInProgress.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            tickPacked.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            tickDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            tickOutDelivery.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            cardDelivered.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            findViewById(R.id.imgCancel).setBackground(getDrawable(R.drawable.baseline_cancel_24));
                            txtDelivered.setText("Canceled");
                            txtDelivered.setTextColor(Color.RED);
                            cancelOrder.setVisibility(View.GONE);
                            track1.setBackgroundColor(Color.RED);
                            track2.setBackgroundColor(Color.RED);
                            track3.setBackgroundColor(Color.RED);
                            editSellerOrder.setVisibility(View.GONE);
                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserOrderDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
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
        if (checkingUserOrSeller.equals("All orders")) {
            Intent intent = new Intent(UserOrderDetailsActivity.this, AdminHomeActivity.class);
            intent.putExtra("WHO", "admin_order");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(UserOrderDetailsActivity.this, UserOrderHeaderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

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
            notificationBody.put("buyerid",userId);
            notificationBody.put("sellerid",
                    "NAPuTkYOldg4M8FHUUwKeNvkBK73");
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
                        "NAPuTkYOldg4M8FHUUwKeNvkBK73");
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
        editSellerOrder = findViewById(R.id.editSellerOrder);

        totalAmount = findViewById(R.id.total_amount);
        txtDate = findViewById(R.id.date);
        txtSaved = findViewById(R.id.totalSaved);


        /////for delivery status maintain
        tickInProgress=findViewById(R.id.tickInProgress);
                tickPacked=findViewById(R.id.tickorderpacked);
                tickOutDelivery=findViewById(R.id.tickOutDelivered);
                tickDelivered=findViewById(R.id.tickDelivered);;
        cardProgress=findViewById(R.id.cardInProgress);
        cardPacked=findViewById(R.id.cardOrderPack);
        cardOutDelivery=findViewById(R.id.cardOutForDelivery);
        cardDelivered=findViewById(R.id.cardDelivered);;
        timProgress=findViewById(R.id.timeinprogress);
        timepPacked=findViewById(R.id.timeOrderPacked);
        timOutDelivery=findViewById(R.id.timeOutForDelivery);
        timDelivered=findViewById(R.id.timeDelivered);;
        txtOutForDelivery=findViewById(R.id.txtoutForDelivery);
        txtDelivered=findViewById(R.id.txtDelivered);
        txtOrderPack=findViewById(R.id.orderpack);
        track1=findViewById(R.id.txtTrack1);
        track2=findViewById(R.id.txtTrack2);
        track3=findViewById(R.id.txtTrack3);


    }
}