package com.example.groceryappp.Activity.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.groceryappp.Activity.AllModel.SingleCartDetails;
import com.example.groceryappp.Activity.AllModel.SingleProductDetails;

@Database(entities = SingleCartDetails.class,version = 1,exportSchema = true)

public abstract class DatabaseHelper extends RoomDatabase {
    private static final String DB_NAME="MyCart";
private static  DatabaseHelper databaseHelper;
public static synchronized DatabaseHelper getInstance(Context context){
    if (databaseHelper==null){
        databaseHelper= Room.databaseBuilder(context,DatabaseHelper.class,DB_NAME).allowMainThreadQueries().build();
    }
        return databaseHelper;
}

public abstract CartDao getDao();


}
