package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DeviceProductInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.AllModel.AdressModel;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdressActivity extends AppCompatActivity implements LocationListener {
    private TextInputEditText locality, pin, city, apartment, fulladress, landmark;
    private LinearLayout edtManualy, curentLocation;

    private AppCompatButton add, edit, update;
    private TextInputLayout fullAdLay;
    ProgressDialog dialog2;
    String Fulladd;
    private CardView edCardView;
    private double lattitude, longitude;
    private ProgressDialog dialog;
    private ImageView back;
    private String[] loocationPermission;
    private String City, Locality, Pin, Street, Apartment, FulAd;
    FirebaseFirestore database;
    private LocationManager locationManager;
    private final int REQUEST_PERMISSION = 1;
    FirebaseAuth auth;
    AdressModel model1;
    String addd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress);

        getSupportActionBar().hide();
        viewInitialize();
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        addPreviousAdress();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ///permission for location
        loocationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        curentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2 = new ProgressDialog(AdressActivity.this);
                dialog2.setIcon(R.drawable.baseline_location_on_24);
                dialog2.setTitle("Fetching Location");
                dialog2.setMessage("Please Wait...");
                dialog2.setCancelable(false);
                dialog2.show();
                fulladress.setVisibility(View.VISIBLE);
                if (checkLocationPermission()) {

                    ///location alowwed
                    detectLocatoin();
                } else {
                    //request location permission
                    requestPermission();
                }
            }
        });
     /*   ////fetching adress details
        database.collection("User").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String Pinn= String.valueOf(documentSnapshot.get("pincode"));
                String Cityyy= String.valueOf(documentSnapshot.get("city"));
                String Streett= String.valueOf(documentSnapshot.get("street"));
                String Fullll= String.valueOf(documentSnapshot.get("fulladress"));
                String Apart= String.valueOf(documentSnapshot.get("apartment"));
                String Local= String.valueOf(documentSnapshot.get("locality"));
                pin.setText(Pinn);
                city.setText(Cityyy);
                street.setText(Streett);
                fulladress.setText(Fullll);
                apartment.setText(Apart);
                locality.setText(Local);



            }
        });

*/
        edtManualy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fulladress.getText().toString().isEmpty()){
                    fullAdLay.setVisibility(View.GONE);
                }else {
                    fullAdLay.setVisibility(View.GONE);
                }

                locality.setEnabled(true);
                pin.setEnabled(true);
                city.setEnabled(true);
                apartment.setEnabled(true);
                landmark.setEnabled(true);
                fulladress.setText("");


            }
        });

        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (checkValidation()) {
                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(AdressActivity.this);
                    dialog1.setIcon(R.drawable.baseline_location_on_24);
                    dialog1.setTitle("Update Address").setMessage("Are You Sure ?").setCancelable(false);
                    dialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateAdress();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                } else {
                    return;
                }









      /*  Intent intent=new Intent(AdressActivity.this,UserPlaceOrderActivity.class);
        intent.putExtra("apart",model1.getApartment());
        intent.putExtra("city",model1.getCity());
        intent.putExtra("street",model1.getStreet());
        intent.putExtra("local",model1.getLocality());
        intent.putExtra("pin",model1.getPin());
        setResult(RESULT_OK,intent);*/
            }

        });
        pin.setText(Pin);
        city.setText(City);
        locality.setText(Locality);
    }

    private boolean checkValidation() {
        if (apartment.getText().toString().isEmpty()) {


            Toast.makeText(this, "Please provide Apartment/House", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (locality.getText().toString().isEmpty()) {


            Toast.makeText(this, "Please provide Locality", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (landmark.getText().toString().isEmpty()) {


            Toast.makeText(this, "Please provide Landmark", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (city.getText().toString().isEmpty()) {

            Toast.makeText(this, "Please provide City", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pin.getText().toString().isEmpty()) {

            Toast.makeText(this, "Please provide Pincode", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateAdress() {
        dialog = new ProgressDialog(AdressActivity.this);
        dialog.setTitle("Updating Adress");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();
        String Locality = locality.getText().toString();
        String Pin = pin.getText().toString();
        String City = city.getText().toString();
        String Apartment = apartment.getText().toString();

        if (fulladress.getText().toString() != null) {
            Fulladd = fulladress.getText().toString();
        } else {
            Fulladd = "";
        }

        Map<String, String> map = new HashMap<>();


        //  AdressModel model=new AdressModel(Locality,Pin,Apartment,City,Street);
        AdressModel model = new AdressModel(Locality, Pin, City, "", Fulladd, String.valueOf(lattitude), "" + longitude, Apartment);
        database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).update("locality", Locality, "pincode", Pin, "apartment", Apartment, "city", City, "fulladress", Fulladd, "latitude", String.valueOf(lattitude), "longitude", String.valueOf(longitude), "landMark", landmark.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdressActivity.this, "updated sucessfuly", Toast.LENGTH_SHORT).show();

                dialog.cancel();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdressActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPreviousAdress() {
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> list;
        try {

            list = geocoder.getFromLocation(latitude, longitude, 1);
            ///GETTING ADRESS
            City = list.get(0).getLocality();
            Pin = list.get(0).getPostalCode();
            Locality = list.get(0).getSubLocality();
            String fulladres = list.get(0).getAddressLine(0);

            //set adress to EditText
            pin.setText(Pin);
            city.setText(City);
            locality.setText(Locality);

            fulladress.setText(fulladres);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AdressActivity.this, loocationPermission, REQUEST_PERMISSION);
    }

    private boolean checkLocationPermission() {

        boolean result = ContextCompat.checkSelfPermission(AdressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private void detectLocatoin() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkLocationPermission();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    private void viewInitialize() {
        locality=findViewById(R.id.locality);
        landmark=findViewById(R.id.landmark);
        pin=findViewById(R.id.pin);
        city=findViewById(R.id.city);
        apartment=findViewById(R.id.apart_house);
        fulladress=findViewById(R.id.full_adress);
        back=findViewById(R.id.back);
        fullAdLay=findViewById(R.id.fullAdLayout);

        curentLocation=findViewById(R.id.currentLoc);

        update=findViewById(R.id.btn_adress_update);

        edtManualy=findViewById(R.id.edtManualy);
        edCardView=findViewById(R.id.cardView2);


    }





    @Override
    public void onLocationChanged(@NonNull Location location) {
        ///location detected
        lattitude = location.getLatitude();
        longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(this,Locale.getDefault());
        List<Address> list;
        try {
            list = geocoder.getFromLocation(lattitude, longitude, 1);
            ///GETTING ADRESS

            City = list.get(0).getLocality();
            Pin = list.get(0).getPostalCode();
            Locality = list.get(0).getSubLocality();
            Street= String.valueOf(list.get(0).getThoroughfare());
            FulAd=list.get(0).getAddressLine(0);


            //set adress to textview

            locality.setEnabled(true);
            pin.setEnabled(true);
            city.setEnabled(true);
            landmark.setEnabled(true);
            apartment.setEnabled(true);
            fulladress.setText(FulAd);
dialog2.dismiss();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}