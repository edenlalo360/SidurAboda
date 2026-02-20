package com.example.siduraboda.models;

import java.util.Date;

public class Student {

    private String id;
    private String Name;
    private Date birthdate;
    private String address;
    private String phone;
    private String password;
    private Boolean theory;
    private Boolean checkeye;
    private Boolean healthdec;

    public Student() {
    }

    public Student(String id, String Name, Date birthdate, String address, String phone, String password, boolean theory, boolean checkeye, boolean healthdec) {
        this.id = id;
        this.Name = Name;
        this.birthdate = birthdate;
        this.address = address;
        this.phone = phone;
        this.password = password;
        this.theory = theory;
        this.checkeye = checkeye;
        this.healthdec = healthdec;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getTheory() {
        return theory;
    }

    public void setTheory(Boolean theory) {
        this.theory = theory;
    }

    public Boolean getCheckeye() {
        return checkeye;
    }

    public void setCheckeye(Boolean checkeye) {
        this.checkeye = checkeye;
    }

    public Boolean getHealthdec() {
        return healthdec;
    }

    public void setHealthdec(Boolean healthdec) {
        this.healthdec = healthdec;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", Name='" + Name + '\'' +
                ", birthdate=" + birthdate +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", theory=" + theory +
                ", checkeye=" + checkeye +
                ", healthdec=" + healthdec +
                '}';
    }
}
