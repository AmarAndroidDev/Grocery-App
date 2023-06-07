package com.example.groceryappp.Activity.AllModel;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class AdminList implements Serializable {
    @Exclude String id;
    String Name,qty,availiable,category;
    int  marktPrice,price;
    public String getImgUri() {
        return imgUri;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getId() {
        return id;
    }
    String imgUri;

    public AdminList() {
    }

    public void setId(String id) {
        this.id = id;
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

    public AdminList(String name, int price, String qty, String availiable, String category, int marktPrice) {
        Name = name;
        this.price = price;
        this.qty = qty;
        this.category = category;
        this.availiable = availiable;
        this.marktPrice = marktPrice;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }





    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAvailiable() {
        return availiable;
    }

    public void setAvailiable(String availiable) {
        this.availiable = availiable;
    }


}
