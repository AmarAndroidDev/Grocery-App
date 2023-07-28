package com.example.groceryappp.Activity.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Activity.AdminHomeActivity;
import com.example.groceryappp.Activity.Activity.AdminProductActivity;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminProductListAdapter extends RecyclerView.Adapter<AdminProductListAdapter.ViewHolderProdcut> {
    ArrayList<SingleProductDetails> list;

    public ArrayList<SingleProductDetails> getList() {
        return list;
    }

    public void setList(ArrayList<SingleProductDetails> list) {
        this.list = list;
    }

    Context context;

    private String userId;

    public AdminProductListAdapter(ArrayList<SingleProductDetails> list, Context context) {
        this.list = list;
        this.context = context;
    }



    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AdminProductListAdapter.ViewHolderProdcut onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(context).inflate(R.layout.adminallproductlist, parent, false);
    return new ViewHolderProdcut(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductListAdapter.ViewHolderProdcut holder, int position) {
SingleProductDetails list1=list.get(position);

        SharedPreferenceManager sharedPreferenceManager= SharedPreferenceManager.getInstance(context);
        SharedPreferences preferences=context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        if (sharedPreferenceManager != null && preferences.contains("USER_ID")) {
            userId=SharedPreferenceManager.getInstance(context).getUserId();
        }

        Glide.with(context).load(list1.getImgUri()).into(holder.prdctImg);
        holder.prdctName.setText(list1.getName());
        holder.prdctMarketP.setText("₹" +""+list1.getMarktPrice());
        holder.prdctPrice.setText("₹" +""+list1.getPrice());
        holder.prdctQty.setText(list1.getQty());
        float discountPrice = list.get(position).getMarktPrice() - list.get(position).getPrice();
        float offerPercent = discountPrice * 100 / list.get(position).getMarktPrice();
        holder.offerPrice.setText(Math.round(offerPercent) + "% Off");
holder.editProduct.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(context, AdminProductActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("list", list1);
        context.startActivity(intent);

    }
});
holder.deleteProduct.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("Delete Product").setMessage("Are you sure ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ProgressDialog dialog=new ProgressDialog(context);
                dialog.setMessage("Deleting Product");
                dialog.setCancelable(false);
                dialog.show();
                FirebaseClient.getInstance().collection("CurrentUser").document(userId).collection("All Item").document(list1.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                     dialog.dismiss();
                        Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getContext(), AdminHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }
});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderProdcut extends RecyclerView.ViewHolder {
        ImageView prdctImg,editProduct,deleteProduct;
        TextView prdctName,prdctMarketP,prdctPrice,prdctQty,offerPrice;
        public ViewHolderProdcut(@NonNull View itemView) {
         
            super(itemView);
            prdctImg=itemView.findViewById(R.id.prdct_img);
            editProduct=itemView.findViewById(R.id.editProduct);
            deleteProduct=itemView.findViewById(R.id.deleteProduct);
            prdctName=itemView.findViewById(R.id.prdct_name);
            prdctMarketP=itemView.findViewById(R.id.marketPrice);
            prdctPrice=itemView.findViewById(R.id.price);
            prdctQty=itemView.findViewById(R.id.qty_desc);
            offerPrice=itemView.findViewById(R.id.offer_price);
        }
    }
}
