package com.example.groceryappp.Activity.Activity;

import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.hideProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.ecs.dbsekycapi.ESignActivity;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Utills.ProgressDialogUtils;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
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
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
private AppCompatButton verify;
    private double lattitude, longitude;
    private TextView userMob,otpTimer,resend,txtResend;
    private String City, Street, Pin, Landmark, FloorNo, fullAd;
    private PinView pinView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String otpFirebase;
    String mobb;
    ArrayList<String> numberList;
private CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
verify=findViewById(R.id.verify);
        userMob=findViewById(R.id.mob);
        pinView=findViewById(R.id.otp);
        otpTimer=findViewById(R.id.otptimer);
        resend=findViewById(R.id.resendOtp);
        txtResend=findViewById(R.id.txtresend);
         mobb=getIntent().getStringExtra("MOB");
     numberList = new ArrayList<>() ;
        otpFirebase=getIntent().getStringExtra("OTP");
        String maskMob=maskMobileNumber(mobb);
        userMob.setText("Verification code successfully send to "+"+91-"+maskMob);
        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Dexter.withContext(OtpActivity.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    ///first create user authentication in firebase,so that we can login next time
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 101);
                    Toast.makeText(OtpActivity.this, "Please allow permission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
verify.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (pinView.getText().length()==6){
            hideKeyboard();
            verifyOtp();
        }
    else {
            Toast.makeText(OtpActivity.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
        }
    }
});
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
if (editable.length()==6){

    verify.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBg));
}else {
    verify.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBglight));
}
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp();
            }
        });
/*verify.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (!pinView.getText().toString().isEmpty()){
           ProgressDialogUtils.showProgressDialog(OtpActivity.this,"Verifying Otp...");
            FirebaseClient.getInstance().collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        String userType=null;
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            String number = snapshot.getString("number");
                            userType = snapshot.getString("accountType");
                            numberList.add(number);
                        }
                        if (numberList.contains(mobb)){
                            String userId=mobb+"7978";
                            moveToExistingAccount(userType,userId);
                        }else {
                            fetchLastLocation();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

//verifyOtp(dialog);

        }else {
            Toast.makeText(OtpActivity.this, "Please enter 6 digit otp", Toast.LENGTH_SHORT).show();
        }
    }
});*/
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Countdown is in progress
                long secondsLeft = millisUntilFinished / 1000;
                otpTimer.setText("("+String.valueOf(secondsLeft)+")");

            }
            @Override
            public void onFinish() {
                // Timer has finished, stop scanning
           resend.setEnabled(true);
           resend.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.buttonBg)));
           otpTimer.setVisibility(View.GONE);
            }
        }.start();

    }

    private void moveToExistingAccount(String userType, String userId) {
        SharedPreferenceManager.getInstance(OtpActivity.this).storeUserId(userId);
            Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

    }

    private void sendOtp() {
        pinView.setText("");
        findViewById(R.id.sendingotp).setVisibility(View.VISIBLE);
resend.setEnabled(false);
            mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.


                    // signInWithPhoneAuthCredential(credential);
                }
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    resend.setEnabled(true);
                    findViewById(R.id.sendingotp).setVisibility(View.GONE);


                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        // Invalid request
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Show a message and update the UI
                }
                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    findViewById(R.id.sendingotp).setVisibility(View.GONE);

                    Toast.makeText(OtpActivity.this, "Otp send successfully to your phone", Toast.LENGTH_SHORT).show();
countDownTimer.start();
resend.setEnabled(false);
                    resend.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.gray)));

                    // Save verification ID and resending token so we can use them later
                }
            };

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber("+91"+mobb)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // (optional) Activity for callback binding
                            // If no activity is passed, reCAPTCHA verification can not be used.
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }


    private void verifyOtp() {
     findViewById(R.id.verifyotp).setVisibility(View.VISIBLE);
        PhoneAuthCredential authCredential= PhoneAuthProvider.getCredential(otpFirebase,pinView.getText().toString());
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseClient.getInstance().collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String userType=null;
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                String number = snapshot.getString("number");
                                userType = snapshot.getString("accountType");
                                numberList.add(number);
                            }
                            if (numberList.contains(mobb)){
                                String userId=mobb+"7978";
                                moveToExistingAccount(userType,userId);
                            }else {
                                fetchLastLocation();
                            }
                        }else {
                            findViewById(R.id.verifyotp).setVisibility(View.GONE);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findViewById(R.id.verifyotp).setVisibility(View.GONE);
                        Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                findViewById(R.id.verifyotp).setVisibility(View.GONE);
                Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String maskMobileNumber(String mobileNumber) {
        int visibleDigits = 4; // Number of visible digits (you can change this as per your requirement)
        int totalDigits = mobileNumber.length();

        if (totalDigits <= visibleDigits) {
            return mobileNumber; // Return the number as it is if it has fewer digits than the visibleDigits
        } else {
            String maskedPart = new String(new char[totalDigits - visibleDigits]).replace("\0", "X");
            String visiblePart = mobileNumber.substring(totalDigits - visibleDigits);
            return maskedPart + "-" + visiblePart;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
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
                            Geocoder geocoder = new Geocoder(OtpActivity.this, Locale.getDefault());
                            List<Address> list;
                            try {
                                list = geocoder.getFromLocation(lattitude, longitude, 1);
                                ///GETTING ADRESS
                                City = list.get(0).getLocality();
                                Pin = list.get(0).getPostalCode();
                                Street = list.get(0).getThoroughfare() + "," + list.get(0).getSubLocality();
                                Landmark = list.get(0).getLocality();
                                FloorNo = list.get(0).getFeatureName();
                       findViewById(R.id.verifyotp).setVisibility(View.GONE);
                                fullAd = FloorNo + "," + Street.replace("","") + "," + Landmark + "," + City + "," + Pin;
                                showBottomSheetDialog(fullAd);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                });
    }


    private void showBottomSheetDialog(String add) {
        View view = LayoutInflater.from(this).inflate(R.layout.number_register_user, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
        dialog.setCancelable(false);
        TextView address = view.findViewById(R.id.address);

       CardView register = view.findViewById(R.id.register);

        TextInputEditText userName = view.findViewById(R.id.name_signUp);
        address.setText(add);
        // Set your desired image to the ImageView
        // imageView.setImageResource(R.drawable.your_image);
        register.setCardBackgroundColor(getResources().getColor(R.color.buttonBglight));
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (input.length() !=0) {
register.setEnabled(true);
                    register.setCardBackgroundColor(getResources().getColor(R.color.buttonBg));


                } else {
                    register.setEnabled(false);
                    register.setCardBackgroundColor(getResources().getColor(R.color.buttonBglight));
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userName.getText().toString().isEmpty()){
                    dialog.cancel();
                    saveToFirebase(userName.getText().toString());
                }else {
                    userName.setError("Name Required");
                }



            }
        });

    }
    private void saveToFirebase(String username) {
      final String userId=mobb+"7978";
        ProgressDialogUtils.showProgressDialog(OtpActivity.this,"Creating Account...");
        //  String fullAd = apartment.getText().toString() + "," + street.getText().toString() + "," + locality.getText().toString() + "," + city.getText().toString() + "," + pin.getText().toString();
        UserInfo userInfo = new UserInfo(userId, "", "", mobb, username, "User", "", City, Street, Landmark, Pin, fullAd, "" + lattitude, "" + longitude);
        FirebaseClient.getInstance().collection("CurrentUser").document(userId).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
SharedPreferenceManager.getInstance(OtpActivity.this).storeUserId(userId);
                Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}