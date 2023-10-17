package com.example.groceryappp.Activity.Utills;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils {
    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    public Boolean isEvenNumber(int  input){
        if (input % 2==0){
            return true;
        }
        return false;
    }
    public Boolean isOddNumber(int  input){
        if (input % 2!=0){
            return true;
        }
        return false;
    }
    public String isValidPassword(String inputPas){
       try {
          if (inputPas.isEmpty()){
              throw new IllegalArgumentException("Null pointer Exception");

          }
          if (inputPas.length()>=6 &&inputPas.length()<=15){
              return "Valid password";
          }
       }catch (Exception e){
throw e;
       }
       return "Wrong Password";
    }
}
