package com.example.siduraboda.screens;

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
import com.example.siduraboda.models.Car;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;

import java.util.ArrayList;

public class InfoCarActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText typeCar, carNumber, insuranceDate, licenseDate;
    private Button editBtn;
    private boolean isEditing = false;
    private Car currentCar;
    private final String[] options = {
            "דרגת רישיון", "A אופנוע", "B רכב פרטי עד 3.5 טון",
            "C1 רכב מסחרי עד 12 טון", "C רכב מסחרי מעל 12 טון",
            "D אוטובוס", "D1 מונית", "E גורר-תומך", "טרקטור 1"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_car);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת אובייקט הרכב מה-Intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            currentCar = getIntent().getSerializableExtra("car", Car.class);
        } else {
            currentCar = (Car) getIntent().getSerializableExtra("car");
        }

        initViews();
        setupSpinner();

        if (currentCar != null) {
            populateFields();
        } else {
            Toast.makeText(this, "שגיאה בטעינת נתוני הרכב", Toast.LENGTH_SHORT).show();
            finish();
        }


        // כפתור עריכה/שמירה
        editBtn.setOnClickListener(v -> {
            if (!isEditing) {
                enableEditing(true);
            } else {
                saveChanges();
            }
        });
    }

    private void initViews() {
        typeCar = findViewById(R.id.type);
        spinner = findViewById(R.id.spinnerRank);
        carNumber = findViewById(R.id.carNumber);
        insuranceDate = findViewById(R.id.insuranceDate);
        licenseDate = findViewById(R.id.licensecarDate);
        editBtn = findViewById(R.id.editBtn);

        enableEditing(false);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void populateFields() {
        typeCar.setText(currentCar.getType());
        carNumber.setText(currentCar.getCarNumber());
        insuranceDate.setText(currentCar.getInsuranceDate());
        licenseDate.setText(currentCar.getLicenseDate());

        // מציאת האינדקס של הדרגה בספינר
        for (int j = 0; j < options.length; j++) {
            if (options[j].equals(currentCar.getRank())) {
                spinner.setSelection(j);
                break;
            }
        }
    }

    private void enableEditing(boolean enable) {
        isEditing = enable;
        typeCar.setEnabled(enable);
        spinner.setEnabled(enable);
        carNumber.setEnabled(enable);
        insuranceDate.setEnabled(enable);
        licenseDate.setEnabled(enable);
        editBtn.setText(enable ? "שמירה" : "עריכה");
    }

    private void saveChanges() {
        String type = typeCar.getText().toString().trim();
        String number = carNumber.getText().toString().trim();
        String insurance = insuranceDate.getText().toString().trim();
        String license = licenseDate.getText().toString().trim();
        String rank = spinner.getSelectedItem().toString();

        if (type.isEmpty() || number.isEmpty() || insurance.isEmpty() || license.isEmpty() || rank.equals(options[0])) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        editBtn.setEnabled(false);
        final String originalNumberStr = currentCar.getCarNumber();

        DatabaseService.getInstance().checkIfCarNumberExists(number, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {
                // Check if the number exists and is NOT the original number
                String cleanNew = number.trim().replace("-", "").replace(" ", "");
                String cleanOriginal = originalNumberStr.trim().replace("-", "").replace(" ", "");
                
                if (exists && !cleanNew.equals(cleanOriginal)) {
                    Toast.makeText(InfoCarActivity.this, "מספר רכב כבר קיים במערכת (אצל מורה אחר)", Toast.LENGTH_SHORT).show();
                    carNumber.setError("מספר רכב כבר קיים");
                    carNumber.requestFocus();
                    editBtn.setEnabled(true);
                    return;
                }

                // Update local object
                currentCar.setType(type);
                currentCar.setCarNumber(number);
                currentCar.setInsuranceDate(insurance);
                currentCar.setLicenseDate(license);
                currentCar.setRank(rank);

                // Update in Database
                Teacher teacher = SharedPreferencesUtil.getTeacher(InfoCarActivity.this);
                if (teacher != null) {
                    ArrayList<Car> cars = teacher.getCars();
                    boolean found = false;
                    for (int i = 0; i < cars.size(); i++) {
                        if (cars.get(i).getCarNumber().equals(originalNumberStr)) {
                            cars.set(i, currentCar);
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        // If for some reason we didn't find it by original number, add it
                        cars.add(currentCar);
                    }
                    
                    teacher.setCars(cars);

                    DatabaseService.getInstance().updateTeacher(teacher.getUid(), t -> {
                        if (t != null) {
                            t.setCars(cars);
                        }
                        return t;
                    }, new DatabaseService.DatabaseCallback<Teacher>() {
                        @Override
                        public void onCompleted(Teacher object) {
                            if (object != null) {
                                SharedPreferencesUtil.saveTeacher(InfoCarActivity.this, object);
                                Toast.makeText(InfoCarActivity.this, "פרטי הרכב עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                                enableEditing(false);
                                editBtn.setEnabled(true);
                            } else {
                                Toast.makeText(InfoCarActivity.this, "שגיאה בעדכון הנתונים", Toast.LENGTH_SHORT).show();
                                editBtn.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(InfoCarActivity.this, "שגיאה בעדכון הנתונים: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            editBtn.setEnabled(true);
                        }
                    });
                } else {
                    editBtn.setEnabled(true);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(InfoCarActivity.this, "שגיאה בבדיקת מספר רכב", Toast.LENGTH_SHORT).show();
                editBtn.setEnabled(true);
            }
        });
    }
}
