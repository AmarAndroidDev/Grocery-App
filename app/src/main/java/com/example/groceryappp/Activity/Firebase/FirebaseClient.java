package com.example.groceryappp.Activity.Firebase;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public  class FirebaseClient {
    private static FirebaseFirestore instance;
    public static ArrayList<SingleProductDetails> list=new ArrayList<>();

    private FirebaseClient() {
        // Private constructor to prevent instantiation from outside the class
    }

    public static synchronized FirebaseFirestore getInstance() {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance();
        }
        return instance;

    }

}
