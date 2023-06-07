package com.example.groceryappp.Activity.AllModel;

public class UserOrderHeader {
    String name,date,orderId,number,imgUrl,adress,status,uid,longitude,lattitude;
int total;

    public String getAdress() {
        return adress;
    }

    public UserOrderHeader(String name, String date, String orderId, String number, String adress, String status, String longitude, String lattitude, int total) {
        this.name = name;
        this.date = date;
        this.orderId = orderId;
        this.number = number;
        this.adress = adress;
        this.status = status;
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.total = total;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public UserOrderHeader() {
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public UserOrderHeader(String name, int total, String date, String orderId, String number) {
        this.name = name;
        this.total = total;
        this.date = date;
        this.orderId = orderId;
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


}
