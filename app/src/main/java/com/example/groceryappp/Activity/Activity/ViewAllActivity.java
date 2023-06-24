package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.Adapter.ProductDetailsAdapter;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.Fragment.CartFragment;
import com.example.groceryappp.Activity.Fragment.UserHomeFragment;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
public class ViewAllActivity extends AppCompatActivity {
    private RecyclerView singlePrdctRcv;
    private CardView checkout;
    private FirebaseFirestore database;
    private ImageView cart;

    private TextView totalPrice, cartSize;
    private FirebaseAuth auth;
    ;
    private AppCompatEditText search;
    private ProductDetailsAdapter vegtableAdapter;

    private ArrayList<SingleProductDetails> list;
    public BroadcastReceiver receiver6 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int size = intent.getIntExtra("size", 0);
            cartSize.setText("" + size);
            cartSize.setVisibility(View.VISIBLE);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        getSupportActionBar().hide();
        viewInitialize();
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        findViewById(R.id.noResultFound).setVisibility(View.GONE);
        database.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int listSize = queryDocumentSnapshots.getDocuments().size();
                if (listSize != 0) {
                    cartSize.setText("" + listSize);
                    cartSize.setVisibility(View.VISIBLE);
                }


            }
        });

        LocalBroadcastManager.getInstance(ViewAllActivity.this).registerReceiver(receiver6, new IntentFilter("Size"));

//        checkout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent=new Intent(ViewAllActivity.this,CartActivity.class);
////                startActivity(intent);
//
//
//            }
//        });
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
                findViewById(R.id.noResultFound).setVisibility(View.GONE);
                filter(editable.toString());
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.relative, new CartFragment()).commit();

            }
        });
        String type = getIntent().getStringExtra("type");
        ////getting Vegetables
        if (type != null && type.equalsIgnoreCase("Vegetables")) {
            database.collection("CurrentUser").document("YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All Item").whereEqualTo("category", "Vegetable").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                                                    @Override
                                                                                                                                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                                        for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                                            SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                                            //  list2.add(singleProductDetails);
                                                                                                                                                                                            // list.add(new SingleProductDetails(q.get("name").toString(),q.get("imgUri").toString(), q.get("qty").toString(), Integer.parseInt(q.get("price").toString()),Integer.parseInt(q.get("marktPrice").toString(),q.get("id").toString())));

                                                                                                                                                                                            list.add(singleProductDetails);
                                                                                                                                                                                            // Toast.makeText(ViewAllActivity.this,""+q.get("marktPrice")+q.get("price"), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                        }
                                                                                                                                                                                        vegtableAdapter.notifyDataSetChanged();

                                                                                                                                                                                        // Toast.makeText(ViewAllActivity.this, queryDocumentSnapshots.getDocuments().size(), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                    }
                                                                                                                                                                                }

            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GROCERY", e.getMessage());
                }
            });

        }

        ////getting Fruits
       else if (type != null && type.equalsIgnoreCase("Fruit")) {
            database.collection("CurrentUser").document("YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All Item").whereEqualTo("category", "Fruit").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                                    for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                                        SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                                        list.add(singleProductDetails);


                                                                                                                                                                                        // Toast.makeText(ViewAllActivity.this,""+q.get("marktPrice")+q.get("price"), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                    }
                                                                                                                                                                                    vegtableAdapter.notifyDataSetChanged();

                                                                                                                                                                                    // Toast.makeText(ViewAllActivity.this, queryDocumentSnapshots.getDocuments().size(), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                }
                                                                                                                                                                            }

            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GROCERY", e.getMessage());
                }
            });

        }  ////getting Fruits
      else   if (type != null && type.equalsIgnoreCase("Seasonal")) {
            database.collection("CurrentUser").document("YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All Item").whereEqualTo("category", "Seasonal").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                                                   @Override
                                                                                                                                                                                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                                       for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                                           SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                                           list.add(singleProductDetails);
                                                                                                                                                                                           // list.add(new SingleProductDetails(q.get("name").toString(),q.get("imgUri").toString(), q.get("qty").toString(),0,0));


                                                                                                                                                                                           // Toast.makeText(ViewAllActivity.this,""+q.get("marktPrice")+q.get("price"), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                       }
                                                                                                                                                                                       vegtableAdapter.notifyDataSetChanged();

                                                                                                                                                                                       // Toast.makeText(ViewAllActivity.this, queryDocumentSnapshots.getDocuments().size(), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                   }
                                                                                                                                                                               }

            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GROCERY", e.getMessage());
                }
            });

        }  ////getting Fruits
      else   if (type != null && type.equalsIgnoreCase("Leafies")) {
            database.collection("CurrentUser").document("YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All Item").whereEqualTo("category", "Leafies").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                                                  @Override
                                                                                                                                                                                  public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                                      for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                                          SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                                          list.add(singleProductDetails);
                                                                                                                                                                                          // list.add(new SingleProductDetails(q.get("name").toString(),q.get("imgUri").toString(), q.get("qty").toString(),0,0));


                                                                                                                                                                                          // Toast.makeText(ViewAllActivity.this,""+q.get("marktPrice")+q.get("price"), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                      }
                                                                                                                                                                                      vegtableAdapter.notifyDataSetChanged();

                                                                                                                                                                                      // Toast.makeText(ViewAllActivity.this, queryDocumentSnapshots.getDocuments().size(), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                  }
                                                                                                                                                                              }

            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GROCERY", e.getMessage());
                }
            });

        }  ////getting Fruits
       else if (type != null && type.equalsIgnoreCase("Others")) {
            database.collection("CurrentUser").document("YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All Item").whereEqualTo("category", "Others").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                                                 @Override
                                                                                                                                                                                 public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                                     for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                                         SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                                         list.add(singleProductDetails);
                                                                                                                                                                                         //list.add(new SingleProductDetails(q.get("name").toString(),q.get("imgUri").toString(), q.get("qty").toString(),0,0));


                                                                                                                                                                                         // Toast.makeText(ViewAllActivity.this,""+q.get("marktPrice")+q.get("price"), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                     }
                                                                                                                                                                                     vegtableAdapter.notifyDataSetChanged();

                                                                                                                                                                                     // Toast.makeText(ViewAllActivity.this, queryDocumentSnapshots.getDocuments().size(), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                     database.collection("CurrentUser").document("YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All Item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                                                                                                                                                                                       @Override
                                                                                                                                                                                                                                                                                                                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                                                                                                                                                                           for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                                                                                                                                                                               SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                                                                                                                                                                               list.add(singleProductDetails);
                                                                                                                                                                                                                                                                                                                               //list.add(new SingleProductDetails(q.get("name").toString(),q.get("imgUri").toString(), q.get("qty").toString(),0,0));


                                                                                                                                                                                                                                                                                                                               // Toast.makeText(ViewAllActivity.this,""+q.get("marktPrice")+q.get("price"), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                                                                                                                                                           }
                                                                                                                                                                                                                                                                                                                           vegtableAdapter.notifyDataSetChanged();

                                                                                                                                                                                                                                                                                                                           // Toast.makeText(ViewAllActivity.this, queryDocumentSnapshots.getDocuments().size(), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                                                                                                                                                       }
                                                                                                                                                                                                                                                                                                                   }

                                                                                                                                                                                     ).addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                                         @Override
                                                                                                                                                                                         public void onFailure(@NonNull Exception e) {
                                                                                                                                                                                             Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                                                                                                             Log.e("GROCERY", e.getMessage());
                                                                                                                                                                                         }
                                                                                                                                                                                     });
                                                                                                                                                                                 }
                                                                                                                                                                             }

            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GROCERY", e.getMessage());
                }
            });

        }


