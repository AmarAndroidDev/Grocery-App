package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryappp.Activity.Activity.UserOrderDetailsActivity;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class UserOrderHeaderAdapter extends RecyclerView.Adapter<UserOrderHeaderAdapter.ViewHolder> {
    private ArrayList<UserInfo> list;
    private String checkingUserOrSeller;
    private Context context;
    private UserInfo header;
    private String Date;
    String orderstatus;

    public UserOrderHeaderAdapter(ArrayList<UserInfo> list, Context context, String checkingUserOrSeller) {
        this.list = list;
        this.context = context;
        this.checkingUserOrSeller = checkingUserOrSeller;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_order_header_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        header = list.get(holder.getAdapterPosition());
        holder.orderId.setText(header.getOrderId());
        holder.name.setText(header.getName());
        holder.number.setText(header.getNumber());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        long data = Long.parseLong(header.getOrderId());
        Date = formatter.format(data);
        holder.date.setText(Date + " " + "/");
        holder.totalPrice.setText(String.valueOf(" ₹" + header.getSum()));
     orderstatus = header.getStatus();

        if (orderstatus.equalsIgnoreCase("order packed")) {
            holder.orderStaus.setText("Packed");
            holder.orderStaus.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFA000")));


        } else if (orderstatus.equalsIgnoreCase("order delivered")) {
            holder.orderStaus.setText("Delivered");
            holder.orderStaus.setTextColor(Color.GREEN);

        } else if (orderstatus.equalsIgnoreCase("order cancel")) {

            holder.orderStaus.setText("Canceled");
            holder.orderStaus.setTextColor(Color.RED);

        }  else if (orderstatus.equalsIgnoreCase("out for delivery")) {

            holder.orderStaus.setText("0ut For Delivery");
            holder.orderStaus.setTextColor(Color.GREEN);

        } else {
            holder.orderStaus.setText("In Progress");

        }
holder.viewDetails.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, UserOrderDetailsActivity.class);
        header.setDate(Date);
        header.setStatus(list.get(holder.getAdapterPosition()).getStatus());
        intent.putExtra("orderDetails", list.get(holder.getAdapterPosition()));
        intent.putExtra("checkingUserOrSeller", checkingUserOrSeller);
        context.startActivity(intent);


    }
});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, name, number, date, totalPrice, orderStaus, viewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.oreder_id);
            name = itemView.findViewById(R.id.customer_name);
            number = itemView.findViewById(R.id.ph_no);
            date = itemView.findViewById(R.id.date);
            totalPrice = itemView.findViewById(R.id.total_price);
            orderStaus = itemView.findViewById(R.id.orderstatus);
            viewDetails = itemView.findViewById(R.id.viewallitem);


        }
    }
}
