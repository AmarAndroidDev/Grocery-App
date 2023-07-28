package com.example.groceryappp.Activity.Activity;

import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.hideProgressDialog;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.showProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminProductActivity extends AppCompatActivity {

    FirebaseFirestore database;
    FirebaseStorage storage;
    boolean value;
    Uri imageUri;
    SingleProductDetails listttt;
    SingleProductDetails adminList2;
    FirebaseAuth auth;
    ImageView itemImage;
    private SingleProductDetails list;
    private Spinner spQty, spAvailiable, spCategory;
    private TextView txtQty;
    private TextInputEditText itemName, price, marketprice;
    private MaterialButton add;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        getSupportActionBar().hide();
        viewInitialize();
        instanceCreated();
        onClick();
        gettingBundle();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        list = (SingleProductDetails) getIntent().getSerializableExtra("list");
        timestamp = String.valueOf(System.currentTimeMillis());
        if (value) {
            newAddItem();
        } else {
            updateItem();
        }
    }

    private void gettingBundle() {
        adminList2 = (SingleProductDetails) getIntent().getSerializableExtra("adminlist");
        value = getIntent().getBooleanExtra("add", false);
    }

    private void spinner(String qty, String avail, String cargory) {

        ////spinner work
        String[] qtyy = {qty, "500gm", "1kg", "1pc", "1pkt", "1unit"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, qtyy);

        spQty.setAdapter(adapter);

        String[] available = {avail, "No"};
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, available);
        spAvailiable.setAdapter(adapter2);

        String[] category = {cargory, "Fruit", "Leafies", "Others", "Seasonal"};
        ArrayAdapter adapter3 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, category);
        spCategory.setAdapter(adapter3);

    }

    private void onClick() {
        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 89);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemImage.getDrawable() == null) {
                    Toast.makeText(AdminProductActivity.this, "please upload product image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (itemName.getText().toString().isEmpty()) {
                    itemName.setError("Product Name Required");
                    return;
                }
                if (marketprice.getText().toString().isEmpty()) {
                    marketprice.setError("Market Price Required");
                    return;
                }
                if (price.getText().toString().isEmpty()) {
                    price.setError("price Required");
                    return;
                }

                if (value) {
                    addNewItemToDatabase(timestamp, "Added Successfully");
                    return;
                }
                if (imageUri != null) {
                    addNewItemToDatabase(list.getId(), "Updated Successfully");
                    return;
                } else {
                    updateItemWithoutImageUri(list.getId());
                }


            }
        });

    }

    private void updateItemWithoutImageUri(String timestampp) {
        showProgressDialog(this, "Updating Item");
        SingleProductDetails listttt3 = new SingleProductDetails(itemName.getText().toString(), Integer.parseInt(price.getText().toString().replace("₹", "")), spQty.getSelectedItem().toString(), spAvailiable.getSelectedItem().toString()
                , spCategory.getSelectedItem().toString(), Integer.parseInt(marketprice.getText().toString().replace("₹", "")), list.getImgUri());
        listttt3.setId(timestampp);

        database.collection("CurrentUser").document(auth.getUid()).collection("All Item").document(timestampp).set(listttt3).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                hideProgressDialog();
                Toast.makeText(AdminProductActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminProductActivity.this, AdminHomeActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void instanceCreated() {
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    private void addNewItemToDatabase(String timestampp, String toastgmsg) {
        showProgressDialog(this, "Adding Item..");
        StorageReference reference = storage.getReference().child("Item Image").child(timestampp);
        SingleProductDetails listttt3 = new SingleProductDetails(itemName.getText().toString(), Integer.parseInt(price.getText().toString().replace("₹", "")), spQty.getSelectedItem().toString(), spAvailiable.getSelectedItem().toString()
                , spCategory.getSelectedItem().toString(), Integer.parseInt(marketprice.getText().toString().replace("₹", "")), imageUri.toString());
        listttt3.setId(timestampp);

        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listttt3.setImgUri(uri.toString());
                        database.collection("CurrentUser").document(auth.getUid()).collection("All Item").document(timestampp).set(listttt3).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                hideProgressDialog();
                                Toast.makeText(AdminProductActivity.this, toastgmsg, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdminProductActivity.this, AdminHomeActivity.class));
                                finish();
                            }
                        });
                    }
                });

            }
        });


    }


    private void viewInitialize() {
        add = findViewById(R.id.add_item);
        spAvailiable = findViewById(R.id.sp_available);
        spCategory = findViewById(R.id.sp_category);
        itemName = findViewById(R.id.prdct_name);
        spQty = findViewById(R.id.sp_qty);


        price = findViewById(R.id.prdct_yourP);
        marketprice = findViewById(R.id.prdct_marketP);
        itemImage = findViewById(R.id.pdctImg);


    }

    private void updateItem() {
        //  update.setVisibility(View.VISIBLE);
        add.setText("Update");
        itemName.setText(list.getName());
        marketprice.setText("₹" + "" + list.getMarktPrice());
        price.setText("₹" + "" + list.getPrice());

        price.setText("₹" + "" + list.getPrice());
        spinner(list.getQty(), list.getAvailiable(), list.getCategory());


        //   Uri uri = Uri.parse(adminList2.getImgUri());

        Glide.with(this).load(list.getImgUri()).into(itemImage);

        String[] qtyy = {list.getQty()};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, qtyy);

        spQty.setAdapter(adapter);

        String[] available = {list.getAvailiable()};
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, available);
        spAvailiable.setAdapter(adapter2);

        String[] category = {list.getCategory()};
        ArrayAdapter adapter3 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, category);
        spCategory.setAdapter(adapter3);


        ////spinner work
        String[] qtyyPrdct = {"1kg", "500gm", "250gm", "1pc", "1pkt", "1unit"};
        ArrayAdapter adapterQty = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, qtyyPrdct);

        spQty.setAdapter(adapterQty);


        String[] availableItem = {"Yes", "No"};
        ArrayAdapter adapterAvailable = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, availableItem);
        spAvailiable.setAdapter(adapterAvailable);


        String[] categoryyy = {"Vegetable", "Fruit", "Leafies", "Others", "Seasonal"};
        ArrayAdapter adapterCatergory = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryyy);
        spCategory.setAdapter(adapterCatergory);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 89 && !(data.getData() == null)) {
                imageUri = data.getData();
                Glide.with(this).load(imageUri).into(itemImage);

            }
        }
        if (resultCode == RESULT_CANCELED) {
            return;
        }

    }

    private void newAddItem() {
        // update.setVisibility(View.GONE);
        add.setVisibility(View.VISIBLE);
        itemName.setText("");

        marketprice.setText("");
        price.setText("");
        spinner("250gm", "Yes", "Vegetables");


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AdminHomeActivity.class));
        finish();
    }
}