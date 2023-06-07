package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryappp.Activity.AllModel.AdminOrderDetails;
import com.example.groceryappp.Activity.AllModel.SingleOrderDetails;
import com.example.groceryappp.R;

import java.util.ArrayList;

public class AdminOrderDetailsAdapter extends RecyclerView.Adapter<AdminOrderDetailsAdapter.ViewHolder> {
    ArrayList<AdminOrderDetails> list;
    Context context;

    public AdminOrderDetailsAdapter(ArrayList<AdminOrderDetails> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view= LayoutInflater.from(context).inflate(R.layout.admin_order_detailssss,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminOrderDetails details=list.get(holder.getAdapterPosition());
holder.date.setText(details.getDate());
holder.itemqty.setText(details.getPqty());
holder.itemname.setText(details.getPname());
holder.totalprice.setText(details.getPtotalprice());
holder.orderid.setText(details.getOrderId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView itemname,orderid,date,itemqty,totalprice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemname=itemView.findViewById(R.id.itemName);
            orderid=itemView.findViewById(R.id.orderId);
            date=itemView.findViewById(R.id.date);
            itemqty=itemView.findViewById(R.id.itemQty);
            totalprice=itemView.findViewById(R.id.totalPrice);
        }
    }
}
