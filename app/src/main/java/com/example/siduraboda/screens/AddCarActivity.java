package com.example.siduraboda.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
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
import com.example.siduraboda.models.Car;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.example.siduraboda.utils.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.function.UnaryOperator;

public class AddCarActivity extends AppCompatActivity {

    EditText etType, etNumber, etInsuranceDate, etLicenseDate;
    Spinner spRank;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etType = findViewById(R.id.et_add_car_type);
        etNumber = findViewById(R.id.et_add_car_number);
        etInsuranceDate = findViewById(R.id.et_add_car_insurance_date);
        etLicenseDate = findViewById(R.id.et_add_car_license_car_date);
        spRank = findViewById(R.id.sp_add_car_rank);
        btnSubmit = findViewById(R.id.btn_add_car_submit);

        //מציג לוח שנה
        View.OnClickListener dateClickListener = v -> {
            EditText target = (EditText) v;
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        String date = day + "/" + (month + 1) + "/" + year;
                        target.setText(date);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        };
        etInsuranceDate.setOnClickListener(dateClickListener);
        etLicenseDate.setOnClickListener(dateClickListener);

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
        spRank.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = etType.getText().toString() + "";
                String carNumber = etNumber.getText().toString() + "";
                String insuranceDate = etInsuranceDate.getText().toString() + "";
                String licenseDate = etLicenseDate.getText().toString() + "";
                String rank = spRank.getSelectedItem().toString() + "";

                if (!checkInput(type, rank, carNumber, convertStringToDate(insuranceDate), convertStringToDate(licenseDate))) {
                    return;
                }

                Car car = new Car(type, rank, carNumber, licenseDate, insuranceDate);

                String teacherId = SharedPreferencesUtil.getTeacherId(AddCarActivity.this);
                DatabaseService.getInstance().updateTeacher(teacherId, new UnaryOperator<Teacher>() {
                    @Override
                    public Teacher apply(Teacher teacher) {
                        if (teacher == null) return null;
                        teacher.addCar(car);
                        return teacher;
                    }
                }, new DatabaseService.DatabaseCallback<Teacher>() {
                    @Override
                    public void onCompleted(Teacher teacher) {
                        Toast.makeText(AddCarActivity.this, "Car successfully added!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
            }
        });
    }

    /// Check if the input is valid
    ///
    /// @return true if the input is valid, false otherwise
    private boolean checkInput(String type, String rank, String number, LocalDate insurancedate, LocalDate licensecardate) {

        if (!Validator.isTypeValid(type)) {
            etType.setError("Car type must be at least 2 characters long");
            etType.requestFocus();
            return false;
        }

        if (!Validator.isCarNumberValid(number)) {
            etNumber.setError("Car number must be between 7-8 characters long");
            etNumber.requestFocus();
            return false;
        }

        if (!Validator.isInsuranceDateValid(insurancedate)) {
            etInsuranceDate.setError("Please enter the last insurance date");
            etInsuranceDate.requestFocus();
            return false;
        }

        if (!Validator.isLicenseDateValid(licensecardate)) {
            etLicenseDate.setError("Please enter the last license date");
            etLicenseDate.requestFocus();
            return false;
        }

        if (!Validator.isSpinnerValid(rank)) {
            Toast.makeText(AddCarActivity.this, "Please select license rank", Toast.LENGTH_SHORT).show();
            spRank.requestFocus();
            return false;
        }

        return true;

    }

    private LocalDate convertStringToDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        return LocalDate.parse(dateStr, formatter);
    }
}