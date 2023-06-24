package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Activity.ViewAllActivity;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {
    Context context;
   int listSize;
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
        SingleProductDetails details = list.get(holder.getAdapterPosition());
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        holder.title.setText(details.getName());
        holder.price.setText(("₹" + details.getPrice()));
        holder.marketPrice.setText(("₹" + details.getMarktPrice()));
        Glide.with(context).load(details.getImgUri()).into(holder.profile_pic);
        holder.quantity.setText(details.getQty());

        ///////////////feteching cart details
        database.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("cart").document(details.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().get("qty")!=null){
                            holder.addToCart.setVisibility(View.GONE);
                            holder.cartlayout.setVisibility(View.VISIBLE);
                            holder.quantityCart.setText(task.getResult().get("unit").toString());

                        }
                    }
                });
   /*     holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalprice = Integer.parseInt(String.valueOf(details.getPrice())) * totalquantity;
                holder.addToCart.setVisibility(View.GONE);
                holder.cartlayout.setVisibility(View.VISIBLE);

                details.setId(list.get(holder.getAdapterPosition()).getId());
                details.setTotalprice("" + totalprice);
                details.setImgUri(details.getImgUri());
                details.setQty(details.getQty());
                details.setUnit("" + totalquantity);
                details.setMarktPrice(details.getMarktPrice());


                database.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("cart").document(list.get(holder.getAdapterPosition()).getId()).set(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });



                notifyDataSetChanged();

             database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listSize = queryDocumentSnapshots.getDocuments();
                        Log.e("GURU", ""+listSize.size() );
                    }
                });
                Intent intent6 = new Intent("Size");
                intent6.putExtra("size", listSize.size());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent6);

            }
        });*/
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalprice = Integer.parseInt(String.valueOf(details.getPrice())) * totalquantity;
                holder.addToCart.setVisibility(View.GONE);
                holder.cartlayout.setVisibility(View.VISIBLE);

                details.setId(list.get(holder.getAdapterPosition()).getId());
                details.setTotalprice(totalprice);
                details.setImgUri(details.getImgUri());
                details.setQty(details.getQty());
                details.setUnit(totalquantity);
                details.setMarktPrice(details.getMarktPrice());


                database.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("cart").document(list.get(holder.getAdapterPosition()).getId()).set(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                notifyDataSetChanged();
                database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listSize = queryDocumentSnapshots.getDocuments().size();
                        Intent intentSize = new Intent("Size");
                        intentSize.putExtra("size", listSize);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intentSize);
                    }
                });





            }
        });

        ////increase cart
        holder.plusCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] qtyyy = new int[]{Integer.parseInt(holder.quantityCart.getText().toString())};
                if (qtyyy[0] <10){
                    qtyyy[0]++;
                    holder.quantityCart.setText(String.valueOf(qtyyy[0]));

                    totalprice=Integer.parseInt(String.valueOf(list.get(holder.getAdapterPosition()).getPrice())) *qtyyy[0];
                    database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").document(list.get(holder.getAdapterPosition()).getId())
                            .update("unit",qtyyy[0],"totalprice",totalprice).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {



                                }

                            });


                }
            }
        });

        holder.minusCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] qtyyy = new int[]{Integer.parseInt(holder.quantityCart.getText().toString())};
                if (qtyyy[0] > 0) {
                    qtyyy[0]--;
                    holder.quantityCart.setText(String.valueOf(qtyyy[0]));
                    totalprice = Integer.parseInt(String.valueOf(list.get(holder.getAdapterPosition()).getPrice())) * qtyyy[0];
                    database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").document(list.get(holder.getAdapterPosition()).getId())
                            .update("unit", qtyyy[0], "totalprice", totalprice).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                }

                if (qtyyy[0] == 0) {
                    database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").document(list.get(holder.getAdapterPosition()).getId())
                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.cartlayout.setVisibility(View.GONE);
                                    holder.addToCart.setVisibility(View.VISIBLE);
                                    holder.quantityCart.setText("1");

                                }
                            });
                    notifyDataSetChanged();
/*

                    Intent intent5 = new Intent("CartSize");
                    intent5.putExtra("cartsize", String.valueOf(details1));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent5);
*//*
                    notifyDataSetChanged();
                    database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            listSize = queryDocumentSnapshots.getDocuments();
                        }
                    });
                    Intent intent6 = new Intent("CartSizeMinus");
                    intent6.putExtra("cartsizeminus", listSize.size());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent6);




                }

            }
        });*/


                }

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

        TextView price,quantityCart,marketPrice;
        TextView quantity;
        ImageView wishlist
                ,profile_pic,minusCart,plusCart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic=itemView.findViewById(R.id.veg_imgprdct);
            title=itemView.findViewById(R.id.title);
            quantity =itemView.findViewById(R.id.qty_desc);
            price=itemView.findViewById(R.id.price);
            marketPrice=itemView.findViewById(R.id.marketPrice);
            minusCart=itemView.findViewById(R.id.minus_cartprdct);
            plusCart=itemView.findViewById(R.id.plus_cartprdct);
            quantityCart=itemView.findViewById(R.id.quantityprdct);
            cartlayout=itemView.findViewById(R.id.cart_layouttprdct);
            addToCart=itemView.findViewById(R.id.add_to_cartprdct);





        }
    }
}
