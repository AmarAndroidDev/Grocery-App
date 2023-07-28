package com.example.groceryappp.Activity.Fragment;

import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.hideProgressDialog;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.showProgressDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.Activity.AdminProductActivity;
import com.example.groceryappp.Activity.Adapter.AdminProductListAdapter;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Utills.FirebaseCallback;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;


public class AdminProductFragment extends Fragment implements FirebaseCallback {
    RecyclerView rcvProduct;
    MaterialButton addProduct;
    private ProgressBar pgbar;
    private FirebaseAuth auth;
    private Switch btnShop;
    private String fireBaseShopStatus;
    private TextView shopStatus;
    private AppCompatEditText search;
    private AdminProductListAdapter allProductAdapter;
    private ArrayList<SingleProductDetails> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_product, container, false);
        viewInitialize(view);
        auth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.pgbarHome).setVisibility(View.GONE);
                addProduct.setVisibility(View.VISIBLE);
            }
        },1500);
list=new ArrayList<>();
        rcvProduct.setLayoutManager(new LinearLayoutManager(getContext()));
//list=FirebaseClient.getProductDetailsFromFirebase(auth.getUid(),"All Item",getContext());
FirebaseCallback.fetchProductDetailsFromFirebase(auth.getUid(), "All Item", getContext(), new FirebaseCallback() {
    @Override
    public void onCallback(ArrayList<SingleProductDetails> productDetails) {
        pgbar.setVisibility(View.GONE);
   list.addAll(productDetails);
   allProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCallbackInfo(ArrayList<UserInfo> info) {

    }
});
        if (list!=null){
            hideProgressDialog();
            allProductAdapter = new AdminProductListAdapter(list, getContext());
            rcvProduct.setAdapter(allProductAdapter);
        }

      //  showProgressDialog(getContext(), "Updating Shop Status...");
// Set the listener to handle the switch button's state change
//getting shop status open /closed
        FirebaseClient.getInstance().collection("CurrentUser").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                fireBaseShopStatus = String.valueOf(documentSnapshot.get("Shop status"));
                if (fireBaseShopStatus.equals("open")) {
                    btnShop.setChecked(true);
                    shopStatus.setText("Shop Open");
                    btnShop.setTrackDrawable(getContext().getDrawable(R.drawable.switch_track));
                    shopStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonBg));
                } else {
                    btnShop.setChecked(false);
                    shopStatus.setText("Shop Closed");
                    shopStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    btnShop.setTrackDrawable(getContext().getDrawable(R.drawable.switch_trackred));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForShopStatus(fireBaseShopStatus);

            }
        });




     /*   FirebaseClient.getInstance().collection("CurrentUser").document(auth.getUid()).collection("All Item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    Sin allProduct = snapshot.toObject(AdminProductList.class);
                    list.add(allProduct);
                }
                allProductAdapter.notifyDataSetChanged();
                hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });*/

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdminProductActivity.class);
                intent.putExtra("add", true);
                startActivity(intent);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                view.findViewById(R.id.noResultFound).setVisibility(View.GONE);
                filter(editable.toString(), view);
            }
        });
        return view;
    }

    private void showDialogForShopStatus(String status) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        if (status.equals("open")) {
            dialog.setTitle("Want to close").setMessage("Are you sure ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    hideProgressDialog();
                    saveShopStatusToFirebase("close");
                /*    shopStatus.setText("Shop closed");
                    btnShop.setTrackDrawable(getContext().getDrawable(R.drawable.switch_trackred));
                    shopStatus.setTextColor(ContextCompat.getColor(getContext(),R.color.red) );
                    updatedShopstatus="close";*/
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    btnShop.setChecked(true);
                }
            }).show();
        }
        else {
            dialog.setTitle("Want to Open").setMessage("Are you sure ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    showProgressDialog(getContext(), " Updating Shop Status...");
                    saveShopStatusToFirebase("open");
                    //   updatedShopstatus="open";
                 /*   shopStatus.setText("Shop Open");
                    btnShop.setTrackDrawable(getContext().getDrawable(R.drawable.switch_track));
                    shopStatus.setTextColor(ContextCompat.getColor(getContext(),R.color.buttonBg) );*/
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (btnShop.isChecked()) {
                        btnShop.setChecked(false);
                    }
                }
            }).show();
        }

    }

    private void saveShopStatusToFirebase(String status) {
        FirebaseClient.getInstance().collection("CurrentUser").document(auth.getUid()).update("Shop status", status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (status.equals("open")) {
                    btnShop.setChecked(true);
                    shopStatus.setText("Shop Open");
                    btnShop.setTrackDrawable(getContext().getDrawable(R.drawable.switch_track));
                    shopStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonBg));
                    hideProgressDialog();
                    fireBaseShopStatus = "open";
                } else {
                    btnShop.setChecked(false);
                    shopStatus.setText("Shop Closed");
                    btnShop.setTrackDrawable(getContext().getDrawable(R.drawable.switch_trackred));
                    shopStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    fireBaseShopStatus = "close";
                    hideProgressDialog();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text, View view) {
        ArrayList<SingleProductDetails> filterList = new ArrayList<>();
        boolean productFound = false;
        //looping through existing elements
        for (SingleProductDetails m : list) {
            //if the existing elements contains the search input
            if (m.getName().toLowerCase().contains(text.toLowerCase())) {
                productFound = true;

                filterList.add(m);
                // Log.e("GURU", "" + list.size());

            }

        }

        rcvProduct.setAdapter(new AdminProductListAdapter(filterList, getContext()));
        allProductAdapter.notifyDataSetChanged();
        if (!productFound) {

            view.findViewById(R.id.noResultFound).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.noResultFound).setVisibility(View.GONE);
        }


    }

    private void viewInitialize(View view) {
        rcvProduct = view.findViewById(R.id.rcvAdminProduct);
        addProduct = view.findViewById(R.id.add_item);
        pgbar = view.findViewById(R.id.pgbar);
        search = view.findViewById(R.id.search);
        btnShop = view.findViewById(R.id.switchButton);
        shopStatus = view.findViewById(R.id.switchText);

    }




    @Override
    public void onCallback(ArrayList<SingleProductDetails> productDetails) {

    }

    @Override
    public void onCallbackInfo(ArrayList<UserInfo> info) {

    }
}