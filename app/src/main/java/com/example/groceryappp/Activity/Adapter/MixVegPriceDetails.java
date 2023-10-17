package com.example.groceryappp.Activity.Adapter;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MixVegPriceDetails extends RecyclerView.Adapter<MixVegPriceDetails.ViewHolder> {
    List<DocumentSnapshot> listSizeMinus, listSize;
    ArrayList<SingleProductDetails> list;
    int totalprice = 0, quant, totalquantity = 1;
    private Context context;
    private FirebaseFirestore database;


    private String userId;

    public MixVegPriceDetails(Context context, ArrayList<SingleProductDetails> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.singel_price_details, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        listSize = new ArrayList<>();
        listSizeMinus = new ArrayList<>();
        database = FirebaseFirestore.getInstance();

        SharedPreferenceManager sharedPreferenceManager= SharedPreferenceManager.getInstance(context);
        SharedPreferences preferences=context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        if (sharedPreferenceManager != null && preferences.contains("USER_ID")) {
            userId=SharedPreferenceManager.getInstance(context).getUserId();
            holder.addToCart.setEnabled(true);
        }          SingleProductDetails details = list.get(holder.getAdapterPosition());
        float discountPrice = list.get(position).getMarktPrice() - list.get(position).getPrice();
        float offerPercent = discountPrice * 100 / list.get(position).getMarktPrice();
        holder.offerPrice.setText(Math.round(offerPercent) + "% Off");

        if (details.getName().length()>8){
            holder.title.setText(details.getName().substring(0,6)+"..");
        }else {
            holder.title.setText(details.getName());
        }
        holder.price.setText("₹" + (details.getPrice()));
        holder.quantity.setText("(" + details.getQty() + ")");
        holder.marketPrice.setText("₹" + details.getMarktPrice());

        Glide.with(context).load(details.getImgUri()).into(holder.profile_pic);


        if (list.get(position).getAvailiable().equalsIgnoreCase("no")){
            holder.addToCart.setEnabled(false);
            holder.addToCart.setText("Not Available");
            holder.addToCart.setAllCaps(false);
            holder.addToCart.setTextColor(Color.RED);
            holder.addToCart.setTextSize(12);
        }

        SingleProductDetails details1 = new SingleProductDetails(details.getName(), details.getPrice());
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalprice = Integer.parseInt(String.valueOf(details.getPrice())) * totalquantity;
                holder.addToCart.setVisibility(View.GONE);
                holder.cartlayout.setVisibility(View.VISIBLE);
                details1.setId(list.get(holder.getAdapterPosition()).getId());
                details1.setTotalprice(totalprice);
                details1.setImgUri(details.getImgUri());
                details1.setQty(details.getQty());
                details1.setUnit(totalquantity);
                details1.setMarktPrice(details.getMarktPrice());
                database.collection("CurrentUser").document(userId)
                        .collection("cart").document(list.get(holder.getAdapterPosition()).getId()).set(details1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                notifyDataSetChanged();
                database.collection("CurrentUser").document(userId).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listSize = queryDocumentSnapshots.getDocuments();
                    }
                });
                Intent intent5 = new Intent("CartSize");
                intent5.putExtra("cartsize", listSize.size());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent5);

            }
        });

if (userId!=null){
    ///////////////feteching cart details
    database.collection("CurrentUser").document(userId)
            .collection("cart").document(details.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().get("qty") != null) {
                        holder.addToCart.setVisibility(View.GONE);
                        holder.cartlayout.setVisibility(View.VISIBLE);
                        holder.quantityCart.setText(task.getResult().get("unit").toString());
                        quant = Integer.parseInt(task.getResult().get("unit").toString());
                    }
                }
            });

}else {
    holder.addToCart.setEnabled(false);
}

        ////increase cart
        holder.plusCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] qtyyy = new int[]{Integer.parseInt(holder.quantityCart.getText().toString())};
                if (qtyyy[0] < 10) {
                    qtyyy[0]++;
                    holder.quantityCart.setText(String.valueOf(qtyyy[0]));
                    totalprice = Integer.parseInt(String.valueOf(list.get(holder.getAdapterPosition()).getPrice())) * qtyyy[0];
                    database.collection("CurrentUser").document(userId).collection("cart").document(list.get(holder.getAdapterPosition()).getId())
                            .update("unit", qtyyy[0], "totalprice", totalprice).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                ArrayList<SingleProductDetails> cartList=new ArrayList<>();
                cartList.add(list.get(holder.getAdapterPosition()));
                final int[] qtyyy = new int[]{Integer.parseInt(holder.quantityCart.getText().toString())};
                if (qtyyy[0] > 0) {
                    qtyyy[0]--;
                    holder.quantityCart.setText(String.valueOf(qtyyy[0]));
                    totalprice = Integer.parseInt(String.valueOf(list.get(holder.getAdapterPosition()).getPrice())) * qtyyy[0];
                    database.collection("CurrentUser").document(userId).collection("cart").document(list.get(holder.getAdapterPosition()).getId())
                            .update("unit", qtyyy[0], "totalprice", totalprice).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                }

                if (qtyyy[0] == 0) {
                    database.collection("CurrentUser").document(userId).collection("cart").document(list.get(holder.getAdapterPosition()).getId())
                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.cartlayout.setVisibility(View.GONE);
                                    holder.addToCart.setVisibility(View.VISIBLE);
                                    holder.quantityCart.setText("1");

                                }
                            });
                    notifyDataSetChanged();
                    database.collection("CurrentUser").document(userId).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
        });
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout cartlayout;
        private TextView price, quantityCart, marketPrice, title, offerPrice, quantity,notAvailable,addToCart;
        private ImageView profile_pic, minusCart, plusCart;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic = itemView.findViewById(R.id.veg_img);
            title = itemView.findViewById(R.id.title_veg);
            quantity = itemView.findViewById(R.id.qty_veg);
            price = itemView.findViewById(R.id.price_veg);
            minusCart = itemView.findViewById(R.id.minus_cart);
            plusCart = itemView.findViewById(R.id.plus_cart);
            quantityCart = itemView.findViewById(R.id.quantity);
            cartlayout = itemView.findViewById(R.id.cart_layout);
            addToCart = itemView.findViewById(R.id.add_to_cart);
            offerPrice = itemView.findViewById(R.id.offer_price);
            marketPrice = itemView.findViewById(R.id.marketPrice);

            notAvailable = itemView.findViewById(R.id.not_available);
        }
    }
}
