package com.example.siduraboda.models;

import androidx.annotation.NonNull;

public class Car {

    private String type;
    private String rank;
    private String carNumber;
    private String licenseDate;
    private String insuranceDate;

    public Car() {
    }

    public Car(String type, String rank, String carNumber, String licenseDate, String insuranceDate) {
        this.type = type;
        this.rank = rank;
        this.carNumber = carNumber;
        this.licenseDate = licenseDate;
        this.insuranceDate = insuranceDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getLicenseDate() {
        return licenseDate;
    }

    public void setLicenseDate(String licenseDate) {
        this.licenseDate = licenseDate;
    }

    public String getInsuranceDate() {
        return insuranceDate;
    }

    public void setInsuranceDate(String insuranceDate) {
        this.insuranceDate = insuranceDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Car{" +
                "type='" + type + '\'' +
                ", rank='" + rank + '\'' +
                ", carNumber='" + carNumber + '\'' +
                ", licenseId='" + licenseDate + '\'' +
                ", insuranceDate='" + insuranceDate + '\'' +
                '}';
    }
}
