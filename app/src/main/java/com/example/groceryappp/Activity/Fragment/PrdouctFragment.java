package com.example.groceryappp.Activity.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.Activity.EditProductActivity;
import com.example.groceryappp.Activity.Adapter.AdminProductAdapter;
import com.example.groceryappp.Activity.AllModel.AdminList;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class PrdouctFragment extends Fragment {
    TextView open, close;
    AdminProductAdapter adapter;
    private ProgressBar pgbar;
    ArrayList<AdminList> lists;
    private LinearLayout emptyList;
    FirebaseAuth auth;
    FirebaseFirestore database;
    private RecyclerView rcv;
    private SearchView searchView;
    private SwitchCompat switchh;
    private ImageView profile;
    private TextView addItem;

    public PrdouctFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prdouct, container, false);
pgbar=new ProgressBar(getContext());

        initialization(view);
        instanceCreated();
        recylerviewSet();
        retriveAllItemOfSeller();
onClick();

//        search();






      /*  switchh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {

                    open.setVisibility(View.VISIBLE);
                    close.setVisibility(View.GONE);
                } else {

                    open.setVisibility(View.GONE);
                    close.setVisibility(View.VISIBLE);

                }
            }
        });*/




        return view;

    }

    private void onClick() {
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProductActivity.class);
                intent.putExtra("add", true);
                startActivity(intent);
            }
        });
    }

    private void instanceCreated() {
        auth = FirebaseAuth.getInstance();
        lists = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
    }

    private void recylerviewSet() {

        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminProductAdapter(lists, getContext());
        rcv.setAdapter(adapter);
    }

    public void retriveAllItemOfSeller() {



        database.collection("CurrentUser").document(auth.getUid()).collection("All Item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    AdminList adminList = documentSnapshot.toObject(AdminList.class);
                    adminList.setId(documentSnapshot.getId());
                    lists.add(adminList);
                    if (lists.size()==0){

                        emptyList.setVisibility(View.VISIBLE);


                    }else {
                        emptyList.setVisibility(View.GONE);

                    }
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialization(View view) {
        rcv = view.findViewById(R.id.rcv_admin_home);
        searchView = view.findViewById(R.id.searchview);
        profile = view.findViewById(R.id.profile_admin);

        addItem = view.findViewById(R.id.add_Item);
        pgbar = view.findViewById(R.id.pgbar);
        emptyList = view.findViewById(R.id.prodouct_empty);


    }

     /*   private void search () {
            searchView.clearFocus();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterlist(newText);
                    return true;
                }
            });
        }


    private void filterlist(String newText) {

        List<AdminList> list=new ArrayList<>();
        for (AdminList m:lists){
            if (m.getName().toLowerCase().contains(newText.toLowerCase())) {
                list.add(m);

            }
        }if (list.isEmpty()){
            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }
        else {
            adapter.setFilterList(list);

        }
        /////



    }*/


}