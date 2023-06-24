package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.groceryappp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditDeliveryDetailActivity extends AppCompatActivity {
    private TextInputEditText name, number;
    private TextInputEditText locality, pin, city, apartment;
    private MaterialButton addAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delivery_detail);
        getSupportActionBar().hide();
        viewInitialize();
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (name.getText().toString().trim().isEmpty()){

                    name.setError("Name is required");

                    return ;
                }  if (name.getText().toString().trim().length() < 4 && name.getText().toString().trim().length() > 16) {

                    name.requestFocus();
                    name.setError("Name should max 16 digit");
                    return ;
                }
                if (number.getText().toString().trim().isEmpty() ){

                    number.setError("Number is required");

                    return ;
                }
                if (number.getText().toString().trim().length() != 10) {

                    number.requestFocus();
                    number.setError("Number should be 10 digit");
                    return ;
                }

                if (apartment.getText().toString().trim().isEmpty()){

                    apartment.setError("Apartment/House is required");
                    return ;
                }

                if (locality.getText().toString().trim().isEmpty() ){

                    locality.setError("Locality is required");
                    return ;
                }
                if (city.getText().toString().trim().isEmpty()){

                    city.setError("City is required");
                    return ;
                } if (pin.getText().toString().trim().isEmpty() ) {

                    pin.setError("PinCode is required");
                    return ;
                }
                else {
                    addAdress();
                }

            }
        });

    }

    private void addAdress() {
        Intent intent=new Intent();
        intent.putExtra("Name",name.getText().toString());
        intent.putExtra("Number",number.getText().toString());
        intent.putExtra("Full_Add",apartment.getText().toString()+","+locality.getText().toString()+","+city.getText().toString()+","+pin.getText().toString());
    setResult(RESULT_OK,intent);
    finish();

    }

    private void viewInitialize() {
        name=findViewById(R.id.name_signUp);
        number=findViewById(R.id.user_number);
        locality=findViewById(R.id.locality);
        pin=findViewById(R.id.pin);
        city=findViewById(R.id.city);
        apartment=findViewById(R.id.apart_house);
        addAddress=findViewById(R.id.addAdres);
    }
}