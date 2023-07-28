package com.example.groceryappp.Activity.AllModel;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class SingleProductDetails implements Serializable {
    @Exclude String id;
    public static final int VIEW_TYPE_SINGLE_CART=1;
    String name,type,imgUri,desc,availiable,qty,img, category;


    int marktPrice,price,unit,totalprice,viewType,profile_pic;


    public SingleProductDetails(String name, int price) {
        this.name=name;
        this.price=price;
    }
    public SingleProductDetails(String name, int price, String qty, String availiable, String category, int marktPrice,String imgUri) {
        this.availiable = availiable;
        this.category = category;
        this.imgUri = imgUri;
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.marktPrice = marktPrice;
        this.price = price;

    }

    public String getImg() {
        return img;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setImg(String img) {
        this.img = img;
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

    public SingleProductDetails(String name, String qty, String imgUri, int price, int unit, int totalprice) {
        this.name = name;
        this.qty = qty;
        this.imgUri = imgUri;
        this.price = price;
        this.unit = unit;
        this.totalprice = totalprice;
    }

    public SingleProductDetails(String name, String imgUri, String qty, int price, int marktPrice, String id) {
        this.name=name;
        this.imgUri=imgUri;
        this.qty=qty;
        this.price=price;
        this.marktPrice=marktPrice;
        this.id=id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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







    public SingleProductDetails() {
    }

    public SingleProductDetails(String name, int price,String qty) {
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public String getType() {
        return type;
    }



    public void setType(String type) {
        this.type = type;
    }






    public int getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(int profile_pic) {
        this.profile_pic = profile_pic;
    }
}
