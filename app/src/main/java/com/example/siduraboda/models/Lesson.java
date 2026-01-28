package com.example.siduraboda.models;


import androidx.annotation.NonNull;

public class Lesson {
    private String startTime;
    private String endTime;
    private String UserId;

    public Lesson(String startTime, String endTime, String UserId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.UserId = UserId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Lesson{" +
                "startTime'" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", UserId='" + UserId + '\'' +
                '}';
    }
}