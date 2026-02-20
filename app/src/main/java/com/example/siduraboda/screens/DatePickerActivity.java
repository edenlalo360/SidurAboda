package com.example.siduraboda.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import com.example.siduraboda.R;

public class DatePickerActivity extends AppCompatActivity {

    // הגדרת המשתנים גלובלית כדי שנוכל לגשת אליהם
    private TextView tvSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_date_picker);

        // טיפול ב-System Bars (Insets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבי ה-UI
        Button btnOpenPicker = findViewById(R.id.btnOpenDatePicker);
        tvSelectedDate = findViewById(R.id.tvDisplayDate);

        // חיבור הכפתור לפונקציה של פתיחת הדיאלוג
        btnOpenPicker.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // עיצוב התאריך
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String selectedDate = sdf.format(new Date(selection));

            // עדכון ה-TextView
            tvSelectedDate.setText(selectedDate);
        });
    }
}