package com.example.groceryappp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryappp.Activity.Adapter.ProductDetailsAdapter;

import com.example.groceryappp.Activity.Adapter.SearchHistory;
import com.example.groceryappp.Activity.AllModel.SingleCartDetails;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Database.DatabaseHelper;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Fragment.CartFragment;

import com.example.groceryappp.Activity.Receiver.InternetConnectivityReceiver;
import com.example.groceryappp.Activity.Utills.CartSizeListner;
import com.example.groceryappp.Activity.Utills.FirebaseCallback;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ViewAllActivity extends AppCompatActivity implements CartSizeListner {
    public BroadcastReceiver receiver6 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int size = intent.getIntExtra("size", 0);
            cartSize.setText("" + size);
            cartSize.setVisibility(View.VISIBLE);

        }
    };
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private static final String SHARED_PREF_NAME = "SearchHistory";
    private static final String KEY_HISTORY = "history";
    private InternetConnectivityReceiver connectivityReceiver;
    private RecyclerView singlePrdctRcv, searchHistoryRecyclerView;
    private CardView checkout;
    private ImageView cart, voiceSearch, scan;
    private TextView totalPrice, cartSize;
    private EditText search;
    private CardView searchHistoryLayout;
    private ProductDetailsAdapter vegtableAdapter;
    private ArrayList<SingleProductDetails> list;
    SearchHistory searchAdapater;

    private SharedPreferences sharedPreferences;
    private Set<String> searchHistorySet;
    private ArrayList<String> searchHistoryList;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private TextView result;
    private String userId;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        getSupportActionBar().hide();
        viewInitialize();
        ////
      shimmerFrameLayout.startShimmer();
      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
          shimmerFrameLayout.stopShimmer();
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        },2000);*/

        sharedPreferenceManager = SharedPreferenceManager.getInstance(ViewAllActivity.this);

        SharedPreferences preferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);

        if (sharedPreferenceManager != null && preferences.contains("USER_ID")) {
            userId = SharedPreferenceManager.getInstance(ViewAllActivity.this).getUserId();
        }
        ////


        findViewById(R.id.noResultFound).setVisibility(View.GONE);

        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        surfaceView = findViewById(R.id.surfaceView);
        list = new ArrayList<>();
        singlePrdctRcv.setLayoutManager(new LinearLayoutManager(this));
        vegtableAdapter = new ProductDetailsAdapter(ViewAllActivity.this,this, list);
        singlePrdctRcv.setAdapter(vegtableAdapter);
        List<SingleCartDetails> allCartList=   DatabaseHelper.getInstance(ViewAllActivity.this).getDao().getAllCartDetails();
if (allCartList.size()>0){
    cartSize.setText("" + allCartList.size());
    cartSize.setVisibility(View.VISIBLE);
}


