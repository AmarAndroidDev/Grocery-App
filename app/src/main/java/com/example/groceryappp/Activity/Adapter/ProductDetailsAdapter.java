package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Activity.SingleProductActivity;
import com.example.groceryappp.Activity.AllModel.SingleCartDetails;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.Database.DatabaseHelper;
import com.example.groceryappp.Activity.Utills.CartSizeListner;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {
    private Context context;
    private int listSize, totalquantity = 1, totalprice = 0;
    private FirebaseFirestore database;
    private String userId;
    private CartSizeListner cartSizeListner;



    private ArrayList<SingleProductDetails> list;

    public ProductDetailsAdapter(CartSizeListner cartSizeListner,Context context, ArrayList<SingleProductDetails> list) {
        this.context = context;
        this.list = list;
        this.cartSizeListner = cartSizeListner;

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
        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
        SharedPreferences preferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, SingleProductActivity.class);
                intent.putExtra("prdct_info",details);
                context.startActivity(intent);
            }
        });
        if (sharedPreferenceManager != null && preferences.contains("USER_ID")) {
            userId = SharedPreferenceManager.getInstance(context).getUserId();
        }
        database = FirebaseFirestore.getInstance();
        holder.title.setText(details.getName());
        holder.price.setText(("₹" + details.getPrice()));
        holder.marketPrice.setText(("₹" + details.getMarktPrice()));
        Glide.with(context).load(details.getImgUri()).into(holder.profile_pic);
        holder.quantity.setText(details.getQty());
        ///////////////feteching cart details
        if (userId != null) {
          /*  database.collection("CurrentUser").document(userId)
                    .collection("cart").document(details.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().get("unit") != null) {
                                holder.addToCart.setVisibility(View.GONE);
                                holder.cartlayout.setVisibility(View.VISIBLE);
                                holder.quantityCart.setText(task.getResult().get("unit").toString());

                            }
                        }
                    });*/

            List<SingleCartDetails> alldetails = DatabaseHelper.getInstance(context).getDao().getAllCartDetails();

            if (alldetails.size() > 0) {
                for (int i = 0; i < alldetails.size(); i++) {
                    if (alldetails.get(i).getId().equals(details.getId())) {
                        holder.addToCart.setVisibility(View.GONE);
                        holder.cartlayout.setVisibility(View.VISIBLE);

                        holder.quantityCart.setText("" + alldetails.get(i).getUnit());
                    }
                }

                // holder.quantityCart.setText(""+alldetails.get(position).getUnit());
            }

        } else {
            holder.addToCart.setEnabled(false);
        }


        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalprice = Integer.parseInt(String.valueOf(details.getPrice())) * totalquantity;

              /*  details.setId(list.get(holder.getAdapterPosition()).getId());
                details.setTotalprice(totalprice);
                details.setImgUri(details.getImgUri());
                details.setQty(details.getQty());
                details.setUnit(totalquantity);
                details.setMarktPrice(details.getMarktPrice());*/
                DatabaseHelper.getInstance(context).getDao().addToCart(new SingleCartDetails("" + list.get(holder.getAdapterPosition()).getId(), details.getName(), details.getImgUri(), details.getQty(), details.getMarktPrice(), details.getPrice(), totalprice, totalquantity));
                holder.addToCart.setVisibility(View.GONE);
                holder.cartlayout.setVisibility(View.VISIBLE);
                holder.quantityCart.setText("1");
  /*              database.collection("CurrentUser").document(userId)
                        .collection("cart").document(list.get(holder.getAdapterPosition()).getId()).set(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                notifyDataSetChanged();
                database.collection("CurrentUser").document(userId).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listSize = queryDocumentSnapshots.getDocuments().size();
                        Intent intentSize = new Intent("Size");
                        intentSize.putExtra("size", listSize);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intentSize);
                    }
                });*/
             List<SingleCartDetails> allCartList=   DatabaseHelper.getInstance(context).getDao().getAllCartDetails();
                if (cartSizeListner != null) {
                    cartSizeListner.onCalBackUpdate(allCartList.size());
                }

               // Toast.makeText(context, ""+allCartList.size(), Toast.LENGTH_SHORT).show();



            }
        });

        ////increase cart
        holder.plusCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* final int[] qtyyy = new int[]{Integer.parseInt(holder.quantityCart.getText().toString())};
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
                }*/
                SingleCartDetails details1 = DatabaseHelper.getInstance(context).getDao().getSingleCartDetails(details.getId());
                int unit = details1.getUnit();

                unit++;
                DatabaseHelper.getInstance(context).getDao().incrementCart(details.getId().toString(), unit);
                holder.quantityCart.setText("" + unit);

            }
        });

        holder.minusCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*    final int[] qtyyy = new int[]{Integer.parseInt(holder.quantityCart.getText().toString())};
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
                }*/
                SingleCartDetails details1 = DatabaseHelper.getInstance(context).getDao().getSingleCartDetails(details.getId());
                int unit = details1.getUnit();
                if (unit > 0) {
                    unit--;
                    DatabaseHelper.getInstance(context).getDao().incrementCart(details.getId().toString(), unit);
                    holder.quantityCart.setText("" + unit);
                }
                if (unit == 0) {
                    List<SingleCartDetails> allCartList=   DatabaseHelper.getInstance(context).getDao().getAllCartDetails();
                    if (cartSizeListner != null) {
                        cartSizeListner.onCalBackUpdate(allCartList.size()-1);
                    }
                    holder.cartlayout.setVisibility(View.GONE);
                    holder.addToCart.setVisibility(View.VISIBLE);
                    DatabaseHelper.getInstance(context).getDao().deleteCart(details.getId());

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView price, quantityCart, marketPrice, quantity, title;
        private CardView cartlayout, addToCart;
        private ImageView profile_pic, minusCart, plusCart;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic = itemView.findViewById(R.id.veg_imgprdct);
            title = itemView.findViewById(R.id.title);
            quantity = itemView.findViewById(R.id.qty_desc);
            price = itemView.findViewById(R.id.price);
            marketPrice = itemView.findViewById(R.id.marketPrice);
            minusCart = itemView.findViewById(R.id.minus_cartprdct);
            plusCart = itemView.findViewById(R.id.plus_cartprdct);
            quantityCart = itemView.findViewById(R.id.quantityprdct);
            cartlayout = itemView.findViewById(R.id.cart_layouttprdct);
            addToCart = itemView.findViewById(R.id.add_to_cartprdct);


        }

    }


}

