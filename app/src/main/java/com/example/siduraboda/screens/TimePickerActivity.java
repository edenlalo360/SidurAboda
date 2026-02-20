package com.example.siduraboda.screens;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Locale;

import com.example.siduraboda.R;

public class TimePickerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_time_picker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnOpen = findViewById(R.id.btnOpenMaterialPicker);
        TextView tvDisplay = findViewById(R.id.tvDisplayTime);

        btnOpen.setOnClickListener(v -> {
            // 1. Get current time for the picker's default position
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            // 2. Build the MaterialTimePicker
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H) // Use CLOCK_24H if preferred
                    .setHour(currentHour)
                    .setMinute(currentMinute)
                    .setTitleText("Select Time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK) // Starts with the dial view
                    .build();

            // 3. Show the dialog
            materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");

            // 4. Handle the "OK" button click
            materialTimePicker.addOnPositiveButtonClickListener(view -> {
                int hour = materialTimePicker.getHour();
                int minute = materialTimePicker.getMinute();

                // Format the time nicely (e.g., 09:05)
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                tvDisplay.setText(formattedTime);
            });

            // Optional: Handle the "Cancel" or "Back" buttons
            materialTimePicker.addOnNegativeButtonClickListener(view -> {
                // Logic for when user cancels
            });
        });
    }
}