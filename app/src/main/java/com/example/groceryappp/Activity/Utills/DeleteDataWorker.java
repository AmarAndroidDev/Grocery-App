package com.example.groceryappp.Activity.Utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DeleteDataWorker extends Worker {
    private static final String DATA_PREFS_NAME = "MySharedPreferences";
    private static final String DATA_KEY = "list";

    public DeleteDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Simulate some time-consuming task
            Thread.sleep(30000); // Sleep for 5 seconds

            // Get access to the SharedPreferences
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
            // Clear the data from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(DATA_KEY);
            editor.apply();
            Toast.makeText(getApplicationContext(), "Sucess", Toast.LENGTH_SHORT).show();
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }
}
