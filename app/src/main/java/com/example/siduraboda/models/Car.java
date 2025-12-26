package com.example.siduraboda.models;

import androidx.annotation.NonNull;

public class Car {

    private String id;
    private String type;
    private String spinnerRank;
    private String carNumber;
    private String licenseId;

    public Car() {
    }

    public Car(String id, String type, String spinnerRank, String carNumber, String licenseId) {
        this.id = id;
        this.type = type;
        this.spinnerRank = spinnerRank;
        this.carNumber = carNumber;
        this.licenseId = licenseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpinnerRank() {
        return spinnerRank;
    }

    public void setSpinnerRank(String spinnerRank) {
        this.spinnerRank = spinnerRank;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", spinnerRank='" + spinnerRank + '\'' +
                ", carNumber='" + carNumber + '\'' +
                ", licenseId='" + licenseId + '\'' +
                '}';
    }
}
