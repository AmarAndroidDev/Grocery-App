package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.groceryappp.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<Integer> list;

    public ViewPagerAdapter(Context context, ArrayList<Integer> list) {
        this.context = context;
        this.list = list;
    }



    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
        //here we pass object into view
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(context).inflate(R.layout.viewpager,container,false);
        ImageView imageView=view.findViewById(R.id.imageimage);

        imageView.setImageResource(list.get(position));

        container.addView(view);
        return view;
    }

}
