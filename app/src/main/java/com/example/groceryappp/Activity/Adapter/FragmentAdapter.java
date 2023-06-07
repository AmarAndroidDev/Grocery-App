package com.example.groceryappp.Activity.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.groceryappp.Activity.Fragment.SellerOrderFragment;
import com.example.groceryappp.Activity.Fragment.PrdouctFragment;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    String[] title=new String[]{"PRODUCT","ORDER","PROFILE"};

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PrdouctFragment();
            case 1:
                return new SellerOrderFragment();
                case 2:


            default:
                return new PrdouctFragment();
        }
    }
    @Override
    public int getItemCount() {
        return title.length ;
    }
}
