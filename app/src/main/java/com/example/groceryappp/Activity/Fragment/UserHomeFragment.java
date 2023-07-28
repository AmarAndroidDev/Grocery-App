package com.example.groceryappp.Activity.Fragment;

import static com.example.groceryappp.Activity.Utills.SharedPreferenceManager.SELLER_CATEGORY_PREF_NAME;
import static com.example.groceryappp.Activity.Utills.SharedPreferenceManager.SINGLE_PRODCT_PREF_NAME;
import static com.example.groceryappp.Activity.Utills.SharedPreferenceManager.USER_INFO_PREF_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Activity.ViewAllActivity;
import com.example.groceryappp.Activity.Adapter.HeadLineCircular;
import com.example.groceryappp.Activity.Adapter.MixVegPriceDetails;
import com.example.groceryappp.Activity.Adapter.ViewPagerAdapter;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.AllModel.ViewPagerModel;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Utills.DataManagementUtils;
import com.example.groceryappp.Activity.Utills.DeleteDataWorker;
import com.example.groceryappp.Activity.Utills.FirebaseCallback;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.Activity.Utills.SharedPreferencesWorker;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserHomeFragment extends Fragment {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_FIRST_LAUNCH = "firstLaunch";
    private ImageView profilePic;
    private UserInfo listInfo;
    private ProgressBar pgbar;
    private AppCompatEditText search;

    private TextView deliveryadress;
    private ViewPager pager2;

    private DotsIndicator indicator;
    private ArrayList<ViewPagerModel> listviewpager;
    private RecyclerView vegHeadline, allvegDeatails;
    private ArrayList<SingleProductDetails> list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<SingleProductDetails> list2;
    private MixVegPriceDetails vegtableAdapter;
    private HeadLineCircular adapter;
    private boolean isCacheAvailable = false;
   private SharedPreferences preferences;
    private SharedPreferenceManager sharedPreferenceManager;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        initilizeView(view);

        //adding viewpager
        listviewpager = new ArrayList<>();
        list2 = new ArrayList<>();
        listviewpager.add(new ViewPagerModel(R.drawable.img_10,""));
        listviewpager.add(new ViewPagerModel(R.drawable.banner1,""));
        listviewpager.add(new ViewPagerModel(R.drawable.img_5,""));
        listviewpager.add(new ViewPagerModel(R.drawable.img_6,""));
        listviewpager.add(new ViewPagerModel(R.drawable.img_7,""));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            view.findViewById(R.id.pgbarHome).setVisibility(View.GONE);
            }
        },1000);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getContext().getColor(R.color.buttonBg));
        // swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getContext().getColor(R.color.bg2));
        list = new ArrayList<>();
        vegHeadline.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new HeadLineCircular(list, getContext());
        vegHeadline.setAdapter(adapter);
        vegtableAdapter = new MixVegPriceDetails(getContext(), list2, null);
        allvegDeatails.setAdapter(vegtableAdapter);
        allvegDeatails.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getContext());
 preferences = getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        if (sharedPreferenceManager != null && preferences.contains(SELLER_CATEGORY_PREF_NAME)) {
            list = sharedPreferenceManager.retrieveSingleProductSharedP(getContext(), SELLER_CATEGORY_PREF_NAME);
            adapter = new HeadLineCircular(list, getContext());
            vegHeadline.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } else {
            fetchingCtegoryItemOfSeller();

        }
        if (sharedPreferenceManager != null && preferences.contains("product list")) {
            list2 = sharedPreferenceManager.retrieveSingleProductSharedP(getContext(), "product list");
            vegtableAdapter = new MixVegPriceDetails(getContext(), list2, null);
            allvegDeatails.setAdapter(vegtableAdapter);
            vegtableAdapter.notifyDataSetChanged();

        } else {
            fetchingItemsOfSeller();

        }
        if (sharedPreferenceManager != null && preferences.contains(USER_INFO_PREF_NAME)) {
            listInfo = sharedPreferenceManager.retrieveUserInfoSharedP(getContext(), USER_INFO_PREF_NAME);
            deliveryadress.setText(listInfo.getCity() + "," + listInfo.getPincode());
            if (listInfo.getProfilePic() != null) {
                Glide.with(getContext()).load(listInfo.getProfilePic()).into(profilePic);
                pgbar.setVisibility(View.GONE);
            } else {
                pgbar.setVisibility(View.GONE);
            }

        } else {
            getingUserDetails(view);

        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getingUserDetails(view);
                fetchingItemsOfSeller();
                fetchingCtegoryItemOfSeller();
                swipeRefreshLayout.setRefreshing(false);
                // Perform your refresh action here
              /*  new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Stop the refreshing animation

                    }
                }, 2000);*/
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("listProduct", list2);
                startActivity(intent);
            }
        });

        // getingUserDetails(view);
        ////fetching address details

        pager2.setAdapter(new ViewPagerAdapter(getContext(), listviewpager));
        indicator.attachTo(pager2);





     /*   new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.pgbar33).setVisibility(View.GONE);
            }
        }, 1000);
*/
        return view;
    }

    private void getingUserDetails(View view) {
        String userId=null;
        if (sharedPreferenceManager != null && preferences.contains("USER_ID")) {
             userId=SharedPreferenceManager.getInstance(getContext()).getUserId();
        }

        if (userId!= null) {
            FirebaseClient.getInstance().collection("CurrentUser").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    listInfo = documentSnapshot.toObject(UserInfo.class);
                    deliveryadress.setText(listInfo.getCity() + "," + listInfo.getPincode());
                    if (listInfo.getProfilePic() != null) {
                        Glide.with(getContext()).load(listInfo.getProfilePic()).into(profilePic);
                        pgbar.setVisibility(View.GONE);
                    } else {
                        pgbar.setVisibility(View.GONE);
                    }

                   // sharedPreferenceManager.storeUserInfoDetailsinSharedP(listInfo, getContext(), SharedPreferenceManager.USER_INFO_PREF_NAME);
                }
            });
        } else {
            view.findViewById(R.id.txt).setVisibility(View.GONE);
            view.findViewById(R.id.deliveryAd).setVisibility(View.GONE);
            view.findViewById(R.id.realay).setVisibility(View.GONE);

        }

    }

    private void initilizeView(View view) {
        vegHeadline = view.findViewById(R.id.rcv_vegHeadline);
        allvegDeatails = view.findViewById(R.id.rcv_allveg);
        pager2 = view.findViewById(R.id.viewpager1);
        indicator = view.findViewById(R.id.dots_indicator);
        deliveryadress = view.findViewById(R.id.deliveryAd);
        profilePic = view.findViewById(R.id.profile_pic);
        search = view.findViewById(R.id.search);
        pgbar = view.findViewById(R.id.pgbar);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void fetchingItemsOfSeller() {
        FirebaseCallback.fetchProductDetailsFromFirebase("NAPuTkYOldg4M8FHUUwKeNvkBK73", "All Item", getContext(), new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<SingleProductDetails> productDetails) {
                list2.addAll(productDetails);
                vegtableAdapter.notifyDataSetChanged();
                // storeSingleProductDetailsinSharedP(list2);
               // sharedPreferenceManager.storeSingleProductDetailsinSharedP(list2, getContext(), SINGLE_PRODCT_PREF_NAME);

            }

            @Override
            public void onCallbackInfo(ArrayList<UserInfo> info) {

            }
        });
        vegtableAdapter.notifyDataSetChanged();

    }

    private void fetchingCtegoryItemOfSeller() {
        FirebaseCallback.fetchProductDetailsFromFirebase("NAPuTkYOldg4M8FHUUwKeNvkBK73", "Category", getContext(), new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<SingleProductDetails> productDetails) {
                list.addAll(productDetails);
                adapter.notifyDataSetChanged();

                //sharedPreferenceManager.storeSingleProductDetailsinSharedP(productDetails, getContext(), SELLER_CATEGORY_PREF_NAME);
            }

            @Override
            public void onCallbackInfo(ArrayList<UserInfo> info) {

            }
        });

    }

    public void storeSingleProductDetailsinSharedP(ArrayList<SingleProductDetails> data) {
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        // Create a SharedPreferences.Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Convert the list of data into a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(data);
        // Put the JSON string into SharedPreferences
        editor.putString("list", json);

        // Apply the changes to persist the data
        editor.apply();
        Toast.makeText(getContext(), "sucess store", Toast.LENGTH_SHORT).show();

    }

    public ArrayList<SingleProductDetails> retrieveSingleProductSharedP() {
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);

        // Retrieve the JSON string from SharedPreferences
        String json = sharedPreferences.getString("list", "");

        // Convert the JSON string back to a list of data
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SingleProductDetails>>() {
        }.getType();
        ArrayList<SingleProductDetails> data = gson.fromJson(json, type);
        Toast.makeText(getContext(), "sucess retrive", Toast.LENGTH_SHORT).show();
        // Return the list of data
        return data;

    }



    @Override
    public void onDestroy() {

      /*  preferences=getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("product list");
        editor.remove("sellerCategory");
        editor.apply();
        Toast.makeText(getContext(), "remove", Toast.LENGTH_SHORT).show();*/
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


    /*    private void deleteSharedPreference() {
                    // Get access to the SharedPreferences
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
                    // Clear the data from SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("list");
                    editor.apply();
                    Toast.makeText(getContext(), "Sucess", Toast.LENGTH_SHORT).show();
                }*/
    @Override
    public void onPause() {
        super.onPause();
        Log.e("frag", "pauseHome");
    }
}