new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {

        String type = getIntent().getStringExtra("type");

        ////getting Vegetables
        if (type != null && type.equals("Vegetables")) {
            gettingCateogoryProduct("Vegetables");
        }
        ////getting Fruits
        else if (type != null && type.equalsIgnoreCase("Fruit")) {
            gettingCateogoryProduct("Fruit");
        }  ////getting Fruits
        else if (type != null && type.equalsIgnoreCase("Seasonal")) {
            gettingCateogoryProduct("Seasonal");

        }  ////getting Fruits
        else if (type != null && type.equalsIgnoreCase("Leafies")) {
            gettingCateogoryProduct("Leafies");

        } else {
            fetchingAllOrder();

        }
    }
},1000);
    /*    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraSource();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }*/
        // vegtableAdapter =new

    /*    ProductDetailsAdapter(this,list);
        singlePrdctRcv.setAdapter(vegtableAdapter);
        vegtableAdapter.notifyDataSetChanged();*/
        // Initialize Shared Preferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // Retrieve the search history from Shared Preferences
        searchHistorySet = sharedPreferences.getStringSet(KEY_HISTORY, new LinkedHashSet<>());
        searchHistoryList = new ArrayList<>(searchHistorySet);
        // Set up the RecyclerView
    /*    searchAdapater = new SearchHistory(searchHistoryList,this,this);
        searchHistoryRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        searchHistoryRecyclerView.setAdapter(searchAdapater);*/

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewAllActivity.this, QrSearchActivity.class);
                startActivity(intent);

            }
        });
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (!search.getText().toString().trim().isEmpty()) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
                        String searchTerm = search.getText().toString();
                        addToSearchHistory(searchTerm);

                    }
                }

                return false;
            }
        });

        voiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                startActivityForResult(intent, 100);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform your refresh action here
                // Once the refreshing is complete, call setRefreshing(false) to stop the loading indicator
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if (userId != null) {
            FirebaseCallback.fetchProductDetailsFromFirebase(userId, "cart", this, new FirebaseCallback() {
                @Override
                public void onCallback(ArrayList<SingleProductDetails> productDetails) {
                    if (productDetails.size() != 0) {
                        cartSize.setText("" + productDetails.size());
                        cartSize.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCallbackInfo(ArrayList<UserInfo> info) {

                }
            });
        }

        LocalBroadcastManager.getInstance(ViewAllActivity.this).

                registerReceiver(receiver6, new IntentFilter("Size"));

        search.addTextChangedListener(new

                                              TextWatcher() {
                                                  @Override
                                                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                  }

                                                  @Override
                                                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                  }

                                                  @Override
                                                  public void afterTextChanged(Editable editable) {
                                                      //after the change calling the method and passing the search input
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



    }

    private void startCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)

                .build();

        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Could not set up the barcode detector", Toast.LENGTH_SHORT).show();
            return;
        }

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(ViewAllActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Do nothing
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new com.google.android.gms.vision.Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Do nothing
            }

            @Override
            public void receiveDetections(com.google.android.gms.vision.Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String qrCode = qrCodes.valueAt(0).displayValue;
                            ;
                            if (qrCode.startsWith("uid:")) {
                                // Further validate the Aadhaar QR code format if needed
                                // You can check for specific patterns or lengths to ensure it matches the Aadhaar QR code format
                                result.setText("UID RESULT   --" + qrCode);
                                // Example validation: Check if the QR code length is 77 characters

                            }
                            if (qrCode.length() == 77) {
                                result.setText("LENGTH RESULT   --" + qrCode);
                            } else {
                                result.setText("no result" + qrCode);
                            }

                            // Toast.makeText(ViewAllActivity.this, "QR Code: " + qrCode, Toast.LENGTH_SHORT).show();
                            // Do something with the QR code data
                        }
                    });
                } else {
                    Toast.makeText(ViewAllActivity.this, "invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addToSearchHistory(String searchQuery) {
        // Add the new search query to the set
        searchHistorySet.add(searchQuery);

        // Limit the search history to the maximum size
        if (searchHistorySet.size() > 5) {
            String oldestQuery = searchHistoryList.get(0);
            searchHistorySet.remove(oldestQuery);
        }

        // Update the Shared Preferences with the modified search history set
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_HISTORY, searchHistorySet);
        editor.apply();

        // Update the search history list and notify the adapter
        searchHistoryList.clear();
        searchHistoryList.addAll(searchHistorySet);
        searchAdapater.notifyDataSetChanged();


    }

    private void fetchingAllOrder() {
        FirebaseClient.getInstance().collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").collection("All Item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                              @Override
                                                                                                                                                              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                  for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                      SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                      list.add(singleProductDetails);
                                                                                                                                                                  }
                                                                                                                                                                  vegtableAdapter.notifyDataSetChanged();

                                                                                                                                                                  if (list.size() == 0) {
                                                                                                                                                                      findViewById(R.id.noResultFound).setVisibility(View.VISIBLE);

                                                                                                                                                                  }
                                                                                                                                                                  shimmerFrameLayout.stopShimmer();
                                                                                                                                                                  swipeRefreshLayout.setVisibility(View.VISIBLE);
                                                                                                                                                                  shimmerFrameLayout.setVisibility(View.GONE);
                                                                                                                                                              }
                                                                                                                                                          }

        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // Log.e("GROCERY", e.getMessage());
            /*    swipeRefreshLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);*/
            }
        });


    }

    private void gettingCateogoryProduct(String value) {
        FirebaseClient.getInstance().collection("CurrentUser").document("NAPuTkYOldg4M8FHUUwKeNvkBK73").collection("All Item").whereEqualTo("category", value).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                                                                                                              @Override
                                                                                                                                                                                              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                                                                                                                  for (DocumentSnapshot q : queryDocumentSnapshots.getDocuments()) {
                                                                                                                                                                                                      SingleProductDetails singleProductDetails = q.toObject(SingleProductDetails.class);
                                                                                                                                                                                                      list.add(singleProductDetails);
                                                                                                                                                                                                  }
                                                                                                                                                                                                  shimmerFrameLayout.stopShimmer();
                                                                                                                                                                                                  swipeRefreshLayout.setVisibility(View.VISIBLE);
                                                                                                                                                                                                  shimmerFrameLayout.setVisibility(View.GONE);
                                                                                                                                                                                                  vegtableAdapter.notifyDataSetChanged();
                                                                                                                                                                                                  if (list.size() == 0) {
                                                                                                                                                                                                      findViewById(R.id.noResultFound).setVisibility(View.VISIBLE);

                                                                                                                                                                                                  }
                                                                                                                                                                                              }
                                                                                                                                                                                          }

        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // Log.e("GROCERY", e.getMessage());
            }
        });


    }


    private void viewInitialize() {
        singlePrdctRcv = findViewById(R.id.single__prdt_rcv);
        searchHistoryRecyclerView = findViewById(R.id.searchRcv);
        checkout = findViewById(R.id.checkout);
        totalPrice = findViewById(R.id.total_price);
        search = findViewById(R.id.edt_Search);
        cartSize = findViewById(R.id.cartSize);
        cart = findViewById(R.id.cart);
        searchHistoryLayout = findViewById(R.id.searchHistory);
        voiceSearch = findViewById(R.id.voiceSearch);
        scan = findViewById(R.id.scan);
        result = findViewById(R.id.result);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void filter(String text) {
        ArrayList<SingleProductDetails> filterList = new ArrayList<>();
        boolean productFound = false;
        //looping through existing elements
        for (SingleProductDetails m : list) {
            //if the existing elements contains the search input
            if (m.getName().toLowerCase().contains(text.toLowerCase())) {
                productFound = true;

                filterList.add(m);
                //  addSearchhistory(String.valueOf(m));
                Log.e("GURU", "" + list.size());

            }

        }

        singlePrdctRcv.setAdapter(new ProductDetailsAdapter(ViewAllActivity.this,this, filterList));
        // vegtableAdapter.notifyDataSetChanged();
        if (!productFound) {

            findViewById(R.id.noResultFound).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.noResultFound).setVisibility(View.GONE);
        }
    }

    public void onResume() {
        super.onResume();
        connectivityReceiver = new InternetConnectivityReceiver(new InternetConnectivityReceiver.ConnectivityListener() {
            @Override
            public void onInternetConnected() {

            }
        });
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            filter(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }


    @Override
    public void onCalBackUpdate(int size) {

        if (size>0){

            cartSize.setText("" + size);
            cartSize.setVisibility(View.VISIBLE);
        }else {

            cartSize.setText("");
            cartSize.setVisibility(View.GONE);
        }

    }
}

