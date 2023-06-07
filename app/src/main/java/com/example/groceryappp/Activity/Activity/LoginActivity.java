package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.AllModel.SaveInfo;
import com.example.groceryappp.Activity.HomeActivity;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
private TextView forgotPasword,adminLogin,signUp;
private EditText email,password;
FirebaseFirestore database;
private AppCompatButton login;
private ProgressDialog dialog;
private FirebaseAuth auth;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        initilization();
        auth=FirebaseAuth.getInstance();
       database=FirebaseFirestore.getInstance();
        dialog=new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Logging in...");
        dialog.setCancelable(false);


        forgotPasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));

            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, UserSignUpActivity.class);
                startActivity(intent);
            }
        });
login.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        loginProcess();
    }
});
        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(new Intent(LoginActivity.this,AdminSignUpActivity.class)));

            }
        });
    }



    private void loginProcess() {

        dialog.show();
        if( password.getText().toString().trim().isEmpty()&& email.getText().toString().trim().isEmpty()) {
            dialog.dismiss();
            Toast.makeText(LoginActivity.this, "Please Provide All Details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            dialog.dismiss();
            email.requestFocus();
            Toast.makeText(LoginActivity.this, "Incorrect email address", Toast.LENGTH_SHORT).show();
       return;
        }
        else {
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    checkUserType();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Please use valid Credential", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

    private void checkUserType() {
        //if usertype is user then send to the usermainactivity,if seller then seller activityt
        database.collection("CurrentUser").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               String type= String.valueOf(documentSnapshot.get("acounttype"));
                if (type.equals("Seller")){
                    dialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                    finish();
                }
                if (type.equals("user") ){
                    dialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }

            }
        });







    }

    private void initilization() {
        forgotPasword=findViewById(R.id.forgot_passeord);
        adminLogin=findViewById(R.id.admin_login);
        login=findViewById(R.id.btn_login);
        email=findViewById(R.id.email_logiin);
        password=findViewById(R.id.pasd_login);
        signUp=findViewById(R.id.signUp);




    }

    @Override
    public void onBackPressed() {
finish();
    }
}