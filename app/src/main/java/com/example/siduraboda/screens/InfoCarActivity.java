package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;

public class InfoCarActivity extends AppCompatActivity {

    Spinner spinner;
    EditText carNumber, insuranceDate, licenseDate;
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
        spinner = findViewById(R.id.spinnerview);
        carNumber = findViewById(R.id.editTextNumber2);
        insuranceDate = findViewById(R.id.editTextDate2);
        licenseDate = findViewById(R.id.editTextDate3);
        editBtn = findViewById(R.id.editBtn);

        // *** כאן השדות נעולים כברירת מחדל ***
        carNumber.setEnabled(false);
        insuranceDate.setEnabled(false);
        licenseDate.setEnabled(false);
        spinner.setEnabled(false);

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
        Button button10 = findViewById(R.id.infocarTOmain);
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

                carNumber.setEnabled(true);
                insuranceDate.setEnabled(true);
                licenseDate.setEnabled(true);
                spinner.setEnabled(true);

            } else {
                // --- מצב שמירה ---
                isEditing = false;
                editBtn.setText("עריכה");

                carNumber.setEnabled(false);
                insuranceDate.setEnabled(false);
                licenseDate.setEnabled(false);
                spinner.setEnabled(false);


            }
        });
    }
}
