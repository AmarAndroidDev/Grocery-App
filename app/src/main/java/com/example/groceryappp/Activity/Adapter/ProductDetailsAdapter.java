package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryappp.Activity.Activity.ViewAllActivity;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {
    Context context;
    FirebaseFirestore database;
    FirebaseAuth auth;
    ArrayList<SingleProductDetails> list;
    int totalquantity=1;

 int totalprice=0;

    public ProductDetailsAdapter(Context context, ArrayList<SingleProductDetails> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_prdct_detatils, parent, false);
        return new ProductDetailsAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductDetailsAdapter.ViewHolder holder, int position) {
SingleProductDetails details=list.get(holder.getAdapterPosition());


        database=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        holder.title.setText(details.getName());
        holder.price.setText((details.getPrice()));
        holder.quantity.setText(details.getQty());
        holder.profile_pic.setImageResource(details.getProfile_pic());
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> map=new HashMap<>();
                map.put("title",list.get(holder.getAdapterPosition()).getName());
                map.put("qty",holder.quantityCart.getText().toString());
                map.put("totalprice",list.get(holder.getAdapterPosition()).getPrice());
                map.put("isAdd",list.get(holder.getAdapterPosition()).getType());
                database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        ////update
                        holder.plusCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (totalquantity<10){
                                    totalquantity++;
                                    holder.quantityCart.setText(String.valueOf(totalquantity));
                                    int price= Integer.parseInt(String.valueOf(list.get(holder.getAdapterPosition()).getPrice()));
                                    totalprice= totalquantity *price;

                    Map<String,Object> map2=new HashMap<>();

                    map2.put("qty",holder.quantityCart.getText().toString());
                    map2.put("totalprice",totalprice);
                                    database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").document(task.getResult().getId()).update("qty",holder.quantityCart.getText().toString(),"totalprice",totalprice).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                                    holder.minusCart.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {

                                            if (totalquantity>0){
                                                totalquantity--;
                                                totalprice= totalquantity *price;
                                                holder.quantityCart.setText(String.valueOf(totalquantity));
                                                database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").document(task.getResult().getId()).update("qty",holder.quantityCart.getText().toString(),"totalprice",totalprice).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });


                                            }
                                            if (totalquantity==0){

                                                database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").document(task.getResult().getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                                holder.addToCart.setVisibility(View.VISIBLE);
                                                holder.cartlayout.setVisibility(View.GONE);
                                            }

                                        }
                                    });


                                }


                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                holder.addToCart.setVisibility(View.GONE);
                holder.cartlayout.setVisibility(View.VISIBLE);
                holder.quantityCart.setText("1");



            }
        });



    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cartlayout,addToCart;
        TextView title;

        TextView price,quantityCart;
        TextView quantity;
        ImageView wishlist
                ,profile_pic,minusCart,plusCart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic=itemView.findViewById(R.id.veg_imgprdct);
            title=itemView.findViewById(R.id.title);
            quantity =itemView.findViewById(R.id.qty_desc);
            price=itemView.findViewById(R.id.price);
            minusCart=itemView.findViewById(R.id.minus_cartprdct);
            plusCart=itemView.findViewById(R.id.plus_cartprdct);
            quantityCart=itemView.findViewById(R.id.quantityprdct);
            cartlayout=itemView.findViewById(R.id.cart_layouttprdct);
            addToCart=itemView.findViewById(R.id.add_to_cartprdct);





        }
    }
}
