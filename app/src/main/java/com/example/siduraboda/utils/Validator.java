package com.example.siduraboda.utils;

import android.util.Patterns;

import androidx.annotation.Nullable;

/// Validator class to validate user input.
/// This class contains static methods to validate user input,
/// like email, password, phone, name etc.
public class Validator {


    /// Check if the password is valid
    /// @param password password to validate
    /// @return true if the password is valid, false otherwise
    public static boolean isPasswordValid(@Nullable String password) {
        return password != null && password.length() >= 4;
    }

    /// Check if the phone number is valid
    /// @param phone phone number to validate
    /// @return true if the phone number is valid, false otherwise
    /// @see Patterns#PHONE
    public static boolean isPhoneValid(@Nullable String phone) {
        return phone != null && phone.length() >= 10 && Patterns.PHONE.matcher(phone).matches();
    }

    /// Check if the name is valid
    /// @param name name to validate
    /// @return true if the name is valid, false otherwise
    public static boolean isNameValid(@Nullable String name) {
        return name != null && name.length() >= 2;
    }

    public static boolean isLicenseValid(@Nullable String license) {
        return license != null && license.length() >= 7 && license != null && license.length() <= 9;
    }
}