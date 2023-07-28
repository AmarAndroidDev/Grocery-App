package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;


import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;


public class AdminEditShopDetails extends AppCompatActivity {
    private Switch minCartPrice, delvOption, delvCharge, specialAnnounce;
    private CheckBox selfPickup, delv1To5, delvTom;
    private TextInputEditText edtminCart, edtDeliveryCharge, edtHrCustom;
    private MaterialButton btnMinCart, btnDeliveryOpt, btnDelvCharge;
    private LinearLayout minCartPriceLayout, delvOptionLayout, delvChargeLayout, specialAnnounceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_shop_details);
        getSupportActionBar().hide();
        viewInitialize();
        switchOnClick();
        buttonOnClick();
        checkingPreviousDetails();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pgbarHome).setVisibility(View.GONE);
            }
        },1000);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void checkingPreviousDetails() {
        FirebaseClient.getInstance().collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long customHrValue = (long) documentSnapshot.get("Customization");
                long mincart = (long) documentSnapshot.get("MinCart");
                long delvcharge = (long) documentSnapshot.get("DeliveryCharge");
                boolean selfPick = (boolean) documentSnapshot.get("SelfPickup");
                boolean oneFiveHr = (boolean) documentSnapshot.get("1To5hr");
                boolean delvtom = (boolean) documentSnapshot.get("delvTomorrow");
                if (delvtom || selfPick || oneFiveHr) {
                    delvOption.setChecked(true);
                    if (selfPick) {
                        selfPickup.setChecked(true);
                    }
                    if (oneFiveHr) {
                        delv1To5.setChecked(true);
                    }
                    if (delvtom) {
                        delvTom.setChecked(true);
                    }
                } else {
                    return;
                }
                if (customHrValue > 1) {
                    edtHrCustom.setText(customHrValue + "Hr");
                }
                if (mincart > 1) {
                    edtminCart.setText("₹" + mincart);
                    minCartPrice.setChecked(true);
                }
                if (delvcharge > 1) {
                    edtDeliveryCharge.setText("₹" + delvcharge);
                    delvCharge.setChecked(true);
                }
                findViewById(R.id.pgbar).setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminEditShopDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void buttonOnClick() {
        btnMinCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtminCart.getText().toString().isEmpty()) {
                    saveToFirebase("MinCart", Integer.valueOf(edtminCart.getText().toString().replace("₹", "")));
                } else {
                    Toast.makeText(AdminEditShopDetails.this, "Please add Min Cart Price", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        btnDelvCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtDeliveryCharge.getText().toString().isEmpty()) {
                    saveToFirebase("DeliveryCharge", Integer.valueOf(edtDeliveryCharge.getText().toString().replace("₹", "")));
                } else {
                    Toast.makeText(AdminEditShopDetails.this, "Please delivery charge", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        btnDeliveryOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean selfPickupChecked = selfPickup.isChecked();
                boolean oneFiveChecked = selfPickup.isChecked();
                boolean tomorrowChecked = selfPickup.isChecked();
                int num = 1;
                if (edtHrCustom.getText() != null) {
                    num = Integer.parseInt(edtHrCustom.getText().toString().replace("Hr", ""));
                } else {
                    num = 1;
                }
                FirebaseClient.getInstance().collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73")
                        .update("SelfPickup", selfPickupChecked, "1To5hr", oneFiveChecked, "delvTomorrow", tomorrowChecked, "Customization", num).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AdminEditShopDetails.this, "sucess", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminEditShopDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void switchOnClick() {
        delvOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    delvOptionLayout.setVisibility(View.VISIBLE);
                    delvOption.setTrackDrawable(getDrawable(R.drawable.switch_track));
                } else {
                    edtHrCustom.setText("");
                    selfPickup.setChecked(false);
                    delv1To5.setChecked(false);
                    delvTom.setChecked(false);
                    delvOptionLayout.setVisibility(View.GONE);
                    delvOption.setTrackDrawable(getDrawable(R.drawable.switch_trackred));
                }
            }
        });
        minCartPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    minCartPriceLayout.setVisibility(View.VISIBLE);
                    minCartPrice.setTrackDrawable(getDrawable(R.drawable.switch_track));
                } else {
                    edtminCart.setText("");
                    minCartPriceLayout.setVisibility(View.GONE);
                    minCartPrice.setTrackDrawable(getDrawable(R.drawable.switch_trackred));
                }
            }
        });
        delvCharge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    delvCharge.setTrackDrawable(getDrawable(R.drawable.switch_track));
                    delvChargeLayout.setVisibility(View.VISIBLE);
                } else {
                    edtDeliveryCharge.setText("");
                    delvCharge.setTrackDrawable(getDrawable(R.drawable.switch_trackred));
                    delvChargeLayout.setVisibility(View.GONE);
                }

            }
        });

        specialAnnounce.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    specialAnnounce.setTrackDrawable(getDrawable(R.drawable.switch_track));
                } else {
                    specialAnnounce.setTrackDrawable(getDrawable(R.drawable.switch_trackred));

                }

            }
        });
    }

    private void viewInitialize() {
        minCartPrice = findViewById(R.id.minCartprice);
        delvOption = findViewById(R.id.deliveryOption);
        delvCharge = findViewById(R.id.deliveryCharge);
        specialAnnounce = findViewById(R.id.specialAnnounce);
        minCartPriceLayout = findViewById(R.id.minCartPriceLayout);
        delvOptionLayout = findViewById(R.id.delvOptionLayout);
        delvChargeLayout = findViewById(R.id.deliveryPriceLayout);
        selfPickup = findViewById(R.id.selfPick);
        delv1To5 = findViewById(R.id.delivery1to5);
        delvTom = findViewById(R.id.deliveryTom);

        edtminCart = findViewById(R.id.edtminCartPrice);
        edtDeliveryCharge = findViewById(R.id.edtdelvCharges);
        edtHrCustom = findViewById(R.id.edtDelvCustom);
        btnDelvCharge = findViewById(R.id.btnDelvCharge);
        btnDeliveryOpt = findViewById(R.id.btnDeliveryOption);
        btnMinCart = findViewById(R.id.btnMinCart);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdminEditShopDetails.this, AdminHomeActivity.class);
        intent.putExtra("WHO", "profile");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveToFirebase(String key, Integer value) {
        FirebaseClient.getInstance().collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73")
                .update(key, value).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AdminEditShopDetails.this, "sucess", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminEditShopDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}