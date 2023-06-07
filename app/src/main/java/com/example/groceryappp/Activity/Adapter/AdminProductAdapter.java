package com.example.groceryappp.Activity.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Activity.EditProductActivity;
import com.example.groceryappp.Activity.AllModel.AdminList;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {
    private ArrayList<AdminList> lists;
    Context context;
   // boolean available=false;
    FirebaseAuth auth;
    FirebaseFirestore database;

    public AdminProductAdapter(ArrayList<AdminList> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }
    public void setFilterList(ArrayList<AdminList> fileterList){
        lists=fileterList;
    notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(context).inflate(R.layout.admin_single_prdct,parent,false);
    return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        auth=FirebaseAuth.getInstance();
        database=FirebaseFirestore.getInstance();

        AdminList adminListt=lists.get(holder.getAdapterPosition());
        Glide.with(context).load(lists.get(position).getImgUri()).
        placeholder(R.drawable.product).
                into(holder.img);
holder.title.setText(lists.get(position).getName());
holder.category.setText(lists.get(position).getCategory());
holder.available.setText(lists.get(position).getAvailiable());
float discountPrice=lists.get(position).getMarktPrice()-lists.get(position).getPrice();
float offerPercent= discountPrice*100/lists.get(position).getMarktPrice();
holder.offerPercentage.setText(Math.round(offerPercent)+"% Off");

holder.price.setText("₹"+lists.get(position).getPrice());
holder.qty.setText(lists.get(position).getQty());
String availableProduct=lists.get(position).getAvailiable();
if (availableProduct.equals("Yes")){
    holder.available.setTextColor(Color.GREEN);
    holder.notAvailable.setVisibility(View.GONE);

}else {
    holder.notAvailable.setVisibility(View.VISIBLE);
    holder.available.setTextColor(Color.RED);



}


holder.edit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(context, EditProductActivity.class);
       /* intent.putExtra("name",lists.get(holder.getAdapterPosition()).getName());
        intent.putExtra("desc",lists.get(holder.getAdapterPosition()).getDesc());
        intent.putExtra("qty",lists.get(holder.getAdapterPosition()).getQty());
        intent.putExtra("price",lists.get(holder.getAdapterPosition()).getPrice());*/

        intent.putExtra("adminlist",adminListt);
        context.startActivity(intent);
    }
});
holder.delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        AlertDialog.Builder dialog=new AlertDialog.Builder(context);

        dialog.setMessage("Are You Sure Want To Delete");
        dialog.setTitle("Delete");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                database.collection("CurrentUser").document(auth.getUid()).collection("All Item").document(adminListt.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyItemChanged(holder.getAdapterPosition());

                    }

                });
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();

    }
});
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
     ImageView img,edit,delete;

     TextView title,category,qty,price,notAvailable,available,offerPercentage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.veg_img_prdct);
            delete=itemView.findViewById(R.id.delete_prdct);
            edit=itemView.findViewById(R.id.edit_prdct);
            title=itemView.findViewById(R.id.title);
            available=itemView.findViewById(R.id.available);
            category=itemView.findViewById(R.id.category);
            qty=itemView.findViewById(R.id.qtyy);
            price=itemView.findViewById(R.id.price);
            notAvailable=itemView.findViewById(R.id.not_available);
            offerPercentage=itemView.findViewById(R.id.offer_percentage);





        }
    }
}
