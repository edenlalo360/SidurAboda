package com.example.siduraboda.screens;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Car;
import com.example.siduraboda.models.Lesson;
import com.example.siduraboda.models.Student;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddLessonActivity extends AppCompatActivity {

    Spinner spStudent, spCar;
    Button btnSubmitLesson;
    Button btnDatePicker, btnStartTime, btnEndTime;
    TextView tvDuration; // ה-TextView החדש למשך השיעור

    String teacherId;

    private String selectedDateStr = "";
    private String selectedStartTimeStr = "";
    private String selectedEndTimeStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_lesson);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבים
        spStudent = findViewById(R.id.sp_choose_student);
        spCar = findViewById(R.id.sp_choose_car);
        btnSubmitLesson = findViewById(R.id.btn_add_lesson_submit);
        btnDatePicker = findViewById(R.id.btn_date_picker);
        btnStartTime = findViewById(R.id.btn_start_time_picker);
        btnEndTime = findViewById(R.id.btn_end_time_picker);
        tvDuration = findViewById(R.id.tv_lesson_duration); // אתחול משך השיעור

        teacherId = SharedPreferencesUtil.getTeacherId(this);

        // מאזינים
        btnDatePicker.setOnClickListener(v -> showDatePicker());
        btnStartTime.setOnClickListener(v -> showTimePicker(btnStartTime, "שעת התחלה", true));
        btnEndTime.setOnClickListener(v -> showTimePicker(btnEndTime, "שעת סיום", false));

        loadSpinnersData();
        btnSubmitLesson.setOnClickListener(v -> CreateLesson());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("בחר תאריך לשיעור")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            selectedDateStr = sdf.format(new Date(selection));
            btnDatePicker.setText("תאריך: " + selectedDateStr);
        });
    }

    private void showTimePicker(Button btn, String title, boolean isStart) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText(title)
                .build();

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
        timePicker.addOnPositiveButtonClickListener(v -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", timePicker.getHour(), timePicker.getMinute());
            if (isStart) selectedStartTimeStr = time;
            else selectedEndTimeStr = time;

            btn.setText(title + ": " + time);
            calculateDuration(); // חישוב משך השיעור בכל בחירת שעה
        });
    }

    // פונקציה לחישוב משך השיעור
    private void calculateDuration() {
        if (!selectedStartTimeStr.isEmpty() && !selectedEndTimeStr.isEmpty()) {
            try {
                String[] startParts = selectedStartTimeStr.split(":");
                String[] endParts = selectedEndTimeStr.split(":");

                int startTotalMinutes = (Integer.parseInt(startParts[0]) * 60) + Integer.parseInt(startParts[1]);
                int endTotalMinutes = (Integer.parseInt(endParts[0]) * 60) + Integer.parseInt(endParts[1]);

                int durationMinutes = endTotalMinutes - startTotalMinutes;

                if (durationMinutes > 0) {
                    int hours = durationMinutes / 60;
                    int mins = durationMinutes % 60;
                    String text = "משך השיעור: ";
                    if (hours > 0) text += hours + " שעות ו-";
                    text += mins + " דקות";
                    tvDuration.setText(text);
                } else {
                    tvDuration.setText("שעת סיום חייבת להיות אחרי שעת התחלה");
                }
            } catch (Exception e) {
                tvDuration.setText("שגיאה בחישוב המשך");
            }
        }
    }

    private void CreateLesson() {
        Student selectedStudent = (Student) spStudent.getSelectedItem();
        Car selectedCar = (Car) spCar.getSelectedItem();

        if (!checkInputLesson(selectedStudent, selectedCar)) return;

        String lessonId = DatabaseService.getInstance().generateLessonId();
        Lesson lesson = new Lesson(lessonId, teacherId, selectedStudent.getId(), selectedCar);

        // כאן תוסיפי את הלוגיקה לשמירת התאריך והשעות בתוך ה-Lesson אם יש לך שדות כאלו
        // למשל: lesson.setDate(selectedDateStr);

        DatabaseService.getInstance().createNewLesson(lesson, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(AddLessonActivity.this, "השיעור נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AddLessonActivity.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkInputLesson(Student student, Car car) {
        if (student == null || car == null) {
            Toast.makeText(this, "אנא בחר סטודנט ורכב", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDateStr.isEmpty() || selectedStartTimeStr.isEmpty() || selectedEndTimeStr.isEmpty()) {
            Toast.makeText(this, "אנא בחר תאריך ושעות", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadSpinnersData() {
        DatabaseService.getInstance().getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> students) { setupStudentSpinner(students); }
            @Override
            public void onFailed(Exception e) {}
        });

        DatabaseService.getInstance().getUser(teacherId, new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                if (teacher != null && teacher.getCars() != null) setupCarSpinner(teacher.getCars());
            }
            @Override
            public void onFailed(Exception e) {}
        });
    }

    private void setupStudentSpinner(List<Student> students) {
        ArrayAdapter<Student> adapter = new ArrayAdapter<Student>(this, android.R.layout.simple_spinner_item, students) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getView(position, convertView, parent);
                label.setText(getItem(position).getName());
                return label;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                label.setText(getItem(position).getName());
                return label;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStudent.setAdapter(adapter);
    }

    private void setupCarSpinner(List<Car> cars) {
        ArrayAdapter<Car> adapter = new ArrayAdapter<Car>(this, android.R.layout.simple_spinner_item, cars) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getView(position, convertView, parent);
                label.setText(getItem(position).getType() + " (" + getItem(position).getCarNumber() + ")");
                return label;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                label.setText(getItem(position).getType() + " - " + getItem(position).getCarNumber());
                return label;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCar.setAdapter(adapter);
    }
}