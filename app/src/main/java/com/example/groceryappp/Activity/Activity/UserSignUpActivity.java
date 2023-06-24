package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSignUpActivity extends AppCompatActivity implements LocationListener {
    private TextView userInfo,adress;
    private AppCompatButton register;
    private CircleImageView profile;

    private double lattitude, longitude;
    private ImageView gps,back;
    private TextInputEditText name, email, password, number,confirmPasd;
    private TextInputEditText locality, pin, city, apartment, street;
    private TextView curentLocation;
    private ProgressDialog dialog;
    private ProgressDialog dialog2;
    private Uri imageUri;
    FirebaseFirestore database;
    private String[] loocationPermission;
    String Fulladress, City, Locality, Pin;
    FirebaseAuth auth;
    ProgressBar bar;
    private LinearLayout userInfoLayout,userAdressLayout,card;
    private LocationManager locationManager;
    private final int REQUEST_PERMISSION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        viewInitialize();

        ///permission for location
        loocationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              boolean result= validate();
            if (result){
                dialog = new ProgressDialog(UserSignUpActivity.this);
                dialog.setTitle("Creating Account");
                dialog.setMessage("Please Wait...");
                dialog.setCancelable(false);
                dialog.show();
                registerUser();
            }else {
                return;
            }

            }
        });
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAdressLayout.setVisibility(View.GONE);
                userInfoLayout.setVisibility(View.VISIBLE);
                userInfo.setTextColor(Color.WHITE);
                adress.setTextColor(Color.BLACK);
                userInfo.setBackgroundResource(R.drawable.green_solid_bg);
              //  card.setBackgroundColor(getResources().getColor(R.color.fulllightgreenBg));
                adress.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        adress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAdressLayout.setVisibility(View.VISIBLE);
                userInfoLayout.setVisibility(View.GONE);
                userInfo.setTextColor(Color.BLACK);
                adress.setTextColor(Color.WHITE);
                adress.setBackgroundResource(R.drawable.green_solid_bg);
               // card.setBackgroundColor(getResources().getColor(R.color.fulllightgreenBg));
                userInfo.setBackgroundColor(Color.TRANSPARENT);
            }
        });
