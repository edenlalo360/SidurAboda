package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Student;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.example.siduraboda.utils.Validator;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etPhone, etPassword;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// get the views
        etPhone = findViewById(R.id.editTextPhone);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogIn);

        /// set the click listener
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnLogin.getId()) {
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();

            /// Validate input
            if (!checkInput(phone, password)) {
                /// stop if input is invalid
                return;
            }

            /// Login teacher
            loginTeacher(phone, password);
        }
    }

    private boolean checkInput(String phone, String password) {
        if (!Validator.isPhoneValid(phone)) {
            etPhone.setError("Invalid phone address");
            etPhone.requestFocus();
            return false;
        }

        if (!Validator.isPasswordValid(password)) {
            etPassword.setError("Password must be at least 6 characters long");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loginTeacher(String phone, String password) {
        DatabaseService.getInstance().getUserByPhoneAndPassword(phone, password, new DatabaseService.DatabaseCallback<Teacher>() {
            /// Callback method called when the operation is completed
            /// @param teacher the teacher object that is logged in
            @Override
            public void onCompleted(Teacher teacher) {
                if (teacher == null) {
                    getStudentByPhoneAndPassword(phone, password);
                    return;
                }
                /// save the teacher data to shared preferences
                SharedPreferencesUtil.saveTeacher(LogInActivity.this, teacher);
                /// Redirect to main activity and clear back stack to prevent teacher from going back to login screen
                Intent mainIntent = new Intent(LogInActivity.this, MainActivity.class);
                /// Clear the back stack (clear history) and start the MainActivity
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                /// Show error message to teacher
                etPassword.setError("Invalid phone or password");
                etPassword.requestFocus();
                /// Sign out the teacher if failed to retrieve teacher data
                /// This is to prevent the teacher from being logged in again
                SharedPreferencesUtil.signOutTeacher(LogInActivity.this);
            }
        });
    }

    private void getStudentByPhoneAndPassword(String phone, String password) {
        DatabaseService.getInstance().getStudentByPhoneAndPassword(phone, password, new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                if (student == null) {
                    Toast.makeText(LogInActivity.this, "Teacher not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                /// save the teacher data to shared preferences
                SharedPreferencesUtil.saveStudent(LogInActivity.this, student);
                /// Redirect to main activity and clear back stack to prevent teacher from going back to login screen
                Intent mainIntent = new Intent(LogInActivity.this, ImStudentActivity.class);
                /// Clear the back stack (clear history) and start the MainActivity
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                /// Show error message to teacher
                etPassword.setError("Invalid phone or password");
                etPassword.requestFocus();
                /// Sign out the teacher if failed to retrieve teacher data
                /// This is to prevent the teacher from being logged in again
                SharedPreferencesUtil.signOutTeacher(LogInActivity.this);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}