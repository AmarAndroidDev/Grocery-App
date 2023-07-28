package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<SingleProductDetails> list;
    private int plusMarketPrice = 0, sumPlus = 0, totalMarketPrice = 0, minusMarketPrice = 0, sumMinus = 0, finalPrice = 0, sum = 0, totalPrice;
    private FirebaseFirestore database;

    int type;
    private String userId;
 public   static int VIEW_TYPE_SINGLE_CART = 1;
public static int VIEW_TYPE_SINGLE_ORDERS = 2;


    public CartAdapter(Context context, ArrayList<SingleProductDetails> list,int type) {
        this.context = context;
        this.list = list;
        this.type=type;
    } public CartAdapter(Context context, ArrayList<SingleProductDetails> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (type == VIEW_TYPE_SINGLE_CART) {
            View v = LayoutInflater.from(context).inflate(R.layout.singlecartdetails, parent, false);
            return new CartViewHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.singel_order_show, parent, false);
            return new OrderViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SharedPreferenceManager sharedPreferenceManager= SharedPreferenceManager.getInstance(context);
        SharedPreferences preferences=context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        if (sharedPreferenceManager != null && preferences.contains("USER_ID")) {
            userId=SharedPreferenceManager.getInstance(context).getUserId();
        }
        if (holder.getClass() == CartViewHolder.class) {
            database = FirebaseFirestore.getInstance();

            SingleProductDetails details = list.get(holder.getAdapterPosition());
            Glide.with(context).load(details.getImgUri()).into(((CartViewHolder) holder).img);
            ((CartViewHolder) holder).unit.setText(String.valueOf(details.getUnit()) + " Unit");
            ((CartViewHolder) holder).qty.setText(String.valueOf(details.getQty()) + "/");
            ((CartViewHolder) holder).quantityCart.setText(String.valueOf(details.getUnit()));
            ((CartViewHolder) holder).name.setText(details.getName());
            ((CartViewHolder) holder).price.setText("₹" + details.getPrice());

            sum = details.getTotalprice() + sum;
            Intent intent = new Intent("Sum");
            intent.putExtra("sum", sum);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            totalMarketPrice = details.getMarktPrice() * details.getUnit() + totalMarketPrice;
            Intent intentsaved = new Intent("TotalMarketPrice");
            intentsaved.putExtra("totalmarketprice", totalMarketPrice);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentsaved);
            ((CartViewHolder) holder).totalPrice.setText("₹" + (String.valueOf(details.getTotalprice())));
            ////increase Cart
            ((CartViewHolder) holder).plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int[] unityy = new int[]{Integer.parseInt(((CartViewHolder) holder).quantityCart.getText().toString())};
                    if (unityy[0] < 10) {
                        unityy[0]++;
                        ((CartViewHolder) holder).quantityCart.setText(String.valueOf(unityy[0]));
                        ((CartViewHolder) holder).unit.setText(String.valueOf(unityy[0]));
                        totalPrice = Integer.parseInt(String.valueOf(list.get(holder.getAdapterPosition()).getPrice())) * unityy[0];
                        String id = list.get(holder.getAdapterPosition()).getId();
                        database.collection("CurrentUser").document(userId).collection("cart").document(id)
                                .update("unit", unityy[0], "totalprice", totalPrice).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });

                        details.setTotalprice(totalPrice);
                        ((CartViewHolder) holder).totalPrice.setText("₹" + (String.valueOf(totalPrice)));
                        ((CartViewHolder) holder).unit.setText(String.valueOf(unityy[0]));
                        for (int i = 0; i < list.size(); i++) {
                            int[] temp = new int[]{list.get(i).getTotalprice()};
                            sumPlus = temp[0] + sumPlus;
                        }
                        Intent intent2 = new Intent("SumPlus");
                        intent2.putExtra("sumplus", sumPlus);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
                        plusMarketPrice = details.getMarktPrice() * details.getUnit() + plusMarketPrice;
                        Intent savePlus = new Intent("PlusMarketPrice");
                        savePlus.putExtra("plusmarketprice", plusMarketPrice);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(savePlus);
                        sumPlus = 0;
                        plusMarketPrice = 0;

                    }
                }
            });
            ////Decrease Cart
            ((CartViewHolder) holder).minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int[] unityy = new int[]{Integer.parseInt(((CartViewHolder) holder).quantityCart.getText().toString())};
                    if (unityy[0] > 0) {
                        unityy[0]--;
                        ((CartViewHolder) holder).quantityCart.setText(String.valueOf(unityy[0]));
                        ((CartViewHolder) holder).unit.setText(String.valueOf(unityy[0]));
                        totalPrice = Integer.parseInt(String.valueOf(list.get(holder.getAdapterPosition()).getPrice())) * unityy[0];
                        database.collection("CurrentUser").document(userId).collection("cart").document(list.get(holder.getAdapterPosition()).getId())
                                .update("unit", unityy[0], "totalprice", totalPrice).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });

                        details.setTotalprice(totalPrice);
                        ((CartViewHolder) holder).totalPrice.setText("₹" + (String.valueOf(totalPrice)));
                        ((CartViewHolder) holder).unit.setText(String.valueOf(unityy[0]));
                        for (int i = 0; i < list.size(); i++) {
                            int[] temp = new int[]{list.get(i).getTotalprice()};
                            sumMinus = temp[0] + sumMinus;
                        }
                        Intent intent3 = new Intent("SumMinus");
                        intent3.putExtra("summinus", sumMinus);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent3);
                        minusMarketPrice = details.getMarktPrice() * details.getUnit() + minusMarketPrice;
                        Intent intenMinus = new Intent("MinusMarketPrice");
                        intenMinus.putExtra("minusmarketprice", minusMarketPrice);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intenMinus);
                        sumMinus = 0;
                        minusMarketPrice = 0;
                    }
                    if (unityy[0] == 0) {
                        database.collection("CurrentUser").document(userId).collection("cart").document(list.get(holder.getAdapterPosition()).getId())
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                        removeItem(holder.getAdapterPosition());
                        for (int i = 0; i < list.size(); i++) {
                            int[] temp = new int[]{list.get(i).getTotalprice()};
                            finalPrice = temp[0] + finalPrice;
                            int tot = finalPrice;
                        }
                        Intent intent4 = new Intent("FinalPrice");
                        intent4.putExtra("finalprice", String.valueOf(finalPrice));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent4);
                        finalPrice = 0;
                    }
                }
            });

        }
        if (holder.getClass() == OrderViewHolder.class) {
            ((OrderViewHolder) holder).totalPrice.setText("₹" + String.valueOf(list.get(position).getTotalprice()));
            ((OrderViewHolder) holder).qty.setText(String.valueOf(list.get(position).getQty() + "/" + list.get(position).getUnit() + "Unit"));
            ((OrderViewHolder) holder).price.setText("₹" + list.get(position).getPrice());
            ((OrderViewHolder) holder).name.setText(list.get(position).getName());
            Glide.with(context).load(list.get(position).getImgUri()).into(((OrderViewHolder) holder).img);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getViewType() == VIEW_TYPE_SINGLE_CART) {
            return VIEW_TYPE_SINGLE_CART;
        } else {
            return VIEW_TYPE_SINGLE_ORDERS;
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView price, quantityCart, name, totalPrice, qty;
        ImageView img, minus, plus;
        TextView unit;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            totalPrice = itemView.findViewById(R.id.cart_totalPrice);
            img = itemView.findViewById(R.id.cart_img);
            quantityCart = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.cart_price);
            name = itemView.findViewById(R.id.cart_name);
            unit = itemView.findViewById(R.id.cart_qty);
            minus = itemView.findViewById(R.id.minus_cart);
            qty = itemView.findViewById(R.id.qty);
            plus = itemView.findViewById(R.id.plus_cart);
        }
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView price, qty, name, totalPrice;
        private ImageView img;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            totalPrice = itemView.findViewById(R.id.cart_totalPrice);
            img = itemView.findViewById(R.id.cart_img);
            qty = itemView.findViewById(R.id.qty);
            price = itemView.findViewById(R.id.cart_price);
            name = itemView.findViewById(R.id.cart_name);


        }
    }
}
