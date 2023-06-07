package com.example.groceryappp.Activity.AllModel;

public class ModelRegister {
    String paswd,name,email,number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPaswd() {
        return paswd;
    }

    public void setPaswd(String paswd) {
        this.paswd = paswd;
    }


    public ModelRegister(String paswd, String name, String email, String number) {
        this.paswd = paswd;
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
