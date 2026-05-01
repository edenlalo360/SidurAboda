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
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.example.siduraboda.utils.Validator;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddStudentActivity extends AppCompatActivity {
    private EditText Name, address, phone, date, password;
    private Switch switchTheory, switchEyes, switchHealth;

    private Button btnAddStudent;
    private String editingStudentId = null;
    private Student editingStudent = null;

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

        editingStudentId = getIntent().getStringExtra("EDIT_STUDENT_ID");

        Name = findViewById(R.id.studentName);
        date = findViewById(R.id.studentBirth);
        address = findViewById(R.id.studentAddress);
        phone = findViewById(R.id.studentPhone);
        password = findViewById(R.id.studentPassword);
        btnAddStudent = findViewById(R.id.btn_AddStudent);
        switchTheory = findViewById(R.id.switchTheory);
        switchEyes = findViewById(R.id.switchEyes);
        switchHealth = findViewById(R.id.switchHealth);

        if (editingStudentId != null) {
            btnAddStudent.setText("עדכון תלמיד");
            loadStudentData();
        }

        date.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.YEAR, -16);
            maxDate.add(Calendar.MONTH, -6);

            DatePickerDialog picker = new DatePickerDialog(this, (view, y, m, d) -> {
                date.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d));
            },
                    maxDate.get(Calendar.YEAR),
                    maxDate.get(Calendar.MONTH),
                    maxDate.get(Calendar.DAY_OF_MONTH));

            picker.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
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
                Date dateObj;
                try {
                    dateObj = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
                } catch (Exception e) {
                    date.setError("Invalid date format");
                    date.requestFocus();
                    return;
                }

                String studentId = editingStudentId != null ? editingStudentId : DatabaseService.getInstance().generateStudentId();
                String teacherId = SharedPreferencesUtil.getTeacherId(this);
                String status = editingStudent != null ? editingStudent.getStatus() : "בתהליך";

                Student newStudent = new Student(
                        studentId,
                        nameStr,
                        dateObj,
                        addressStr,
                        phoneStr,
                        passwordStr,
                        theoryIsChecked,
                        eyesIsChecked,
                        healthIsChecked,
                        teacherId,
                        status
                );

                DatabaseService.getInstance().createNewStudent(newStudent, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        runOnUiThread(() -> {
                            String msg = editingStudentId != null ? "Student updated successfully!" : "Student added successfully!";
                            Toast.makeText(AddStudentActivity.this, msg, Toast.LENGTH_SHORT).show();
                            if (editingStudentId != null) finish();
                            else ClearFields();
                        });
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

    private void loadStudentData() {
        DatabaseService.getInstance().getStudent(editingStudentId, new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                if (student != null) {
                    editingStudent = student;
                    Name.setText(student.getName());
                    if (student.getBirthdate() != null) {
                        date.setText(new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(student.getBirthdate()));
                    }
                    address.setText(student.getAddress());
                    phone.setText(student.getPhone());
                    password.setText(student.getPassword());
                    switchTheory.setChecked(student.getTheory() != null && student.getTheory());
                    switchEyes.setChecked(student.getCheckeye() != null && student.getCheckeye());
                    switchHealth.setChecked(student.getHealthdec() != null && student.getHealthdec());
                }
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AddStudentActivity.this, "שגיאה בטעינת נתוני תלמיד", Toast.LENGTH_SHORT).show();
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
        if (addressText.isEmpty()) {
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
