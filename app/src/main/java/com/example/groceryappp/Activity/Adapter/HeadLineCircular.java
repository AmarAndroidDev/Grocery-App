package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Activity.ViewAllActivity;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HeadLineCircular extends RecyclerView.Adapter<HeadLineCircular.ViewHolder> {
    private ArrayList<SingleProductDetails> list;
    private Context context;


    public HeadLineCircular(ArrayList<SingleProductDetails> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.bg_headline_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(list.get(position).getName());
        Glide.with(context).load(list.get(position).getImg()).into(holder.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewAllActivity.class);
                intent.putExtra("type", list.get(holder.getAdapterPosition()).getType());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.profile_title);
            profile = itemView.findViewById(R.id.profile);


        }

    }
}

