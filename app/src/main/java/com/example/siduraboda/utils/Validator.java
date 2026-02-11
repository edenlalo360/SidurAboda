package com.example.siduraboda.utils;

import android.util.Patterns;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;

/// Validator class to validate user input.
/// This class contains static methods to validate user input,
/// like email, password, phone, name etc.
public class Validator {


    /// תקינות קלט הרשמה

    public static boolean isPasswordValid(@Nullable String password) {
        return password != null && password.length() >= 4;
    }


    public static boolean isPhoneValid(@Nullable String phone) {
        return phone != null && phone.length() >= 10 && Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isNameValid(@Nullable String name) {
        return name != null && name.length() >= 2;
    }

    public static boolean isLicenseValid(@Nullable String license) {
        return license != null && license.length() >= 7 && license != null && license.length() <= 9;
    }

    ///תקינות קלט הוספת רכב וגם אודות רכב

    public static boolean isTypeValid(@Nullable String type) {
        return type != null && type.length() >= 2;
    }
    public static boolean isCarNumberValid(@Nullable String carnumber) {
        return carnumber != null && carnumber.length() >= 7 && carnumber.length() <= 8;
    }
    public static boolean isInsuranceDateValid(@Nullable LocalDate insurance) {
        return insurance != null ;
    }
    public static boolean isLicenseDateValid(@Nullable LocalDate license) {
        return license != null;
    }
    public static boolean isSpinnerValid(@Nullable String spinner) {
        return !spinner.equals("דרגת רישיון");
    }

    /// תקינות קלט קביעת שיעור
    public static boolean isLessonDateValid(@Nullable LocalDate date) {
        return date != null;
    }
    public static boolean isTimeValid(@Nullable LocalTime time) {
        return time != null;
    }
    public static boolean isSpinnerStudentValid(@Nullable String spinnerstudent) {
        return !spinnerstudent.equals("בחר תלמיד לקביעת שיעור");
    }public static boolean isSpinnerCarValid(@Nullable String spinnercar) {
        return !spinnercar.equals("בחר רכב לשיעור זה");
    }

}