package com.example.groceryappp.Activity.Activity;

import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.hideProgressDialog;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.showProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.AllModel.UserInfo;

import com.example.groceryappp.Activity.Receiver.InternetConnectivityReceiver;
import com.example.groceryappp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSignUpActivity extends AppCompatActivity implements InternetConnectivityReceiver.ConnectivityListener {
    private FirebaseFirestore database;
    private InternetConnectivityReceiver connectivityReceiver;
    private String City, Street, Pin, Landmark, FloorNo;
    private FirebaseAuth auth;
    private ProgressBar bar;
    private AppCompatButton register;
    private CircleImageView profile;
    private double lattitude, longitude;
    private ImageView back;
    private TextInputEditText name, email, password, number, confirmPasd, securityPin;
    private Uri imageUri;
    private String userType;
    private LinearLayout userInfoLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        viewInitialize();
        userType = getIntent().getStringExtra("USER_TYPE");
        if (userType.equals("Seller")) {
            findViewById(R.id.securityLayout).setVisibility(View.VISIBLE);


        }
        ////
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ////
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = validate();
                if (result) {
                    if (userType.equals("Seller")) {

                        if (securityPin.getText().toString().isEmpty()) {
                            securityPin.setError("Security Pin Required");
                            return;
                        }
                        if (!securityPin.getText().toString().equals("MANA")) {
                            securityPin.setError("Incorrect Security Pin");
                            return;
                        } else {
                            showProgressDialog(UserSignUpActivity.this, "Creating Account...");
                            registerUser("Seller");
                        }

                    } else {
                        showProgressDialog(UserSignUpActivity.this, "Creating Account...");
                        registerUser("User");
                    }


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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(UserSignUpActivity.this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            showOptionDialog();
                        } else {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 101);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
            }
        });

    }


    private void showOptionDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(UserSignUpActivity.this);
        String item[] = {"Upload from Gallery"};

        alert.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 8);

            }
        }).show();

    }


    private boolean validate() {
        if (name.getText().toString().trim().isEmpty() || name.getText() == null) {
            name.setError("Name is required");
            return false;
        } else if (name.getText().toString().trim().length() < 4 && name.getText().toString().trim().length() > 16) {
            name.requestFocus();
            name.setError("Name should max 16 digit");
            return false;
        } else if (number.getText().toString().trim().isEmpty()) {
            number.setError("Number is required");
            return false;
        } else if (number.getText().toString().trim().length() != 10) {
            number.requestFocus();
            number.setError("Number should be 10 digit");
            return false;
        } else if (email.getText().toString().trim().isEmpty()) {
            email.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            email.requestFocus();
            email.setError("Incorrect email address");
            return false;
        } else if (password.getText().toString().trim().isEmpty()) {
            password.setError("Password is required");
            return false;
        } else if (password.getText().toString().trim().length() < 4 && password.getText().toString().trim().length() > 10) {
            password.requestFocus();
            password.setError("Password should be 4-10 digit");
            return false;
        } else if (confirmPasd.getText().toString().trim().isEmpty()) {

            confirmPasd.setError("Confirm Password is required");
            return false;
        } else if (!password.getText().toString().trim().equals(confirmPasd.getText().toString().trim())) {
            confirmPasd.setError("Password is not matching");
            return false;
        } else {
            return true;
        }


    }

    private void registerUser(String userType) {
        Dexter.withContext(UserSignUpActivity.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                 /*   dialog2 = new ProgressDialog(UserSignUpActivity.this);
                    dialog2.setMessage("Fetching Location...");
                    dialog2.setCancelable(false);
                    dialog2.show();*/
                    fetchLastLocation();
                    String emaill = email.getText().toString().trim();
                    String paswordd = password.getText().toString().trim();
                    String userName = name.getText().toString().trim();
                    String numberr = number.getText().toString().trim();
///first create user authentication in firebase,so that we can login next time
                    auth.createUserWithEmailAndPassword(emaill, paswordd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            saveToFirebase(emaill, paswordd, numberr, userName, userType);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                            email.setText("");
                        }

                    });
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 101);
                    Toast.makeText(UserSignUpActivity.this, "Please allow permission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();


    }

    private void saveToFirebase(String email, String pasword, String number, String username, String userType) {
        if (imageUri == null) {
            //save user info without image
            // String fullAd = apartment.getText().toString() + "," + locality.getText().toString() + "," + street.getText().toString() + "," + city.getText().toString() + "," + pin.getText().toString();
            String fullAd = FloorNo + "," + Street + "," + Landmark + "," + City + "," + Pin;
            UserInfo userInfo = new UserInfo(auth.getUid(), email, pasword, number, username, userType, "", City, Street, Landmark, Pin, fullAd, "" + lattitude, "" + longitude);
            database.collection("CurrentUser").document(auth.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    hideProgressDialog();
                    showBottomSheetDialog(fullAd);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Toast.makeText(UserSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        //  String fullAd = apartment.getText().toString() + "," + street.getText().toString() + "," + locality.getText().toString() + "," + city.getText().toString() + "," + pin.getText().toString();
                        String fullAd = FloorNo + "," + Street + "," + Landmark + "," + City + "," + Pin;
                        UserInfo userInfo = new UserInfo(auth.getUid(), email, pasword, number, username, userType, "" + downloadUri, City, Street, Landmark, Pin, fullAd, "" + lattitude, "" + longitude);
                        database.collection("CurrentUser").document(auth.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                hideProgressDialog();
                                showBottomSheetDialog(fullAd);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgressDialog();
                                Toast.makeText(UserSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void showBottomSheetDialog(String add) {
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheetdialog, null);
        TextView address = view.findViewById(R.id.user_address);
        MaterialButton button = view.findViewById(R.id.continueShop);
        address.setText(add);
        // Set your desired image to the ImageView
        // imageView.setImageResource(R.drawable.your_image);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals("Seller")) {
                    Intent intent = new Intent(UserSignUpActivity.this, AdminHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(UserSignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        });

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
        dialog.setCancelable(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.getData() != null) {
            if (requestCode == 9) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profile.setImageBitmap(imageBitmap);
              /*  imageUri = data.getData();
                Glide.with(this).load(data.getData()).into(profile);*/
                //  profile.setImageURI(imageUri);
            }
            if (requestCode == 8) {
                imageUri = data.getData();
                profile.setImageURI(imageUri);
            }
        }
    }

    private void viewInitialize() {
        register = findViewById(R.id.btn_Signup);
        password = findViewById(R.id.pasd_signUp);
        email = findViewById(R.id.email_signUp);
        name = findViewById(R.id.name_signUp);
        number = findViewById(R.id.user_number);
        confirmPasd = findViewById(R.id.confirm_pasd_signUp);
        securityPin = findViewById(R.id.security_pin);
        profile = findViewById(R.id.profile);
        back = findViewById(R.id.back);

        userInfoLayout = findViewById(R.id.userInfoLayout);

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
                            lattitude = location.getLatitude();
                            longitude = location.getLongitude();
                            // Use the latitude and longitude values
                            // for your desired purpose
                            Geocoder geocoder = new Geocoder(UserSignUpActivity.this, Locale.getDefault());
                            List<Address> list;
                            try {
                                list = geocoder.getFromLocation(lattitude, longitude, 1);
                                ///GETTING ADRESS
                                City = list.get(0).getLocality();
                                Pin = list.get(0).getPostalCode();
                                Street = list.get(0).getThoroughfare() + "," + list.get(0).getSubLocality();
                                Landmark = list.get(0).getLocality();
                                FloorNo = list.get(0).getFeatureName();


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
}



