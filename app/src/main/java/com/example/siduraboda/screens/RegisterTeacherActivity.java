package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.example.siduraboda.utils.Validator;

import java.util.ArrayList;

public class RegisterTeacherActivity extends BaseActivity implements View.OnClickListener {
    private EditText firstName, lastName, Phone, License, Password;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_teacher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firstName = findViewById(R.id.et_register_first_name);
        lastName = findViewById(R.id.et_register_last_name);
        Phone = findViewById(R.id.et_register_phone);
        License = findViewById(R.id.et_register_num_license);
        Password = findViewById(R.id.et_register_password);
        btnRegister = findViewById(R.id.btnupdate);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegister.getId()) {

            /// get the input from the teacher
            String fName = firstName.getText().toString();
            String lName = lastName.getText().toString();
            String phone = Phone.getText().toString();
            String license = License.getText().toString();
            String password = Password.getText().toString();

            if (checkInput(fName, lName, phone, license, password)) {
                registerTeacher(fName, lName, phone, license, password);
            }

        }
    }

    /// Check if the input is valid
    ///
    /// @return true if the input is valid, false otherwise
    private boolean checkInput(String fName, String lName, String phone, String license, String password) {

        if (!Validator.isNameValid(fName)) {
            firstName.setError("First name must be at least 2 characters long");
            firstName.requestFocus();
            return false;
        }

        if (!Validator.isNameValid(lName)) {
            lastName.setError("Last name must be at least 2 characters long");
            lastName.requestFocus();
            return false;
        }

        if (!Validator.isPhoneValid(phone)) {
            Phone.setError("Invalid Phone address");
            Phone.requestFocus();
            return false;
        }

        if (!Validator.isLicenseValid(license)) {
            License.setError("license must be at least 7 characters long and max 9 characters long");
            License.requestFocus();
            return false;
        }

        if (!Validator.isPasswordValid(password)) {
            Password.setError("Password must be at least 6 characters long");
            Password.requestFocus();
            return false;
        }

        return true;
    }

    /// Register the teacher
    private void registerTeacher(String fName, String lName, String phone, String license, String password) {
        String uid = databaseService.generateUserId();

        Teacher teacher = new Teacher(uid, password, fName, lName, license, phone, false, new ArrayList<>());

        databaseService.checkIfPhoneExists(phone, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean phoneExists) {

                if (phoneExists) {
                    Toast.makeText(RegisterTeacherActivity.this, "מספר טלפון כבר קיים", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseService.checkIfLicenseNum(license, new DatabaseService.DatabaseCallback<Boolean>() {
                    @Override
                    public void onCompleted(Boolean licenseExists) {

                        if (licenseExists) {
                            Toast.makeText(RegisterTeacherActivity.this, "מספר רישיון כבר קיים", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        createTeacherInDatabase(teacher);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(RegisterTeacherActivity.this, "שגיאה בבדיקת מספר רישיון", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(RegisterTeacherActivity.this, "שגיאה בבדיקת מספר טלפון", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTeacherInDatabase(Teacher teacher) {
        databaseService.createNewUser(teacher, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                /// save the teacher to shared preferences
                SharedPreferencesUtil.saveTeacher(RegisterTeacherActivity.this, teacher);
                /// Redirect to MainActivity and clear back stack to prevent teacher from going back to register screen
                Intent mainIntent = new Intent(RegisterTeacherActivity.this, MainActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                /// show error message to teacher
                Toast.makeText(RegisterTeacherActivity.this, "Failed to register teacher", Toast.LENGTH_SHORT).show();
                /// sign out the teacher if failed to register
                SharedPreferencesUtil.signOutTeacher(RegisterTeacherActivity.this);
            }
        });
    }
}