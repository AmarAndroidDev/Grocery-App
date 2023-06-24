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

import com.example.groceryappp.Activity.Activity.UserOrderDetailsActivity;
import com.example.groceryappp.Activity.AllModel.UserOrderHeader;
import com.example.groceryappp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UserOrderHeaderAdapter extends RecyclerView.Adapter<UserOrderHeaderAdapter.ViewHolder> {
    ArrayList<UserOrderHeader> list;
    Context context;
    UserOrderHeader header;
    String Date;
    public UserOrderHeaderAdapter(ArrayList<UserOrderHeader> list, Context context) {
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
      header = list.get(position);
        holder.orderId.setText(header.getOrderId());
        holder.name.setText(header.getName());
        holder.number.setText(header.getNumber());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        long data = Long.parseLong(header.getOrderId());
    Date= formatter.format(data);
        holder.date.setText(Date+" "+"/" );
        holder.totalPrice.setText(String.valueOf(" ₹"+header.getTotal()));
        String orderstatus = header.getStatus();
        if (orderstatus.equalsIgnoreCase("Processing")) {
            holder.orderStaus.setText(orderstatus);
            holder.orderStaus.setTextColor(Color.BLUE);

        } else if (orderstatus.equalsIgnoreCase("Completed")) {
            holder.orderStaus.setText(orderstatus);
            holder.orderStaus.setTextColor(Color.GREEN);

        } else if (orderstatus.equalsIgnoreCase("Cancel")) {

            holder.orderStaus.setText(orderstatus);
            holder.orderStaus.setTextColor(Color.RED);

        }else {
            holder.orderStaus.setText("InProgress");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId,name,number,date,totalPrice,orderStaus,viewDetails;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId=itemView.findViewById(R.id.oreder_id);
            name=itemView.findViewById(R.id.customer_name);
            number=itemView.findViewById(R.id.ph_no);
            date=itemView.findViewById(R.id.date);
            totalPrice=itemView.findViewById(R.id.total_price);
            orderStaus=itemView.findViewById(R.id.orderstatus);
            viewDetails=itemView.findViewById(R.id.viewallitem);

            viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, UserOrderDetailsActivity.class);
                    intent.putExtra("orderId",list.get(getAdapterPosition()).getOrderId());
                    intent.putExtra("order status",list.get(getAdapterPosition()).getStatus());
                    intent.putExtra("sum",header.getTotal());
                    intent.putExtra("date",Date);
intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);


                }
            });
        }
    }
}
