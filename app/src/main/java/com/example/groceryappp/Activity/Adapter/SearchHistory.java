package com.example.groceryappp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Activity.ViewAllActivity;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchHistory extends RecyclerView.Adapter<SearchHistory.ViewHolder> {
    private ArrayList<String> list;
    private Context context;


    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String searchQuery);
    }


    public SearchHistory(ArrayList<String> list, Context context, OnItemClickListener itemClickListener) {
        this.list = list;
        this.context = context;

        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.search_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(list.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.searchname);



        }

        @Override
        public void onClick(View v) {

                String searchQuery = list.get(getAdapterPosition());
                itemClickListener.onItemClick(searchQuery);
            Toast.makeText(context, searchQuery, Toast.LENGTH_SHORT).show();
        }
    }
}

