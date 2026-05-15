package com.example.siduraboda.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
            
            // הגבלה לתאריכים מהשנה האחרונה בלבד
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            cal.add(Calendar.YEAR, -1);
            dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
            
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

                String type = etType.getText().toString().trim();
                String carNumber = etNumber.getText().toString().trim();
                String insuranceDate = etInsuranceDate.getText().toString().trim();
                String licenseDate = etLicenseDate.getText().toString().trim();
                String rank = spRank.getSelectedItem().toString();

                LocalDate insuranceLocalDate = convertStringToDate(insuranceDate);
                LocalDate licenseLocalDate = convertStringToDate(licenseDate);

                if (!checkInput(type, rank, carNumber, insuranceLocalDate, licenseLocalDate)) {
                    return;
                }

                btnSubmit.setEnabled(false); // Disable to prevent multiple clicks
                Toast.makeText(AddCarActivity.this, "בודק נתונים...", Toast.LENGTH_SHORT).show();

                DatabaseService.getInstance().checkIfCarNumberExists(carNumber, new DatabaseService.DatabaseCallback<Boolean>() {
                    @Override
                    public void onCompleted(Boolean exists) {
                        if (exists) {
                            Toast.makeText(AddCarActivity.this, "מספר רכב כבר קיים במערכת (אצל מורה אחר)", Toast.LENGTH_SHORT).show();
                            etNumber.setError("מספר רכב כבר קיים");
                            etNumber.requestFocus();
                            btnSubmit.setEnabled(true);
                            return;
                        }

                        Car car = new Car(type, rank, carNumber, licenseDate, insuranceDate);

                        String teacherId = SharedPreferencesUtil.getTeacherId(AddCarActivity.this);
                        if (teacherId == null) {
                            Toast.makeText(AddCarActivity.this, "שגיאה: מורה לא מחובר", Toast.LENGTH_SHORT).show();
                            btnSubmit.setEnabled(true);
                            return;
                        }

                        Log.d("AddCarActivity", "Updating teacher: " + teacherId);
                        Toast.makeText(AddCarActivity.this, "מוסיף רכב...", Toast.LENGTH_SHORT).show();

                        DatabaseService.getInstance().updateTeacher(teacherId, new UnaryOperator<Teacher>() {
                            @Override
                            public Teacher apply(Teacher teacher) {
                                if (teacher == null) {
                                    Log.e("AddCarActivity", "Teacher object is null in DB for ID: " + teacherId);
                                    return null;
                                }
                                teacher.addCar(car);
                                return teacher;
                            }
                        }, new DatabaseService.DatabaseCallback<Teacher>() {
                            @Override
                            public void onCompleted(Teacher serverTeacher) {
                                if (serverTeacher != null) {
                                    Log.d("AddCarActivity", "Car added successfully to DB");
                                    SharedPreferencesUtil.saveTeacher(AddCarActivity.this, serverTeacher);
                                    Toast.makeText(AddCarActivity.this, "הרכב נוסף בהצלחה!", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Log.e("AddCarActivity", "Failed to update: serverTeacher is null");
                                    Toast.makeText(AddCarActivity.this, "שגיאה: המורה לא נמצא במסד הנתונים", Toast.LENGTH_LONG).show();
                                    btnSubmit.setEnabled(true);
                                }
                            }

                            @Override
                            public void onFailed(Exception e) {
                                Log.e("AddCarActivity", "Update failed", e);
                                Toast.makeText(AddCarActivity.this, "שגיאה בתקשורת: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                btnSubmit.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(AddCarActivity.this, "שגיאה בבדיקת מספר רכב: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSubmit.setEnabled(true);
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
            Toast.makeText(AddCarActivity.this, "אנא הזן תאריך ביטוח תקין (עד שנה אחורה)", Toast.LENGTH_SHORT).show();
            etInsuranceDate.requestFocus();
            return false;
        }

        if (!Validator.isLicenseDateValid(licensecardate)) {
            Toast.makeText(AddCarActivity.this, "אנא הזן תאריך רישיון תקין (עד שנה אחורה)", Toast.LENGTH_SHORT).show();
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
        try {
            // שימוש בפורמט שמתאים למה שה-DatePickerDialog מחזיר (d/M/yyyy)
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // אם יש שגיאה בפורמט
        }
    }
}