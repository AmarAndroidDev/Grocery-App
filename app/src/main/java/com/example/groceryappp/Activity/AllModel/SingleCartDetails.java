package com.example.groceryappp.Activity.AllModel;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "SingleCartDetails")
public class SingleCartDetails implements Serializable {
 @PrimaryKey(autoGenerate = false)
 @NonNull
public String id;
    @ColumnInfo(name ="name")
private String name;
    @ColumnInfo(name ="imgUri")
private String imgUri;
    @ColumnInfo(name ="qty")
private String qty;

    @ColumnInfo(name ="marktPrice")
private int marktPrice;
    @ColumnInfo(name ="price")
private int price;
    @ColumnInfo(name ="totalprice")
private int totalprice;
    @ColumnInfo(name ="unit")
private int unit;

    public SingleCartDetails(String id, String name, String imgUri, String qty, int marktPrice, int price, int totalprice, int unit) {
        this.id = id;
        this.name = name;
        this.imgUri = imgUri;
        this.qty = qty;
        this.marktPrice = marktPrice;
        this.price = price;
        this.totalprice = totalprice;
        this.unit = unit;
    }

    public String  getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public int getMarktPrice() {
        return marktPrice;
    }

    public void setMarktPrice(int marktPrice) {
        this.marktPrice = marktPrice;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }
}
