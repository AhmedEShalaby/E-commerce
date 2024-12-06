package com.example.e_commerce;

public class User {

    private String name;
    private String email;
    private String password;
    private String address;
    private String number;
    private String birthdate;
    private boolean isAdmin;

    public User(String name, String email, String password, String address, String number, String birthdate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.number = number;
        this.birthdate = birthdate;
        //this.isAdmin = isAdmin;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
