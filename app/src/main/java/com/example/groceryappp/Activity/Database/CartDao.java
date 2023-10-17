package com.example.groceryappp.Activity.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.groceryappp.Activity.AllModel.SingleCartDetails;


import java.util.List;

@Dao
public interface CartDao {

@Insert()
public void addToCart( SingleCartDetails details);
@Query("select exists(select * from SingleCartDetails where id= :idd)")
Boolean isExistCart(String idd);
@Query("select * from SingleCartDetails")
public List<SingleCartDetails> getAllCartDetails();
@Query("select * from SingleCartDetails where id=:idd")
public SingleCartDetails getSingleCartDetails(String idd);
    @Query("UPDATE SingleCartDetails SET unit = :newQuantity WHERE id = :itemId")
    void incrementCart(String  itemId, int newQuantity);
    @Query("DELETE FROM SingleCartDetails  WHERE id = :itemId")
    void deleteCart(String  itemId);

}
