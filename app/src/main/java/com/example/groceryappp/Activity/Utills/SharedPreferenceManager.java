package com.example.groceryappp.Activity.Utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.groceryappp.Activity.AllModel.SingleProductDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferenceManager {

    public static final String USER_INFO_PREF_NAME = "userInfo";

    public static final String SINGLE_PRODCT_PREF_NAME = "product list";
    public static final String SELLER_CATEGORY_PREF_NAME = "sellerCategory";
 private static  SharedPreferenceManager sharedPreferenceManager;
 private SharedPreferences preferences;

 private   SharedPreferenceManager(Context context){

     preferences=context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
 }
 public static  synchronized SharedPreferenceManager getInstance(Context context){
     if (sharedPreferenceManager==null){
         sharedPreferenceManager=new SharedPreferenceManager(context);
     }
     return sharedPreferenceManager;
 }
 public void storeUserId(String userId){
     SharedPreferences.Editor editor = preferences.edit();
     editor.putString("USER_ID", userId);
     editor.apply();

 }
 public String getUserId(){
     String userId = preferences.getString("USER_ID", "");
     return userId;
 }
    public  void storeSingleProductDetailsinSharedP(ArrayList<SingleProductDetails> data,Context context,String key) {
        SharedPreferences.Editor editor = preferences.edit();
        // Convert the list of data into a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(data);
        // Put the JSON string into SharedPreferences
        editor.putString(key, json);
        // Apply the changes to persist the data
        editor.apply();
//        Toast.makeText(context, "success store--"+key, Toast.LENGTH_SHORT).show();

    }
/*    public  void storeNewCartListSharedP(ArrayList<SingleProductDetails> data,Context context,String key) {
     ArrayList<SingleProductDetails> prevCartList=retrieveSingleProductSharedP(context,USER_CART_PREF_NAME);
     data.addAll(prevCartList);
        SharedPreferences.Editor editor = preferences.edit();
        // Convert the list of data into a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(data);
        // Put the JSON string into SharedPreferences
        editor.putString(key, json);
        // Apply the changes to persist the data
        editor.apply();
        Toast.makeText(context, "new cart added"+key, Toast.LENGTH_SHORT).show();

    }
    public  void removeCartListSharedP(ArrayList<SingleProductDetails> data,Context context,String key) {
     ArrayList<SingleProductDetails> prevCartList=retrieveSingleProductSharedP(context,USER_CART_PREF_NAME);
     String prevId=null;
     for (SingleProductDetails d:prevCartList){
         prevId=d.getId();
     }

     prevCartList.remove(prevId.contains(data.get(0).getId()));
        SharedPreferences.Editor editor = preferences.edit();
        // Convert the list of data into a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(data);
        // Put the JSON string into SharedPreferences
        editor.putString(key, json);
        // Apply the changes to persist the data
        editor.apply();
        Toast.makeText(context, "new cart added"+key, Toast.LENGTH_SHORT).show();

    }*/
    public  void storeUserInfoDetailsinSharedP(UserInfo data, Context context, String key) {
        SharedPreferences.Editor editor = preferences.edit();
        // Convert the list of data into a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(data);
        // Put the JSON string into SharedPreferences
        editor.putString(key, json);
        // Apply the changes to persist the data
        editor.apply();
        //Toast.makeText(context, "success store"+"--"+key, Toast.LENGTH_SHORT).show();
    }
    public  void storeSearchDetailsinSharedP(String history, Context context, String key) {
        SharedPreferences.Editor editor = preferences.edit();
        // Convert the list of data into a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(history);
        // Put the JSON string into SharedPreferences
        editor.putString(key, json);
        // Apply the changes to persist the data
        editor.apply();
        //Toast.makeText(context, "success store"+"--"+key, Toast.LENGTH_SHORT).show();
    }

    public ArrayList<SingleProductDetails> retrieveSingleProductSharedP(Context context, String retriveKey) {
        // Retrieve the JSON string from SharedPreferences
        String json = preferences.getString(retriveKey, "");
        // Convert the JSON string back to a list of data
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SingleProductDetails>>() {}.getType();
        ArrayList<SingleProductDetails> data = gson.fromJson(json, type);
//        Toast.makeText(context, " retrive"+"--"+retriveKey, Toast.LENGTH_SHORT).show();
        // Return the list of data
      //  Toast.makeText(context, "store"+data.size(), Toast.LENGTH_SHORT).show();

        return data;

    }
    public ArrayList<String> retrieveSearchHistorySharedP(Context context, String retriveKey) {
        // Retrieve the JSON string from SharedPreferences
        String json = preferences.getString(retriveKey, "");
        // Convert the JSON string back to a list of data
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SingleProductDetails>>() {}.getType();
        ArrayList<String> data = gson.fromJson(json, type);
//        Toast.makeText(context, " retrive"+"--"+retriveKey, Toast.LENGTH_SHORT).show();
        // Return the list of data
        Toast.makeText(context, "update"+data.size(), Toast.LENGTH_SHORT).show();
        return data;

    }
    public UserInfo retrieveUserInfoSharedP(Context context,String retriveKey) {
        // Retrieve the JSON string from SharedPreferences
        String json = preferences.getString(retriveKey, "");
        // Convert the JSON string back to a list of data
        Gson gson = new Gson();
   UserInfo data = gson.fromJson(json, UserInfo.class);
       // Toast.makeText(context, "sucess retrive"+retriveKey, Toast.LENGTH_SHORT).show();
        // Return the list of data
        return data;
    }
    public void removeKeyFromSharedPreferences(Context context,String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
//        Toast.makeText(context, "remove"+key, Toast.LENGTH_SHORT).show();
    }
}
