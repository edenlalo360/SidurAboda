package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.models.User;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;

public class UpDateActivity extends AppCompatActivity {

    private static final String TAG = "UpDateActivity";

    EditText firstName, lastName, phoneNumber, licenseNum, password;
    Button updateBtn;

    boolean isEditing = false;

    DatabaseService databaseService;
    User currentUser;

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

        // כפתור חזרה לבית
        Button button18 = findViewById(R.id.updateTOmain);
        button18.setOnClickListener(v ->
        {
            Intent intent = new Intent(UpDateActivity.this, MainActivity.class);
            startActivity(intent);
        });


        // שירות DB
        databaseService = DatabaseService.getInstance();

        // משתמש מחובר
        currentUser = SharedPreferencesUtil.getUser(this);
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // איתור רכיבים
        firstName = findViewById(R.id.et_register_first_name);
        lastName = findViewById(R.id.et_register_last_name);
        phoneNumber = findViewById(R.id.et_register_phone);
        licenseNum = findViewById(R.id.et_register_num_license);
        password = findViewById(R.id.et_register_password);
        updateBtn = findViewById(R.id.btnupdate);

        lockFields();


        // טעינת נתוני המשתמש
        showUserDetails();

        //קריאה לשתי פונקציות שבודקת שהמספר טלפון ורישיון רק של בן אדם אחד



        // כפתור עריכה / שמירה
        updateBtn.setOnClickListener(v -> {
            if (!isEditing) {
                isEditing = true;
                updateBtn.setText("שמירה");
                unlockFields();
            } else {
                validateAndUpdate();
            }
        });
    }


    // הצגת נתוני המשתמש

    private void showUserDetails() {
        databaseService.getUser(currentUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                currentUser = user;

                firstName.setText(currentUser.getFirstName());
                lastName.setText(currentUser.getLastName());
                phoneNumber.setText(currentUser.getPhone());
                licenseNum.setText(currentUser.getLicenseId());
                password.setText(currentUser.getPassword());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load user", e);
                Toast.makeText(UpDateActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPhoneNumber(String phone, DatabaseService.DatabaseCallback<Boolean> callback) {
        databaseService.checkIfPhoneExists(phone, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {
                callback.onCompleted(exists);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onFailed(e);
            }
        });
    }

    private void checkLicenseNum(String license, DatabaseService.DatabaseCallback<Boolean> callback) {
        databaseService.checkIfLicenseNum(license, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {
                callback.onCompleted(exists);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onFailed(e);
            }
        });
    }

    private void validateAndUpdate() {
        String newPhone = phoneNumber.getText().toString().trim();
        String newLicense = licenseNum.getText().toString().trim();

        checkPhoneNumber(newPhone, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean phoneExists) {

                if (phoneExists && !newPhone.equals(currentUser.getPhone())) {
                    Toast.makeText(UpDateActivity.this, "מספר טלפון כבר קיים", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkLicenseNum(newLicense, new DatabaseService.DatabaseCallback<Boolean>() {
                    @Override
                    public void onCompleted(Boolean licenseExists) {

                        if (licenseExists && !newLicense.equals(currentUser.getLicenseId())) {
                            Toast.makeText(UpDateActivity.this, "מספר רישיון כבר קיים", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        updateUserDetails();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(UpDateActivity.this, "שגיאה בבדיקת רישיון", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpDateActivity.this, "שגיאה בבדיקת טלפון", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // עדכון נתוני המשתמש

    private void updateUserDetails() {
        currentUser.setFirstName(firstName.getText().toString());
        currentUser.setLastName(lastName.getText().toString());
        currentUser.setPhone(phoneNumber.getText().toString());
        currentUser.setLicenseId(licenseNum.getText().toString());
        currentUser.setPassword(password.getText().toString());

        databaseService.updateUser(
                currentUser.getUid(),
                user -> {
                    user.setFirstName(currentUser.getFirstName());
                    user.setLastName(currentUser.getLastName());
                    user.setPhone(currentUser.getPhone());
                    user.setLicenseId(currentUser.getLicenseId());
                    user.setPassword(currentUser.getPassword());
                    return user;
                },
                new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void result) {
                        Toast.makeText(UpDateActivity.this, "הפרטים עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                        isEditing = false;
                        updateBtn.setText("עריכה");
                        lockFields();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e(TAG, "Update failed", e);
                        Toast.makeText(UpDateActivity.this, "שגיאה בעדכון פרטים", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    // נעילה / פתיחה של שדות

    private void lockFields() {
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        phoneNumber.setEnabled(false);
        licenseNum.setEnabled(false);
        password.setEnabled(false);
    }

    private void unlockFields() {
        firstName.setEnabled(true);
        lastName.setEnabled(true);
        phoneNumber.setEnabled(true);
        licenseNum.setEnabled(true);
        password.setEnabled(true);
    }
}