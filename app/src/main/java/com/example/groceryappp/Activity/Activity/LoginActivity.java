package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
private TextView forgotPasword,adminLogin,signUp;
private TextInputEditText email,password;
FirebaseFirestore database;
private MaterialButton login;
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
      /*  adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(new Intent(LoginActivity.this,AdminSignUpActivity.class)));

            }
        });*/
    }



    private void loginProcess() {

        dialog.show();
        if( email.getText().toString().trim().isEmpty()) {
            dialog.dismiss();
            email.requestFocus();
            email.setError("Email is Required");

            return;
        }else
            email.setError(null);
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            dialog.dismiss();
            email.requestFocus();
            email.setError("Incorrect email address");

            return;
        }
        if( password.getText().toString().trim().isEmpty()) {
            dialog.dismiss();
            password.requestFocus();
            password.setError("Password is Required");


            return;
        }else password.setError(null);




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





    private void checkUserType() {
        //if usertype is user then send to the usermainactivity,if seller then seller activity
        database.collection("CurrentUser").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               String type= String.valueOf(documentSnapshot.get("accountType"));
                if (type.equals("Seller")){
                    dialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                    finish();
                }
                if (type.equals("User") ){
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