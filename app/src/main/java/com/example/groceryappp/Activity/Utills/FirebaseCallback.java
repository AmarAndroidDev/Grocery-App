package com.example.groceryappp.Activity.Utills;

import android.content.Context;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public interface FirebaseCallback {
    void onCallback(  ArrayList<SingleProductDetails> productDetails );
    void onCallbackInfo(  ArrayList<UserInfo> info );


    static void fetchProductDetailsFromFirebase(String authId, String key, Context context, FirebaseCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("CurrentUser")
                .document(authId)
                .collection(key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<SingleProductDetails> productDetails = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                SingleProductDetails  details = snapshot.toObject(SingleProductDetails.class);
                                productDetails.add(details);

                            }
                        } else {
                            // Handle the error
                            Toast.makeText(context, "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // Invoke the callback with the populated details ArrayList
                        callback.onCallback(productDetails);
                    }
                });


    }
     static void fetchUserInfoFromFirebase(String authId, String key, Context context, FirebaseCallback callbackInfo) {
        FirebaseFirestore.getInstance()
                .collection("CurrentUser")
                .document(authId)
                .collection(key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<UserInfo> info = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                UserInfo  details = snapshot.toObject(UserInfo.class);
                                info.add(details);

                            }
                        } else {
                            // Handle the error
                            Toast.makeText(context, "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // Invoke the callback with the populated details ArrayList
                        callbackInfo.onCallbackInfo(info);
                    }
                });


    }

}

