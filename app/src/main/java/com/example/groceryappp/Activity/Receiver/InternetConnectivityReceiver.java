package com.example.groceryappp.Activity.Receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.groceryappp.Activity.Fragment.UserHomeFragment;
import com.example.groceryappp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class InternetConnectivityReceiver extends BroadcastReceiver {
    private ConnectivityListener connectivityListener;
    public InternetConnectivityReceiver(ConnectivityListener listener) {
        this.connectivityListener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnectedToInternet(context)) {

            if (connectivityListener != null) {
                connectivityListener.onInternetConnected();
            }
        } else {
showDialog( context);
        }
    }

    private void showDialog(Context context) {
        BottomSheetDialog dialog=new BottomSheetDialog(context);
        View view= LayoutInflater.from(context).inflate(R.layout.internetdialog, null);
        dialog.setContentView(view);
        dialog.setCancelable(false);

        dialog.show();
        TextView refresh= view.findViewById(R.id.refresh);
        ProgressBar pgbar= view.findViewById(R.id.pgbar);

     refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               pgbar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnectedToInternet(context)) {
                         dialog.dismiss();
                            pgbar.setVisibility(View.GONE);
                        } else {
                          dialog.show();
                            pgbar.setVisibility(View.GONE);
                        }
                    }
                }, 1000);


            }
        });
    }

    private boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public interface ConnectivityListener {
        void onInternetConnected();
    }
}
