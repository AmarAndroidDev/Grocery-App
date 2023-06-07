package com.example.groceryappp.Activity.AllModel;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class SingleOrderDetails implements Serializable {
    @Exclude
    String idd;
    String name,type,imgUrl,desc,availiable,qty;
    int marktPrice,viewType,unit;
    int price;

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    int profile_pic,totalprice;

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


public static final int VIEW_TYPE_SINGLE_CART=1;
public static final int VIEW_TYPE_SINGLE_ORDERS=2;

    public int getViewType() {
        return viewType;
    }



    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getIdd() {
        return idd;
    }

    public void setIdd(String idd) {
        this.idd = idd;
    }



    public void setImgUri(String imgUri) {
        this.imgUrl = imgUri;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getMarktPrice() {
        return marktPrice;
    }

    public void setMarktPrice(int marktPrice) {
        this.marktPrice = marktPrice;
    }

    public String getAvailiable() {
        return availiable;
    }

    public void setAvailiable(String availiable) {
        this.availiable = availiable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }






    public SingleOrderDetails() {
    }

    public SingleOrderDetails(String name, int price) {
        this.name = name;
        this.price = price;

    }

    public String getType() {
        return type;
    }



    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(int profile_pic) {
        this.profile_pic = profile_pic;
    }
}