else {
            database.collection("CurrentUser").document("YC7vLsrOpiVkMBqOcseWHL1BLTH3").collection("All Item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                              @Override
                                                                                                                                              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                  for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                      SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                      list.add(singleProductDetails);
                                                                                                                                                      //list.add(new SingleProductDetails(q.get("name").toString(),q.get("imgUri").toString(), q.get("qty").toString(),0,0));


                                                                                                                                                      // Toast.makeText(ViewAllActivity.this,""+q.get("marktPrice")+q.get("price"), Toast.LENGTH_SHORT).show();
                                                                                                                                                  }
                                                                                                                                                  vegtableAdapter.notifyDataSetChanged();

                                                                                                                                                  // Toast.makeText(ViewAllActivity.this, queryDocumentSnapshots.getDocuments().size(), Toast.LENGTH_SHORT).show();
                                                                                                                                              }
                                                                                                                                          }

            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GROCERY", e.getMessage());
                }
            });
        }


        singlePrdctRcv.setLayoutManager(new LinearLayoutManager(this));
        list =new ArrayList<>();
        vegtableAdapter =new ProductDetailsAdapter(this,list);
        singlePrdctRcv.setAdapter(vegtableAdapter);

    }



    private void viewInitialize() {
        singlePrdctRcv = findViewById(R.id.single__prdt_rcv);
        checkout = findViewById(R.id.checkout);
        totalPrice = findViewById(R.id.total_price);
        search = findViewById(R.id.edt_Search);
        cartSize = findViewById(R.id.cartSize);
        cart = findViewById(R.id.cart);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void filter(String text) {
  ArrayList<SingleProductDetails> filterList=new ArrayList<>();
     boolean productFound=false;
        //looping through existing elements
        for (SingleProductDetails m: list) {
            //if the existing elements contains the search input
            if (m.getName().toLowerCase().contains(text.toLowerCase())) {
                productFound=true;

                filterList.add(m);
                Log.e("GURU", "" + list.size());

            }

        }

  singlePrdctRcv.setAdapter(new ProductDetailsAdapter(this,filterList));
        vegtableAdapter.notifyDataSetChanged();
        if (!productFound) {

            findViewById(R.id.noResultFound).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.noResultFound).setVisibility(View.GONE);        }

        //calling a method of the adapter class and passing the filtered list

    }






}