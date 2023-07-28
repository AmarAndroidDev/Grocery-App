package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.groceryappp.Activity.AllModel.ViewPagerModel;
import com.example.groceryappp.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<ViewPagerModel> list;

    public ViewPagerAdapter(Context context, ArrayList<ViewPagerModel> list) {
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
        TextView title=view.findViewById(R.id.title);

        imageView.setImageResource(list.get(position).getImg());

title.setText(list.get(position).getTitle());
        container.addView(view);
        return view;
    }

}
