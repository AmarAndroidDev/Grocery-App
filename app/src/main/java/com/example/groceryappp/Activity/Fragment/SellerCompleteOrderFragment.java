package com.example.groceryappp.Activity.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.groceryappp.R;


public class SellerCompleteOrderFragment extends Fragment {



    public SellerCompleteOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
 View view= inflater.inflate(R.layout.fragment_seller_new_order, container, false);
 return view;
    }
}