package com.example.groceryappp.Activity.AllModel;

public class SingleItemDetails {
    String name,imgUrl,qty;
    int totalprice,price,unit;

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

    public SingleItemDetails(String name, int price, String qty, int totalprice,String imgUrl,int unit) {
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.totalprice = totalprice;
        this.imgUrl = imgUrl;
        this.unit = unit;
    }
}
