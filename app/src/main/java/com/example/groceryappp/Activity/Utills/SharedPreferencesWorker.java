package com.example.groceryappp.Activity.Utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SharedPreferencesWorker extends Worker {

    public SharedPreferencesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Remove the key from SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("product list");
        editor.apply();
        Toast.makeText(getApplicationContext(), "remove", Toast.LENGTH_SHORT).show();
        return Result.success();
    }
}