back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        onBackPressed();
    }
});
        curentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2 = new ProgressDialog(UserSignUpActivity.this);
                dialog2.setMessage("Fetching Location...");
                dialog2.setCancelable(false);
                dialog2.show();
                if (checkLocationPermission()) {
                    ///location alowwed
                    detectLocatoin();
                }
                else {
                    //request location permission
                    requestPermission();
                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UserSignUpActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(UserSignUpActivity.this,new String[]{Manifest.permission.CAMERA},1);
                }else {
                    showOptionDialog();
                }


            }
        });

    }

    private void showOptionDialog() {
        AlertDialog.Builder alert=new AlertDialog.Builder(UserSignUpActivity.this);
       String item[]={"Camera","Gallery"};
        alert.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
if (item[i].equals("Camera")){
    Intent intent = new Intent();
    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent, 9);

}else {
     Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 8);
}
            }
        }).show();

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
            City = list.get(0).getLocality();
            Pin = list.get(0).getPostalCode();
            Locality = list.get(0).getSubLocality();

            //set adress to textview

            city.setText(City);
            locality.setText(Locality);
            city.setText(City);
            pin.setText(Pin);
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

        if (name.getText().toString().trim().isEmpty()){

            name.setError("Name is required");

            return false;
        }  if (name.getText().toString().trim().length() < 4 && name.getText().toString().trim().length() > 16) {

            name.requestFocus();
            name.setError("Name should max 16 digit");
            return false;
        }
        if (number.getText().toString().trim().isEmpty() ){

            number.setError("Number is required");

            return false;
        }
        if (number.getText().toString().trim().length() != 10) {

            number.requestFocus();
            number.setError("Number should be 10 digit");
            return false;
        }
        if(email.getText().toString().trim().isEmpty()) {

            email.setError("Email is required");
            return false;
        }
        if (password.getText().toString().trim().isEmpty()){

            password.setError("Password is required");
            return false;
        } if (confirmPasd.getText().toString().trim().isEmpty() ) {

            confirmPasd.setError("Confirm Password is required");
            return false;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {

            email.requestFocus();
            email.setError("Incorrect email address");
            return false;
        }


        if (password.getText().toString().trim().length() < 4 && password.getText().toString().trim().length() > 10) {

            password.requestFocus();
            password.setError("Password should be 4-10 digit");
            return false;
        }

        if (!password.getText().toString().trim().equals(confirmPasd.getText().toString().trim())) {

            confirmPasd.setError("Password is not matching");

            return false;
        }

  if (apartment.getText().toString().trim().isEmpty()){

      apartment.setError("Apartment/House is required");
            return false;
        }
        if(street.getText().toString().trim().isEmpty()) {

            street.setError("Street is required");
            return false;
        }
        if (locality.getText().toString().trim().isEmpty() ){

            locality.setError("Locality is required");
            return false;
        }
        if (city.getText().toString().trim().isEmpty()){

            city.setError("City is required");
            return false;
        } if (pin.getText().toString().trim().isEmpty() ) {

            pin.setError("PinCode is required");
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

                                                                                                Toast.makeText(UserSignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                                                            }


                                                                                        }
                                                                                    }
        );
    }

    private void saveToFirebase(String email, String pasword, String number, String username) {
        if (imageUri == null) {
         //save user info without image
           /*    Map<String, String> map = new HashMap<>();
            map.put("uid", auth.getUid());
            map.put("email", "" + email);
            map.put("pasword", "" + pasword);
            map.put("number", "" + number);
            map.put("username", "" + username);
            map.put("acounttype", "user");
            map.put("profilepic", "");
            map.put("city", "" + City);
            map.put("fulladress", "" + Fulladress);
            map.put("pincode", "" + Pin);
            map.put("street", "" + Locality);
            map.put("latitude", String.valueOf(lattitude));
            map.put("longitude", String.valueOf(longitude));*/
            String fullAd=apartment.getText().toString()+","+locality.getText().toString()+","+street.getText().toString()+","+city.getText().toString()+","+pin.getText().toString();
            UserInfo userInfo=new UserInfo(auth.getUid(),email,pasword,number,username,"User","",apartment.getText().toString(),city.getText().toString(),locality.getText().toString(),street.getText().toString(),pin.getText().toString(),fullAd,""+lattitude,""+longitude);
            database.collection("CurrentUser").document(auth.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.dismiss();
                    Intent intent=new Intent(UserSignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Intent intent=new Intent(UserSignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

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
                       /* Map<String, String> map = new HashMap<>();
                        map.put("uid", auth.getUid());
                        map.put("email", "" + email);
                        map.put("pasword", "" + pasword);
                        map.put("number", "" + number);
                        map.put("username", "" + username);
                        map.put("acounttype", "user");
                        map.put("profilepic", "" + downloadUri);
                        map.put("city", "" + City);
                        map.put("fulladress", "" + Fulladress);
                        map.put("pincode", "" + Pin);
                        map.put("street", "" + Locality);*/
                        String fullAd=apartment.getText().toString()+","+street.getText().toString()+","+locality.getText().toString()+","+city.getText().toString()+","+pin.getText().toString();
                        UserInfo userInfo=new UserInfo(auth.getUid(),email,pasword,number,username,"user",""+downloadUri,apartment.getText().toString(),city.getText().toString(),locality.getText().toString(),street.getText().toString(),pin.getText().toString(),fullAd,""+lattitude,""+longitude);
                        database.collection("CurrentUser").document(auth.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                                Intent intent=new Intent(UserSignUpActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Intent intent=new Intent(UserSignUpActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


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
        boolean result = ContextCompat.checkSelfPermission(UserSignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(UserSignUpActivity.this, loocationPermission, REQUEST_PERMISSION);
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
        if (requestCode == 8 && !(data.getData() != null )) {
            imageUri = data.getData();
            profile.setImageURI(imageUri);

        }else {

        } if (requestCode == 9 && !(data.getData() != null )) {
           Bitmap bitmap= (Bitmap) data.getExtras().get("data");
           profile.setImageBitmap(bitmap);
            imageUri = data.getData();
          //  profile.setImageURI(imageUri);

        }else {

        }
    }
    private void viewInitialize() {
        register=findViewById(R.id.btn_Signup);

        password=findViewById(R.id.pasd_signUp);
        email=findViewById(R.id.email_signUp);

        confirmPasd = findViewById(R.id.confirm_pasd_signUp);

        profile = findViewById(R.id.profile);

        back = findViewById(R.id.back);

curentLocation=findViewById(R.id.curentLocation);
        locality=findViewById(R.id.locality);
        street=findViewById(R.id.street);
        pin=findViewById(R.id.pin);
        city=findViewById(R.id.city);
        apartment=findViewById(R.id.apart_house);
        userInfo=findViewById(R.id.userInfo);
        adress=findViewById(R.id.adress);
        userAdressLayout=findViewById(R.id.adressLayout);
        userInfoLayout=findViewById(R.id.userInfoLayout);
        card=findViewById(R.id.card);


    }


}