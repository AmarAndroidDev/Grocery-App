package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.groceryappp.Activity.Adapter.UserOrderHeaderAdapter;
import com.example.groceryappp.Activity.AllModel.UserOrderHeader;
import com.example.groceryappp.Activity.Fragment.ProfileFragment;
import com.example.groceryappp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserOrderHeaderActivity extends AppCompatActivity {
RecyclerView rcvHeader;
ImageView cart;
ArrayList<UserOrderHeader> list;
private ImageView back;
UserOrderHeaderAdapter adapter;
FirebaseFirestore database;
ProgressDialog dialog;
FirebaseAuth auth;
TextView sum,savedPrice;
LinearLayout emptyCart;
RelativeLayout placeOrderActivity;

String orderType;
    LinearLayout emtyCart;
    TextView datashow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_header);
        getSupportActionBar().hide();
        orderType=getIntent().getStringExtra("ordertype");
        fetchingDialog();

  viewInitialize();
        String orderid=getIntent().getStringExtra("orderId");
        String buyerId=getIntent().getStringExtra("buyerid");
        String seller = getIntent().getStringExtra("sellerid");
back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
//Intent intent=new Intent(UserOrderHeaderActivity.this, profileFragment.class);
/*intent.putExtra("userType","user");
        startActivity(intent);
        finish();
        if (orderType.equals("setting")){


        }
        if (orderType.equals("placeOrder")){

            startActivity(new Intent(UserOrderHeaderActivity.this,HomeActivity.class));
            finish();
        }*/
      onBackPressed();

    }
});

database=FirebaseFirestore.getInstance();
auth=FirebaseAuth.getInstance();
        String orderId=getIntent().getStringExtra("orderId");//timstamp
        String totalvalue = getIntent().getStringExtra("sum");
        list=new ArrayList<>();
        //set up recylerview
        adapter=new UserOrderHeaderAdapter(list,this);
        rcvHeader.setLayoutManager(new LinearLayoutManager(this));
        rcvHeader.setAdapter(adapter);


        database.collection("CurrentUser").document(auth.getUid()).collection("orders").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot:queryDocumentSnapshots.getDocuments()){
                    UserOrderHeader header=snapshot.toObject(UserOrderHeader.class);
                    list.add(header);
                    if (list.size()!=0){
                        emptyCart.setVisibility(View.GONE);
                    }else {
                        emptyCart.setVisibility(View.VISIBLE);
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        if (list.size()!=0){
            emptyCart.setVisibility(View.GONE);
        }else {
            emptyCart.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
       /* Intent intent=new Intent(UserOrderHeaderActivity.this, ProfileFragment.class);

       intent.putExtra("USER_TYPE","user");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
*/
        super.onBackPressed();

    }

    private void viewInitialize() {
        rcvHeader=findViewById(R.id.rcv_order_header);
        back=findViewById(R.id.back);
        emptyCart=findViewById(R.id.emptycart);
    }
    private void fetchingDialog() {
        dialog = new ProgressDialog(UserOrderHeaderActivity.this);
        dialog.setMessage("Fetching Orders");
        dialog.setCancelable(false);
        dialog.show();
    }


}