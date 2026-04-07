package com.example.siduraboda.models;


import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class Lesson {
    private String id;

    private String teacherId; // driving teacher id
    private String studentId; // student id
    private Car car;
    private DayAndHours dayAndHours;
    private String date;


    public Lesson() {
    }

    public Lesson(String id, String teacherId, String studentId, Car car, DayAndHours dayAndHours, String date) {
        this.id = id;

        this.teacherId = teacherId;
        this.studentId = studentId;
        this.car = car;
        this.dayAndHours = dayAndHours;
        this.date = date;


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
    public DayAndHours getDayAndHours() {return dayAndHours;}
    public void setDayAndHours(DayAndHours dayAndHours) {this.dayAndHours = dayAndHours;}

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}


    @NonNull
    @Override
    public String toString() {
        return "Lesson{" +
                "id='" + id + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", car=" + car +
                '}';
    }
}