package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
private TextInputEditText emailId;
private AppCompatButton recover;
private TextView txtSuccess;
private ImageView back;
private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        getSupportActionBar().hide();
        emailId=findViewById(R.id.email);
        recover=findViewById(R.id.recover);
        txtSuccess=findViewById(R.id.txtsucess);
        back=findViewById(R.id.back);
        auth=FirebaseAuth.getInstance();

        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPasseord();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    private void recoverPasseord() {
        if (emailId.getText().toString().equals("")){
            emailId.setError("Email is required");

            return;
        }  if (!Patterns.EMAIL_ADDRESS.matcher(emailId.getText().toString()).matches()){
            emailId.setError("Incorrect  Email Id");
            return;
        }else {
          ProgressDialog  progressDialog=new  ProgressDialog(this);
            progressDialog.setMessage("Sending request...");
            progressDialog.show();
            auth.sendPasswordResetEmail(emailId.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    txtSuccess.setVisibility(View.VISIBLE);
                    txtSuccess.setText("Please open your email for reset password");
                    recover.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txtSuccess.setTextColor(getColor(R.color.bg2));
                    }
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   // Toast.makeText(PasswordResetActivity.this, "Error-"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    txtSuccess.setVisibility(View.VISIBLE);
                    txtSuccess.setText(e.getMessage());
                    txtSuccess.setTextColor(Color.RED);
                    progressDialog.dismiss();
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}