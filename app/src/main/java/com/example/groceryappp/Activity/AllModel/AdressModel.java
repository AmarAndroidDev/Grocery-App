package com.example.groceryappp.Activity.AllModel;

import java.io.Serializable;

public class AdressModel implements Serializable {
    String locality,pincode,apartment,city,street,fulladress,latitude,longitude,house;

    public AdressModel() {
    }

    public AdressModel(String locality, String pincode, String apartment, String city, String street, String fulladress, String latitude, String longitude) {
        this.locality = locality;
        this.pincode = pincode;
        this.apartment = apartment;
        this.city = city;
        this.street = street;
        this.fulladress = fulladress;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getFulladress() {
        return fulladress;
    }

    public void setFulladress(String fulladress) {
        this.fulladress = fulladress;
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

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }
}
