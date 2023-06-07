package com.example.groceryappp.Activity.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.groceryappp.Activity.Fragment.CartFragment;
import com.example.groceryappp.Activity.Fragment.UserHomeFragment;

public class UserHomeFragmentAdapter extends FragmentStateAdapter {
    public UserHomeFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    String[] title=new String[]{"Home","Cart","Order","Profile"};

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       switch (position){
           case 0:return new UserHomeFragment();
           case 1:return new CartFragment();
       }

        return new UserHomeFragment();
    }

    @Override
    public int getItemCount() {
        return title.length;
    }
}
