package com.example.siduraboda.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class User {
    private String uid;
    private String password;
    private String firstName;
    private String lastName;
    private String licenseId;
    private String phone;
    private ArrayList<Car> cars;

    boolean isAdmin;
    public User() {}

    public User(String uid, String password, String firstName,
                String lastName, String licenseId, String phone, boolean isAdmin, ArrayList<Car> cars) {
        this.uid = uid;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.licenseId = licenseId;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.cars = cars;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @NonNull
    public ArrayList<Car> getCars() {
        if (this.cars == null
          ) {
            this.cars = new ArrayList<>();
        }
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", licenseId='" + licenseId + '\'' +
                ", phone='" + phone + '\'' +
                ", isAdmin=" + isAdmin +
                ", cars=" + cars +
                '}';
    }
}
