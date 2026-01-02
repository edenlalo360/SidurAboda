package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.utils.Validator;

import java.util.Date;

public class InfoCarActivity extends AppCompatActivity {

    Spinner spinner;
    EditText typeCar, carNumber, insuranceDate, licenseDate;
    Button editBtn;
    boolean isEditing = false; // למעקב אם במצב עריכה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_car);

        // מערכת שוליים
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // איתור רכיבים
        typeCar = findViewById(R.id.type);
        spinner = findViewById(R.id.spinnerRank);
        carNumber = findViewById(R.id.carNumber);
        insuranceDate = findViewById(R.id.insuranceDate);
        licenseDate = findViewById(R.id.licensecarDate);
        editBtn = findViewById(R.id.editBtn);

        // *** כאן השדות נעולים כברירת מחדל ***
        typeCar.setEnabled(false);
        spinner.setEnabled(false);
        carNumber.setEnabled(false);
        insuranceDate.setEnabled(false);
        licenseDate.setEnabled(false);




        // מילוי ספינר
        String[] options = {
                "דרגת רישיון", "A אופנוע", "B רכב פרטי עד 3.5 טון",
                "C1 רכב מסחרי עד 12 טון", "C רכב מסחרי מעל 12 טון",
                "D אוטובוס", "D1 מונית", "E גורר-תומך", "טרקטור 1"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // כפתור בית
        Button button10 = findViewById(R.id.updateTOmain);
        button10.setOnClickListener(v -> {
            Intent intent = new Intent(InfoCarActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // כפתור עריכה/שמירה
        editBtn.setOnClickListener(v -> {

            if (!isEditing) {
                // --- מצב עריכה ---
                isEditing = true;
                editBtn.setText("שמירה");

                if (insuranceDate.getText().toString().isEmpty()) {
                    insuranceDate.setError("יש להזין תאריך ביטוח");
                    return;
                }

                typeCar.setEnabled(true);
                carNumber.setEnabled(true);
                insuranceDate.setEnabled(true);
                licenseDate.setEnabled(true);
                spinner.setEnabled(true);

            } else {
                // --- מצב שמירה ---
//
//
//                checkInput()
//                if (!updateCar()) {
//
//                }

                isEditing = false;
                editBtn.setText("עריכה");

                typeCar.setEnabled(false);
                carNumber.setEnabled(false);
                insuranceDate.setEnabled(false);
                licenseDate.setEnabled(false);
                spinner.setEnabled(false);


            }
        });
    }
    /// Check if the input is valid
    /// @return true if the input is valid, false otherwise
    private boolean checkInput(String type, String rank, String number, Date insurancedate, Date licensecardate) {

        if (!Validator.isTypeValid(type)) {
            typeCar.setError("Car type must be at least 2 characters long");
            typeCar.requestFocus();
            return false;
        }

        if (!Validator.isCarNumberValid(number)) {
            carNumber.setError("Car number must be between 7-8 characters long");
            carNumber.requestFocus();
            return false;
        }

        if (!Validator.isInsuranceDateValid(insurancedate)) {
            insuranceDate.setError("Please enter the last insurance date");
            insuranceDate.requestFocus();
            return false;
        }

        if (!Validator.isLicenseDateValid(licensecardate)) {
            licenseDate.setError("Please enter the last license date");
            licenseDate.requestFocus();
            return false;
        }

        if (!Validator.isSpinnerValid(rank)) {
            Toast.makeText(InfoCarActivity.this, "Please select license rank", Toast.LENGTH_SHORT).show();
            spinner.requestFocus();
            return false;
        }

        return true;

    }}

