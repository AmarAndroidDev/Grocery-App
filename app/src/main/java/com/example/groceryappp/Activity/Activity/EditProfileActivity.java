package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
private SwitchCompat switchPasword;
LinearLayout paswordLayout;
LinearLayout updateProfile;
private FirebaseFirestore database;
private FirebaseAuth auth;
private ImageView back;

    private CircleImageView profile;
    private ProgressDialog dialog;
    String Name,Number,uri,userType;

    private TextInputEditText name, email, password, number,confirmPasd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();
        viewInitialize();
        database=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance().getInstance();
        //getting previous data and set
        userType=getIntent().getStringExtra("userType");
        Name=getIntent().getStringExtra("name");
        Number=getIntent().getStringExtra("number");
        uri=getIntent().getStringExtra("profileImg");


        name.setText(Name);
        number.setText(Number);
        Glide.with(this).load(uri).

                into(profile);


        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result=validateInput();
                if (result){
                    updateUser();
                }else
                    return;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent=new Intent(new Intent(EditProfileActivity.this,ProfileActivity.class));
                intent.putExtra("userType",userType);
             startActivity(intent);
             finish();
            }
        });

    }


    private boolean validateInput() {


            if (name.getText().toString().trim().isEmpty() ) {

                Toast.makeText(EditProfileActivity.this, "Please Provide Name", Toast.LENGTH_SHORT).show();
                return false;
            } if (number.getText().toString().trim().isEmpty() ) {

                Toast.makeText(EditProfileActivity.this, "Please Provide Number", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (name.getText().toString().trim().length() < 4 && name.getText().toString().trim().length() > 16) {

                name.requestFocus();
                Toast.makeText(EditProfileActivity.this, " Name should be 16 digit ", Toast.LENGTH_SHORT).show();
            return false;}
        if (number.getText().toString().trim().length() != 10) {

            number.requestFocus();
            Toast.makeText(EditProfileActivity.this, " Number should be 10 digit ", Toast.LENGTH_SHORT).show();
            return false;
        }

            else {
                return true;

            }



    }


    private void updateUser() {

        dialog = new ProgressDialog(this);

        dialog.setMessage("Updating User Details...");
        dialog.setCancelable(false);
        dialog.show();
        database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).update("name",name.getText().toString(),"number",number.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

dialog.dismiss();
                Intent intent=new Intent();
                intent.putExtra("name",name.getText().toString());
                intent.putExtra("number",number.getText().toString());
              setResult(RESULT_OK,intent);
              finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewInitialize() {
        name=findViewById(R.id.ed_name);
profile=findViewById(R.id.profile);
updateProfile=findViewById(R.id.update_profile);
        number=findViewById(R.id.ed_number);

back=findViewById(R.id.back);

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(new Intent(EditProfileActivity.this,ProfileActivity.class));
        intent.putExtra("userType",userType);
        startActivity(intent);
        finish();
    }
}