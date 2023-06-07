package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.AllModel.ModelRegister;
import com.example.groceryappp.Activity.HomeActivity;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminSignUpActivity extends AppCompatActivity implements LocationListener{
    private AppCompatButton signUp;
    private CircleImageView profile;
    private EditText city, pincode;
    private ImageView gps,back;
    private TextView login,fullAdress;
    ProgressDialog dialog2;
    private Uri imageUri;
    public static final int REQUEST_PERMISSION=2;
    String Fulladress, City, Locality, Pin;
    private double lattitude, longitude;
    private TextInputEditText name, number, email, password, confirmPasd,securityPin;
    private ProgressDialog dialog;
    FirebaseAuth auth;
    private String[] loocationPermission;
    private FirebaseFirestore database;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        getSupportActionBar().hide();

        viewInitialize();
        ///permission for location
        loocationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              boolean result=  validate();
              if (result){
                  registerUser();
              }else return;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog2 = new ProgressDialog(AdminSignUpActivity.this);
                dialog2.setTitle("Fetching Location");
                dialog2.setMessage("Please Wait...");
                dialog2.setCancelable(false);
                dialog2.show();
                if (checkLocationPermission()) {

                    ///location alowwed
                    detectLocatoin();
                } else {
                    //request location permission
                    requestPermission();
                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 8);
            }
        });

    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
