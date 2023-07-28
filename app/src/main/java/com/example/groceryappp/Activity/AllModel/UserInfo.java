package com.example.groceryappp.Activity.AllModel;

import java.io.Serializable;

public class UserInfo implements Serializable {
    String uid,email,password,number,name,accountType,profilePic,apartment,city,locality,landmark,pincode,fullAd,latitude,longitude,date,orderId,imgUri,adress,status,deliverySlot,cTimePrgrs,cTimePacked,cTimeOutDelivery,cTimeDelivered,saved;
    int total,sum;

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }




    public String getcTimePrgrs() {
        return cTimePrgrs;
    }

    public void setcTimePrgrs(String cTimePrgrs) {
        this.cTimePrgrs = cTimePrgrs;
    }


    public String getcTimePacked() {
        return cTimePacked;
    }

    public void setcTimePacked(String cTimePacked) {
        this.cTimePacked = cTimePacked;
    }


    public String getcTimeOutDelivery() {
        return cTimeOutDelivery;
    }

    public void setcTimeOutDelivery(String cTimeOutDelivery) {
        this.cTimeOutDelivery = cTimeOutDelivery;
    }


    public String getcTimeDelivered() {
        return cTimeDelivered;
    }

    public void setcTimeDelivered(String cTimeDelivered) {
        this.cTimeDelivered = cTimeDelivered;
    }

    public UserInfo() {

    }

    public UserInfo(String name, String orderId, String date, String number, String fullAd, String status, String longitude, String latitude,
                    int summ, String deliverySlot, String uid,String saved,String cTimePrgrs , String cTimePacked,String cTimeOutDelivery ,String cTimeDelivered) {
this.name=name;
this.orderId=orderId;
this.date=date;
this.number=number;
this.fullAd=fullAd;
this.status=status;
this.longitude=longitude;
this.latitude=latitude;
this.sum=summ;
this.deliverySlot=deliverySlot;
this.uid=uid;
this.saved=saved;
this.cTimePrgrs=cTimePrgrs;
this.cTimePacked=cTimePacked;
this.cTimeOutDelivery=cTimeOutDelivery;
this.cTimeDelivered=cTimeDelivered;

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

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public UserInfo(String uid, String email, String password, String number, String name, String accountType, String profilePic,String city, String locality, String landmark, String pincode, String fullAd, String latitude, String longitude) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.number = number;
        this.name = name;
        this.accountType = accountType;
        this.profilePic = profilePic;
        this.city = city;
        this.locality = locality;
        this.landmark = landmark;
        this.pincode = pincode;
        this.fullAd = fullAd;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getFullAd() {
        return fullAd;
    }

    public void setFullAd(String fullAd) {
        this.fullAd = fullAd;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }




}
