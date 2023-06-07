package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.AllModel.AdminList;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProductActivity extends AppCompatActivity {
    ProgressDialog dialog;
    FirebaseFirestore database;
    FirebaseStorage storage;
    boolean value;
    Uri imageUri;
    AdminList listttt;
    AdminList adminList2;
    FirebaseAuth auth;
    ImageView itemImage;
    private Spinner spQty, spAvailiable, spCategory;
    private TextView txtQty;
    private EditText itemName, price, marketprice;
    private AppCompatButton update, add;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        getSupportActionBar().hide();
        viewInitialize();
        instanceCreated();
        onClick();
        spinner();
        gettingBundle();

        timestamp = String.valueOf(System.currentTimeMillis());

//        if (adminList2.getImgUri()==null){
//            Toast.makeText(this, "do", Toast.LENGTH_SHORT).show();
//        }else {
//            Glide.with(this).load(adminList2.getImgUri()).
//
//                    placeholder(R.drawable.product).into(itemImage);
//
//        }






     /*   String name=getIntent().getStringExtra("name");
        String qty=getIntent().getStringExtra("qty");
        String descc=getIntent().getStringExtra("desc");
        String pricee=getIntent().getStringExtra(*/
        if (value) {
            newAddItem();
        } else {
            updateItem();
        }
    }

    private void gettingBundle() {
        adminList2 = (AdminList) getIntent().getSerializableExtra("adminlist");
        value = getIntent().getBooleanExtra("add", false);
    }

    private void spinner() {
        ////spinner work
        String[] qtyy = {"250gm","500gm","1kg", "1pc", "1pkt","1unit"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, qtyy);

        spQty.setAdapter(adapter);

        String[] available = {"Yes", "No"};
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, available);
        spAvailiable.setAdapter(adapter2);

        String[] category = {"Vegetable", "Fruit", "Leafies", "Others","Seasonal"};
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
                addNewItemToDatabase(timestamp);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateItemToDatabase();
            }
        });
    }

    private void instanceCreated() {
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseFirestore.getInstance();
    }
    private void UpdateItemToDatabase() {

        listttt = new AdminList(itemName.getText().toString(), Integer.parseInt(price.getText().toString()), spQty.getSelectedItem().toString(), spAvailiable.getSelectedItem().toString()
                , spCategory.getSelectedItem().toString(), Integer.parseInt(marketprice.getText().toString()));

        final String timestamppp = adminList2.getId();
        listttt.setId(timestamppp);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Updating Item");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();
        listttt.setId(timestamppp);
        if (imageUri == null) {
            listttt.setImgUri(adminList2.getImgUri());
            database.collection("CurrentUser").document(auth.getUid()).collection("All Item").document(timestamppp).set(listttt).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.dismiss();
                    Toast.makeText(EditProductActivity.this, "Updated Sucessfuly", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProductActivity.this, AdminHomeActivity.class));
                    finish();
                }


            });




        }
        else {


            StorageReference reference = storage.getReference().child("Item Image").child(timestamppp);
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listttt.setImgUri(uri.toString());
                            database.collection("CurrentUser").document(auth.getUid()).collection("All Item").document(timestamppp).set(listttt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText(EditProductActivity.this, "Added Sucessfuly", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EditProductActivity.this, AdminHomeActivity.class));
                                    finish();
                                }
                            });
                        }
                    });

                }
            });

        }

    }

    private void addNewItemToDatabase(String timestampp) {
        if (itemName.getText().toString().isEmpty() && price.getText().toString().isEmpty() && marketprice.getText().toString().isEmpty()) {
            Toast.makeText(EditProductActivity.this, "Please Provide All Details", Toast.LENGTH_SHORT).show();
        }
        if (imageUri == null) {
            dialog = new ProgressDialog(this);
            dialog.setTitle("Adding Item");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.show();

            AdminList listttt2 = new AdminList(itemName.getText().toString(),Integer.parseInt(price.getText().toString()),  spQty.getSelectedItem().toString(), spAvailiable.getSelectedItem().toString()
                    , spCategory.getSelectedItem().toString(), Integer.parseInt(marketprice.getText().toString()));
            listttt2.setId(timestampp);

            database.collection("CurrentUser").document(auth.getUid()).collection("All Item").document(timestampp).set(listttt2).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    itemName.setText("");

                    marketprice.setText("");
                    price.setText("");
                    Toast.makeText(EditProductActivity.this, "Added Sucesfuly", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProductActivity.this, AdminHomeActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            dialog = new ProgressDialog(this);
            dialog.setTitle("Adding Item");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.show();
            StorageReference reference = storage.getReference().child("Item Image").child(timestampp);
            AdminList listttt3 = new AdminList(itemName.getText().toString(), Integer.parseInt(price.getText().toString()), spQty.getSelectedItem().toString(), spAvailiable.getSelectedItem().toString()
                    , spCategory.getSelectedItem().toString(), Integer.parseInt(marketprice.getText().toString()));
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
                                    dialog.dismiss();
                                    Toast.makeText(EditProductActivity.this, "Added Sucessfuly", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EditProductActivity.this, AdminHomeActivity.class));
                                    finish();
                                }
                            });
                        }
                    });

                }
            });


        }
    }


    private void viewInitialize() {
        update = findViewById(R.id.update_item);
        add = findViewById(R.id.add_new_Item);
        spAvailiable = findViewById(R.id.sp_available);
        spCategory = findViewById(R.id.sp_category);
        itemName = findViewById(R.id.item_name);
        spQty = findViewById(R.id.sp_qty);

        price = findViewById(R.id.item_price);
        marketprice = findViewById(R.id.market_price);
        itemImage = findViewById(R.id.item_image);


    }

    private void updateItem() {
        update.setVisibility(View.VISIBLE);
        add.setVisibility(View.GONE);
        itemName.setText(adminList2.getName());

        marketprice.setText(""+adminList2.getMarktPrice());
        price.setText(""+adminList2.getPrice());

        price.setText(""+adminList2.getPrice());


     //   Uri uri = Uri.parse(adminList2.getImgUri());
      /*  Glide.with(this).load(adminList2.getImgUri()).into(itemImage);

        String[] qtyy = {adminList2.getQty()};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, qtyy);

        spQty.setAdapter(adapter);

        String[] available = {adminList2.getAvailiable()};
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, available);
        spAvailiable.setAdapter(adapter2);

        String[] category = {adminList2.getCategory()};
        ArrayAdapter adapter3 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, category);
        spCategory.setAdapter(adapter3);*/


        ////spinner work
        String[] qtyy = {"250gm","500gm","1kg", "1pc", "1pkt","1unit"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, qtyy);

        spQty.setAdapter(adapter);


        String[] available = {"Yes", "No"};
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, available);
        spAvailiable.setAdapter(adapter2);


        String[] category = {"Vegetable", "Fruit", "Leafies", "Others","Seasonal"};
        ArrayAdapter adapter3 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, category);
        spCategory.setAdapter(adapter3);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 89 && !(data.getData() == null)) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(itemImage);

        }
        else {
            return;
        }

    }

    private void newAddItem() {
        update.setVisibility(View.GONE);
        add.setVisibility(View.VISIBLE);
        itemName.setText("");

        marketprice.setText("");
        price.setText("");


    }

}