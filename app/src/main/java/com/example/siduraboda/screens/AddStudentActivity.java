package com.example.siduraboda.screens;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Student;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.Validator;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddStudentActivity extends AppCompatActivity {
    private EditText Name, address, phone, date, password;
    private Switch switchTheory, switchEyes, switchHealth;

    private Button btnAddStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button button14 = findViewById(R.id.addstudentTOmain); //הוספת תלמיד לדף הבית
        button14.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(AddStudentActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
        );

        Name = findViewById(R.id.studentName);
        date = findViewById(R.id.studentBirth);
        address = findViewById(R.id.studentAddress);
        phone = findViewById(R.id.studentPhone);
        password = findViewById(R.id.studentPassword);
        btnAddStudent = findViewById(R.id.btn_AddStudent);
        switchTheory = findViewById(R.id.switchTheory);
        switchEyes = findViewById(R.id.switchEyes);
        switchHealth = findViewById(R.id.switchHealth);

        date.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            DatePickerDialog picker = new DatePickerDialog(this, (view, y, m, d) -> {
                date.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            picker.show();
        });


        btnAddStudent.setOnClickListener(v -> {
            String nameStr = Name.getText().toString().trim();
            String phoneStr = phone.getText().toString().trim();
            String addressStr = address.getText().toString().trim();
            String dateStr = date.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();
            boolean theoryIsChecked = switchTheory.isChecked();
            boolean eyesIsChecked = switchEyes.isChecked();
            boolean healthIsChecked = switchHealth.isChecked();


            if (check(nameStr, phoneStr, addressStr, dateStr, passwordStr)) {

                // המרת תאריך
                Date dateObj;
                try {
                    dateObj = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
                } catch (Exception e) {
                    date.setError("Invalid date format");
                    date.requestFocus();
                    return;
                }

                String studentId = DatabaseService.getInstance().generateStudentId();

                // יצירת אובייקט תלמיד
                Student newStudent = new Student(
                        studentId,
                        nameStr,
                        dateObj,
                        addressStr,
                        phoneStr,
                        passwordStr,
                        theoryIsChecked,   // theory
                        eyesIsChecked,   // checkeye
                        healthIsChecked    // healthdec
                );

                DatabaseService.getInstance().createNewStudent(newStudent, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddStudentActivity.this, "Student added successfully!", Toast.LENGTH_SHORT).show();
                        });
                        ClearFields();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddStudentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

        });

    }

    private boolean check(String name, String phoneNumber, String addressText, String dateText, String Password) {
        if (!Validator.isNameValid(name)) {
            Name.setError("Name must be at least 2 characters long");
            Name.requestFocus();
            return false;
        }

        if (!Validator.isPhoneValid(phoneNumber)) {
            phone.setError("Invalid Phone address");
            phone.requestFocus();
            return false;
        }

        if (addressText == "") {
            address.setError("Enter address");
            address.requestFocus();
            return false;
        }

        if (dateText.isEmpty()) {
            date.setError("Please enter a date");
            date.requestFocus();
            return false;
        }
        if (!Validator.isPasswordValid(Password)) {
            password.setError("Password must be at least 6 characters long");
            password.requestFocus();
            return false;
        }


        return true;
    }

    public void ClearFields() {
        Name.setText("");
        date.setText("");
        address.setText("");
        phone.setText("");
        password.setText("");
        switchTheory.setChecked(false);
        switchEyes.setChecked(false);
        switchHealth.setChecked(false);


    }

}
