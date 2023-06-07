package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {
private TextView register;
private EditText email,pasword;
private AppCompatButton login;
private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amin_login);
        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        viewInitialize();
        clickListner();


    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin();
    }

    private void clickListner() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              loginProcess();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminLoginActivity.this,AdminSignUpActivity.class));
                finish();
            }
        });
    }

    private void viewInitialize() {
        register=findViewById(R.id.admin_register);
        email=findViewById(R.id.admin_email_login);
        pasword=findViewById(R.id.admin_pasword_login);

        login=findViewById(R.id.btn_login);
    }
    private void isLogin() {
        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(AdminLoginActivity.this, AdminHomeActivity.class));
        }
    }
    private void loginProcess() {
        if( pasword.getText().toString().isEmpty()&& email.getText().toString().isEmpty()) {
            Toast.makeText(AdminLoginActivity.this, "Please Provide All Details", Toast.LENGTH_SHORT).show();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.requestFocus();
            Toast.makeText(AdminLoginActivity.this, "Please provide the correct email adress", Toast.LENGTH_SHORT).show();
        }
        else {
            auth.signInWithEmailAndPassword(email.getText().toString(), pasword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(AdminLoginActivity.this, AdminHomeActivity.class));
                    } else {
                        Toast.makeText(AdminLoginActivity.this, "Error" , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }

}