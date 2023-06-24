package com.example.groceryappp.Activity.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.groceryappp.Activity.Adapter.HeadLineCircular;
import com.example.groceryappp.Activity.Adapter.MixVegPriceDetails;
import com.example.groceryappp.Activity.Adapter.ViewPagerAdapter;
import com.example.groceryappp.Activity.AllModel.Headline;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;


public class UserHomeFragment extends Fragment {
    TabLayout tableLayout;
    ViewPager pager2;
    DotsIndicator indicator;
    ArrayList<Integer> listviewpager;


    private RecyclerView vegHeadline, allvegDeatails;
    private ArrayList<Headline> list;

    private ArrayList<SingleProductDetails> list2;

    Dialog dialog;
    FirebaseFirestore database;
    FirebaseAuth auth;
    MixVegPriceDetails vegtableAdapter;
    private ImageView back, cart, profile;
    private List<DocumentSnapshot> listSize;
    private HeadLineCircular adapter;


    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        initilizeView(view);
        database = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        //adding viewpager

        listviewpager=new ArrayList<>();
        listviewpager.add(R.drawable.img_7);
        listviewpager.add(R.drawable.img_10);
        listviewpager.add(R.drawable.tomatofinal);

        listviewpager.add(R.drawable.img_8);
        listviewpager.add(R.drawable.img_9);

        fetchingItemsOfSeller();
        fetchingCtegoryItemOfSeller();
        pager2.setAdapter(new ViewPagerAdapter(getContext(),listviewpager));
        indicator.attachTo(pager2);
      /*  dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
        new CountDownTimer(2000,1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
dialog.dismiss();
            }
        }.start();*/



      /*  cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                intent.putExtra("cartSize", listSize.size());
                startActivity(intent);
            }
        });*/
       /* profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProfileActivity.class));
            }
        });*/


        list2 = new ArrayList<>();
        vegtableAdapter = new MixVegPriceDetails(getContext(), list2);
        allvegDeatails.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allvegDeatails.setAdapter(vegtableAdapter);



        list = new ArrayList<>();



        vegHeadline.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new HeadLineCircular(list, getContext());
        vegHeadline.setAdapter(adapter);
        return view;
    }
    private void loadingProgrgessBar() {



    }

      private void initilizeView(View view) {
        vegHeadline=view.findViewById(R.id.rcv_vegHeadline);

        allvegDeatails=view.findViewById(R.id.rcv_allveg);
          pager2=view.findViewById(R.id.viewpager1);
          indicator=view.findViewById(R.id.dots_indicator);
       // back=view.findViewById(R.id.back);
      //  profile=view.findViewById(R.id.profile);




    }

    @Override
    public void onResume() {
        super.onResume();


        vegtableAdapter.notifyDataSetChanged();

    }

    private void fetchingItemsOfSeller(){
        database.collection("CurrentUser").document("YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All Item").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                       @Override
                                                                                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                           for (QueryDocumentSnapshot q : task.getResult()) {
                                                                                                               SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                               list2.add(singleProductDetails);
                                                                                                               // list2.add(new SingleProductDetails(q.get("Name").toString(),  q.get("price").toString(),q.get("qty").toString()));

                                                                                                           }

                                                                                                           vegtableAdapter.notifyDataSetChanged();


                                                                                                       }
                                                                                                   }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchingCtegoryItemOfSeller(){
        database.collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            for (QueryDocumentSnapshot q : task.getResult()) {
                                                                                // SingleProductDetails singleProductDetails=q.toObject(SingleProductDetails.class)
                                                                                //  list2.add(singleProductDetails);
                                                                                list.add(new Headline(q.get("name").toString(),q.get("type").toString(),q.get("img").toString()));

                                                                            }
                                                                            adapter.notifyDataSetChanged();

                                                                        }
                                                                    }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}