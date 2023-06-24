package com.example.groceryappp.Activity.ServiceFcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.groceryappp.Activity.Activity.SellerOrderDetailsActivity;
import com.example.groceryappp.Activity.Activity.UserOrderDetailsActivity;
import com.example.groceryappp.Activity.Activity.UserOrderHeaderActivity;
import com.example.groceryappp.Activity.Fragment.SellerOrderFragment;
import com.example.groceryappp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID";
    FirebaseAuth auth;
    NotificationManager manager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        ///all notification wl rcvd here

        auth = FirebaseAuth.getInstance();
        String notificationType = message.getData().get("notificationtype");
        if (notificationType.equals("New Order")) {
            String buyeruid = message.getData().get("buyerid");
            String orderId = message.getData().get("orderid");
            String sellerId = message.getData().get("sellerid");
            String notificationTitle = message.getData().get("notificationtitle");
            String notificationmessage = message.getData().get("notificationmessage");
            if (auth.getUid().equals(
                    "YC7vLsrOpiVkMBqOcseWHL1BLTH3")) {
                ShowNotification(orderId, buyeruid, sellerId, notificationTitle, notificationmessage, notificationType);
                Log.d("mine", "onMessageReceived: " + message.getData().toString());

            }

        }
        if (notificationType.equals("Cancel Order")) {
            String buyeruid = message.getData().get("buyerid");
            String orderId = message.getData().get("orderid");
            String sellerId = message.getData().get("sellerid");
            String notificationTitle = message.getData().get("notificationtitle");
            String notificationmessage = message.getData().get("notificationmessage");
            if (auth.getUid().equals(
                    "YC7vLsrOpiVkMBqOcseWHL1BLTH3")) {
                ShowNotification(orderId, buyeruid, sellerId, notificationTitle, notificationmessage, notificationType);
                Log.d("mine", "onMessageReceived: " + message.getData().toString());

            }

        }
        if (notificationType.equals("Order Status Changed")) {
            String selleruid = message.getData().get("sellerid");
            String orderId = message.getData().get("orderid");
            String buyerid = message.getData().get("buyerid");
            String notificationTitle = message.getData().get("notificationtitle");
            String notificationmessage = message.getData().get("notificationmessage");
            if (auth.getUid().equals(buyerid)) {
                ShowNotification(orderId, buyerid, selleruid, notificationTitle, notificationmessage, notificationType);
                Log.d("mine", "onMessageReceived: " + message.getData().toString());

            }
        }


    }

    private void ShowNotification(String orderId, String buyerId, String sellerId, String notificationTitle, String notificationMessage, String notificationType) {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        ////id required for notification random
        int id = new Random().nextInt();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setNotificationChannel(manager);
        }

////handel when user/seller click this notification navigate to orderdetails activity
        Intent intent = null;
        if (notificationType.equals("New Order")) {
            intent = new Intent(this, SellerOrderDetailsActivity.class);
            intent.putExtra("orderId", orderId);


            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);



        }
        else if (notificationType.equals("Order Status Changed")) {
            intent = new Intent(this, UserOrderDetailsActivity.class);
           intent.putExtra("orderId", orderId);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        } else if (notificationType.equals("Cancel Order")) {
            intent = new Intent(this, UserOrderDetailsActivity.class);
           intent.putExtra("orderId", orderId);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        }
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
        ///Large Icon
        Bitmap largeicon = BitmapFactory.decodeResource(getResources(), R.drawable.shop);
        //sound notification
        Uri notificationuri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setLargeIcon(largeicon);
        notificationBuilder.setSound(notificationuri).setContentTitle(notificationTitle).setContentIntent(pendingIntent);
        notificationBuilder.setSmallIcon(R.drawable.shop);
        ///show notification
        manager.notify(id, notificationBuilder.build());
    }


    private void setNotificationChannel(NotificationManager manager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Sample", NotificationManager.IMPORTANCE_HIGH);

            channel.setLightColor(Color.RED);
            channel.enableLights(true);
            channel.enableVibration(true);
            if (manager != null) {
                manager.createNotificationChannel(channel);

            }


        }


    }
}
