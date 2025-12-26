package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;

public class UpDateActivity extends AppCompatActivity {

    EditText firstName, lastName, phoneNumber, licenseNum, Password;
    Button updateBtn;
    boolean isEditing = false; // למעקב אם במצב עריכה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_up_date);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // איתור רכיבים
        firstName = findViewById(R.id.et_register_first_name);
        lastName = findViewById(R.id.et_register_last_name);
        phoneNumber = findViewById(R.id.et_register_phone);
        licenseNum = findViewById(R.id.et_register_num_license);
        Password= findViewById(R.id.et_register_password);
        updateBtn = findViewById(R.id.btnupdate);

        // *** כאן השדות נעולים כברירת מחדל ***
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        phoneNumber.setEnabled(false);
        licenseNum.setEnabled(false);
        Password.setEnabled(false);

        // כפתור בית
        Button button18 = findViewById(R.id.updateTOmain);
        button18.setOnClickListener(v -> {
            Intent intent = new Intent(UpDateActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // כפתור עריכה/שמירה
        updateBtn.setOnClickListener(v -> {

            if (!isEditing) {
                // --- מצב עריכה ---
                isEditing = true;
                updateBtn.setText("שמירה");

                firstName.setEnabled(true);
                lastName.setEnabled(true);
                phoneNumber.setEnabled(true);
                licenseNum.setEnabled(true);
                Password.setEnabled(true);


            } else {
                // --- מצב שמירה ---
                isEditing = false;
                updateBtn.setText("עריכה");
                firstName.setEnabled(false);
                lastName.setEnabled(false);
                phoneNumber.setEnabled(false);
                licenseNum.setEnabled(false);
                Password.setEnabled(false);


            }
        });
    }
}