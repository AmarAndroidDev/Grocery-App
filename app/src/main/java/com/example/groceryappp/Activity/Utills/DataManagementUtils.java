package com.example.groceryappp.Activity.Utills;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class DataManagementUtils {
    private static final String DELETE_WORK_TAG = "delete_work";

    public static void scheduleDataDeletion(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true) // Optional constraints
                .build();

        PeriodicWorkRequest deletionWorkRequest =
                new PeriodicWorkRequest.Builder(DeleteDataWorker.class, 1, TimeUnit.MINUTES)
                        .addTag(DELETE_WORK_TAG)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                DELETE_WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                deletionWorkRequest

        );
    }
}
