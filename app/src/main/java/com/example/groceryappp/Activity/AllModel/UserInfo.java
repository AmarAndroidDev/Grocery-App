package com.example.groceryappp.Activity.AllModel;

public class UserInfo {
    String uid,email,password,number,name,accountType,profilePic,apartment,city,locality,landmark,pincode,fullAd;

    public UserInfo(String uid, String email, String password, String number, String name, String accountType, String profilePic, String apartment, String city, String locality, String landmark, String pincode,String fullAd) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.number = number;
        this.name = name;
        this.accountType = accountType;
        this.profilePic = profilePic;
        this.apartment = apartment;
        this.city = city;
        this.locality = locality;
        this.landmark = landmark;
        this.pincode = pincode;
        this.fullAd = fullAd;

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
