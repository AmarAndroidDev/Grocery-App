package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryappp.Activity.Activity.SellerOrderDetailsActivity;
import com.example.groceryappp.Activity.Activity.UserOrderDetailsActivity;
import com.example.groceryappp.Activity.AllModel.UserOrderHeader;
import com.example.groceryappp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SellerOrderHeaderAdapter extends RecyclerView.Adapter<SellerOrderHeaderAdapter.ViewHolder> {
    ArrayList<UserOrderHeader> list;
    Context context;

    public SellerOrderHeaderAdapter(ArrayList<UserOrderHeader> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
   View view= LayoutInflater.from(context).inflate(R.layout.user_order_header_details,parent,false);
   return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserOrderHeader header=list.get(position);
holder.orderId.setText(header.getOrderId());
holder.name.setText(header.getName());
holder.number.setText(header.getNumber());
        if (header.getStatus()=="Processing"){
            holder.orderStaus.setTextColor(Color.BLUE);
            holder.orderStaus.setText(header.getStatus());
        } if (header.getStatus()=="Completed"){
            holder.orderStaus.setTextColor(Color.GREEN);
            holder.orderStaus.setText(header.getStatus());
        }if (header.getStatus()=="Cancel"){
            holder.orderStaus.setTextColor(Color.RED);
            holder.orderStaus.setText(header.getStatus());
        }
        else {
            holder.orderStaus.setTextColor(Color.BLACK);
            holder.orderStaus.setText(header.getStatus());
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        long data= Long.parseLong(header.getOrderId());
    String date=  formatter.format(data);
holder.date.setText(date+" "+"/"+" ");
holder.totalPrice.setText(header.getTotal());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId,name,number,date,totalPrice,orderStaus,viewall;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId=itemView.findViewById(R.id.oreder_id);
            name=itemView.findViewById(R.id.customer_name);
            number=itemView.findViewById(R.id.ph_no);
            date=itemView.findViewById(R.id.date);
            totalPrice=itemView.findViewById(R.id.total_price);
            orderStaus=itemView.findViewById(R.id.orderstatus);
            viewall=itemView.findViewById(R.id.viewallitem);

            viewall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, SellerOrderDetailsActivity.class);
                    intent.putExtra("orderId",list.get(getAdapterPosition()).getOrderId());
                    intent.putExtra("name",list.get(getAdapterPosition()).getName());
                    intent.putExtra("number",list.get(getAdapterPosition()).getNumber());
                    intent.putExtra("adress",list.get(getAdapterPosition()).getAdress());
                    intent.putExtra("orderstatus",list.get(getAdapterPosition()).getStatus());
                    intent.putExtra("uid",list.get(getAdapterPosition()).getUid());
                    intent.putExtra("latitude",list.get(getAdapterPosition()).getLattitude());
                    intent.putExtra("longitude",list.get(getAdapterPosition()).getLongitude());
                    intent.putExtra("imguri",list.get(getAdapterPosition()).getImgUri());
                    context.startActivity(intent);
                }
            });
        }
    }
}