///location detected
        lattitude = location.getLatitude();
        longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> list;
        try {
            list = geocoder.getFromLocation(lattitude, longitude, 1);
            ///GETTING ADRESS
            Fulladress = list.get(0).getAddressLine(0);
            City = list.get(0).getLocality();
            Pin = list.get(0).getPostalCode();
            Locality = list.get(0).getSubLocality();

            //set adress to textview
            pincode.setText(Pin);
            city.setText(City);
            fullAdress.setText(Fulladress + "" + Locality);
            dialog2.dismiss();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show();
    }

    private boolean validate() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Creating Account");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();
        if (securityPin.getText().toString().trim().isEmpty()&& name.getText().toString().trim().isEmpty() && number.getText().toString().trim().isEmpty() && password.getText().toString().trim().isEmpty() && confirmPasd.getText().toString().trim().isEmpty() && email.getText().toString().trim().isEmpty()) {
            dialog.dismiss();
            Toast.makeText(AdminSignUpActivity.this, "Please Provide All Details", Toast.LENGTH_SHORT).show();
      return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            dialog.dismiss();
            email.requestFocus();
            Toast.makeText(AdminSignUpActivity.this, "Incorrect email address", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (number.getText().toString().trim().length() != 10) {
            dialog.dismiss();
            number.requestFocus();
            Toast.makeText(AdminSignUpActivity.this, " Number should be 10 digit ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.getText().toString().trim().length() < 4 && name.getText().toString().trim().length() > 16) {
            dialog.dismiss();
            name.requestFocus();
            Toast.makeText(AdminSignUpActivity.this, " Name should be 16 digit ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.getText().toString().trim().length() < 4 && password.getText().toString().trim().length() > 10) {
            dialog.dismiss();
            password.requestFocus();
            Toast.makeText(AdminSignUpActivity.this, " password should be 4 to 10 digit ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (longitude==0.0 || lattitude==0.0) {
            dialog.dismiss();
            Toast.makeText(this, "Click gps button to detect location", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (!password.getText().toString().trim().equals(confirmPasd.getText().toString().trim())) {
            dialog.dismiss();
            Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show();

            return false;
        }
if (confirmPasd.getText().toString().trim().isEmpty()){
    dialog.dismiss();
    Toast.makeText(this, "Type Confirm Password", Toast.LENGTH_SHORT).show();
    return false;
}
        if (securityPin.getText().toString().isEmpty()){
            dialog.dismiss();
            Toast.makeText(this, "Type security pin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!securityPin.getText().toString().trim().equals("MANA")){
            dialog.dismiss();
            Toast.makeText(this, "Wrong security pin", Toast.LENGTH_SHORT).show();
            return false;
        }
         return true;


    }

    private void registerUser() {
        String emaill = email.getText().toString().trim();
        String paswordd = password.getText().toString().trim();
        String userName = name.getText().toString().trim();
        String numberr = number.getText().toString().trim();
///firset create user authentication in firebase,so that we can login next time
        auth.createUserWithEmailAndPassword(emaill, paswordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                            ///account created
                                                                                            //now save to firebase


                                                                                            if (task.isSuccessful()) {
                                                                                                saveToFirebase(emaill,paswordd, numberr,userName);

                                                                                            } else {
                                                                                                dialog.dismiss();

                                                                                                Toast.makeText(AdminSignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                                                            }


                                                                                        }
                                                                                    }
        );
    }

    private void saveToFirebase(String email, String pasword, String number, String username) {
        if (imageUri == null) {
            //save user info without image
            Map<String, String> map = new HashMap<>();
            map.put("uid", auth.getUid());
            map.put("email", "" + email);
            map.put("pasword", "" + pasword);
            map.put("number", "" + number);
            map.put("username", "" + username);
            map.put("acounttype", "Seller");
            map.put("profilepic", "");
            map.put("city", "" + City);
            map.put("fulladress", "" + Fulladress);
            map.put("pincode", "" + Pin);
            map.put("street", "" + Locality);
            map.put("latitude", "" + lattitude);
            map.put("longitude", "" + longitude);
            map.put("locality",""+Locality);
            database.collection("CurrentUser").document(auth.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.dismiss();
                    startActivity(new Intent(AdminSignUpActivity.this, AdminHomeActivity.class));
                 finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    startActivity(new Intent(AdminSignUpActivity.this, AdminHomeActivity.class));
                    finish();
                }
            });
        } else {
            ///save info with imageurl
            String filePathName = "/profile/" + auth.getUid();
            StorageReference reference = FirebaseStorage.getInstance().getReference(filePathName);
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    while (!task.isSuccessful()) ;
                    Uri downloadUri = task.getResult();
                    if (task.isSuccessful()) {

                        //save to db user info
                        Map<String, String> map = new HashMap<>();
                        map.put("uid", auth.getUid());
                        map.put("email", "" + email);
                        map.put("pasword", "" + pasword);
                        map.put("number", "" + number);
                        map.put("username", "" + username);
                        map.put("acounttype", "Seller");
                        map.put("profilepic", "" + downloadUri);
                        map.put("city", "" + City);
                        map.put("fulladress", "" + Fulladress);
                        map.put("pincode", "" + Pin);
                        map.put("street", "" + Locality);
                        database.collection("CurrentUser").document(auth.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                                startActivity(new Intent(AdminSignUpActivity.this, AdminHomeActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                startActivity(new Intent(AdminSignUpActivity.this, AdminHomeActivity.class));
                                finish();
                            }
                        });


                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(AdminSignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(AdminSignUpActivity.this, loocationPermission, REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0) {
                boolean locationacpetd = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (locationacpetd) {


                    //permission allowed
                    detectLocatoin();
                } else {
                    ///permission denied
                    Toast.makeText(this, "Required location permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void detectLocatoin() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8 && !(data.getData() == null)) {
            imageUri = data.getData();
            profile.setImageURI(imageUri);

        }
    }

            private void viewInitialize() {
                signUp = findViewById(R.id.btn_Signup);
                name = findViewById(R.id.name_signUp);
                number = findViewById(R.id.user_number);
                email = findViewById(R.id.email_signUp);
                password = findViewById(R.id.pasd_signUp);
                confirmPasd = findViewById(R.id.confirm_pasd_signUp);
                gps = findViewById(R.id.gps);
                profile = findViewById(R.id.profile);
                pincode=findViewById(R.id.pin);
                city=findViewById(R.id.city);
                fullAdress=findViewById(R.id.fulladress);
                securityPin=findViewById(R.id.securityPin);
                back=findViewById(R.id.back);




            }
        }
