package com.example.groceryappp.Activity.AllModel;

public class SingleItemDetails {
    String name,imgUri,qty;
    int totalprice,price,unit;

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

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

    public SingleItemDetails(String name, int price, String qty, int totalprice,int unit,String imgUri) {
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.totalprice = totalprice;
        this.imgUri = imgUri;

        this.unit = unit;
    }
}
