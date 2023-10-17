package com.example.groceryappp.Activity.Activity;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.hideProgressDialog;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.showProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.AllModel.UserInfo;

import com.example.groceryappp.Activity.Receiver.InternetConnectivityReceiver;
import com.example.groceryappp.Activity.Utills.ProgressDialogUtils;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;public class LoginActivity extends AppCompatActivity implements InternetConnectivityReceiver.ConnectivityListener {
    private static final int SMS_VERIFICATION_TIMEOUT = 60;
    private static final String TAG = "GoogleSignIn";
    private static final int RC_SIGN_IN = 123;
    public static GoogleSignInClient mGoogleSignInClient;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String type;
    private String City, Street, Pin, Landmark, FloorNo, fullAd;
    private ArrayList<String> emailFromDatabase;
    private double lattitude, longitude;
    private ConstraintLayout addressLayout;
    private InternetConnectivityReceiver connectivityReceiver;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView  adminLogin, signUp;
    private CardView guest,btnSignGoogle,sendOtp;
    private TextInputEditText phNo;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    ;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initilization();
        auth = FirebaseAuth.getInstance();
        emailFromDatabase = new ArrayList<>();

        findViewById(R.id.waveBottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdminLoginDialog();
            }
        }); sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phNo.getText().length()==10){
                    hideKeyboard();
                    sendOtpToMobile();
                    return;
                }else {
                    phNo.setError("Invalid Number");
                }

            }
        });
        getSupportActionBar().hide();


        ////
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ////
        Dexter.withContext(LoginActivity.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    ///first create user authentication in firebase,so that we can login next time
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 101);
                    Toast.makeText(LoginActivity.this, "Please allow permission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
        findViewById(R.id.txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UserSignUpActivity.class);
                intent.putExtra("USER_TYPE", "Seller");
                startActivity(intent);
            }
        });

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignGoogle = findViewById(R.id.btn_sign_google);
        btnSignGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UserSignUpActivity.class);
                intent.putExtra("USER_TYPE", "User");
                startActivity(intent);
            }
        });
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


    }

    private void showAdminLoginDialog() {
        BottomSheetDialog dialog=new BottomSheetDialog(this);
        View view=LayoutInflater.from(this).inflate(R.layout.admin_login,null);
        dialog.setContentView(view);
        dialog.show();


         CardView login;
        TextInputEditText email, password;
        TextView  forgotPasword = view.findViewById(R.id.forgot_password);
        login = view.findViewById(R.id.adminLogin);
        email =  view.findViewById(R.id.login);
        password =  view.findViewById(R.id.pasd_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProcess(email,password);
                // sendOTP(number.getText().toString());
            }
        });
        forgotPasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));

            }
        });
    }

    private void sendOtpToMobile() {
        findViewById(R.id.sendingotp).setVisibility(View.VISIBLE);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+91" + phNo.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(LoginActivity.this)
                        // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                phNo.setEnabled(true);
                                findViewById(R.id.sendingotp).setVisibility(View.GONE);
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                                    findViewById(R.id.sendingotp).setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    // Invalid request
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    findViewById(R.id.sendingotp).setVisibility(View.GONE);
                                    // The SMS quota for the project has been exceeded
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                findViewById(R.id.sendingotp).setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                // The SMS verification code has been sent to the provided phone number, we
                                // now need to ask the user to enter the code and then construct a credential
                                // by combining the code with a verification ID.

                                navToOtp(verificationId);
                                // Save verification ID and resending token so we can use them later
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void navToOtp(String verificationId) {
        Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
        intent.putExtra("MOB", phNo.getText().toString());
        intent.putExtra("OTP", verificationId);
        startActivity(intent);
    }


    private void loginProcess(TextInputEditText email,TextInputEditText password) {
        if (email.getText().toString().trim().isEmpty()) {
            email.requestFocus();
            email.setError("Email is Required");
            return;
        } else
            email.setError(null);
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.requestFocus();
            email.setError("Incorrect email address");
            return;
        }
        if (password.getText().toString().isEmpty()) {
            password.requestFocus();
            password.setError("Password is Required");
            return;
        } else password.setError(null);
        showProgressDialog(LoginActivity.this, "Log in...");
        auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                checkUserType();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void checkUserType() {
        //if usertype is user then send to the usermainactivity,if seller then seller activity
     /*   FirebaseClient.getInstance().collection("CurrentUser").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
              *//*  String type = String.valueOf(documentSnapshot.get("accountType"));
                if (type.equals("Seller")) {
                    hideProgressDialog();
                    startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                    finish();
                }
                if (type.equals("User")) {
                    hideProgressDialog();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }*//*

            }
        });*/
        SharedPreferenceManager.getInstance(this).storeUserId(auth.getUid());
        hideProgressDialog();
        startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
        finish();
    }

    private void initilization() {

sendOtp=findViewById(R.id.senOtp);
        guest = findViewById(R.id.guest);
        addressLayout = findViewById(R.id.addressLayout);

        signUp = findViewById(R.id.signUp);
        phNo = findViewById(R.id.ph_no);


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void sendOTP(String phoneNumber) {

        // initializing our callbacks for on
        // verification callback method.

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+917997160405")            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.e("ERRORR", "onVerificationFailed: " + e.getMessage().toString());
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Toast.makeText(LoginActivity.this, s.toString(), Toast.LENGTH_SHORT).show();
                            }
                        })           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "sucessfuly", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn() {
findViewById(R.id.googleLogin).setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                findViewById(R.id.googleLogin).setVisibility(View.GONE);
                Log.e(TAG, "Google sign in failed" + e.getMessage());
                Toast.makeText(LoginActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseClient.getInstance().collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String email = snapshot.getString("email");
                        emailFromDatabase.add(email);
                    }
                    Log.e(TAG, "onComplete: " + emailFromDatabase);
                    if (emailFromDatabase != null) {
                        if (emailFromDatabase.contains(acct.getEmail())) {
                            auth.signInWithCredential(credential)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                SharedPreferenceManager.getInstance(LoginActivity.this).storeUserId(auth.getUid());
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();

                                            } else {
                                                findViewById(R.id.googleLogin).setVisibility(View.GONE);
                                                // If sign in fails, display a message to the user.
                                                // Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            findViewById(R.id.googleLogin).setVisibility(View.GONE);
                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else {
                            auth.signInWithCredential(credential)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                registerUser(acct.getEmail(), acct.getDisplayName(), "" + acct.getPhotoUrl());
                                            } else {
                                                findViewById(R.id.googleLogin).setVisibility(View.GONE);
                                                // If sign in fails, display a message to the user.
                                                // Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            findViewById(R.id.googleLogin).setVisibility(View.GONE);
                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }


                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                findViewById(R.id.googleLogin).setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void registerUser(String email, String name, String photourl) {
        Dexter.withContext(LoginActivity.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                 /*   dialog2 = new ProgressDialog(UserSignUpActivity.this);
                    dialog2.setMessage("Fetching Location...");
                    dialog2.setCancelable(false);
                    dialog2.show();*/

                    fetchLastLocation(email, name, photourl);

///first create user authentication in firebase,so that we can login next time

                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 101);
                    Toast.makeText(LoginActivity.this, "Please allow permission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();


    }

    private void fetchLastLocation(String email, String name, String photourl) {
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
                            Geocoder geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
                            List<Address> list;
                            try {
                                list = geocoder.getFromLocation(lattitude, longitude, 1);
                                ///GETTING ADRESS
                                City = list.get(0).getLocality();
                                Pin = list.get(0).getPostalCode();
                                Street = list.get(0).getThoroughfare() + "," + list.get(0).getSubLocality();
                                Landmark = list.get(0).getLocality();
                                FloorNo = list.get(0).getFeatureName();
                                saveToFirebase(email, name, photourl);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                });
    }

    private void saveToFirebase(String email, String username, String photourl) {
        //  String fullAd = apartment.getText().toString() + "," + street.getText().toString() + "," + locality.getText().toString() + "," + city.getText().toString() + "," + pin.getText().toString();
        fullAd = FloorNo + "," + Street + "," + Landmark + "," + City + "," + Pin;
        UserInfo userInfo = new UserInfo(auth.getUid(), email, "", "", username, "User", photourl, City, Street, Landmark, Pin, fullAd, "" + lattitude, "" + longitude);
        FirebaseClient.getInstance().collection("CurrentUser").document(auth.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                SharedPreferenceManager.getInstance(LoginActivity.this).storeUserId(auth.getUid());

                showAdressLayout(fullAd);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showAdressLayout(String add) {
        addressLayout.setVisibility(View.VISIBLE);
       // View view = LayoutInflater.from(this).inflate(R.layout.bottomsheetdialog, null);
        TextView address = findViewById(R.id.user_address);
        MaterialButton button = findViewById(R.id.continueShop);
        address.setText(add);
        // Set your desired image to the ImageView
        // imageView.setImageResource(R.drawable.your_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

      /*  BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
        dialog.setCancelable(false);*/

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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
