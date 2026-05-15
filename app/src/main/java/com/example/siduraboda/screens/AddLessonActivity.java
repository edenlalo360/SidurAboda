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
import com.example.siduraboda.models.DayAndHours;
import com.example.siduraboda.models.HourMinute;
import com.example.siduraboda.models.Lesson;
import com.example.siduraboda.models.Student;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.models.Weekday;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class AddLessonActivity extends AppCompatActivity {

    Spinner spStudent, spCar;
    Button btnSubmitLesson;
    Button btnDatePicker, btnStartTime, btnEndTime;
    TextView tvDuration;

    String teacherId;

    private String selectedDateStr = "";
    private String selectedStartTimeStr = "";
    private String selectedEndTimeStr = "";

    // רשימה לשמירת השיעורים הקיימים לבדיקת חפיפה
    private List<Lesson> allExistingLessons = new ArrayList<>();

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

        spStudent = findViewById(R.id.sp_choose_student);
        spCar = findViewById(R.id.sp_choose_car);
        btnSubmitLesson = findViewById(R.id.btn_add_lesson_submit);
        btnDatePicker = findViewById(R.id.btn_date_picker);
        btnStartTime = findViewById(R.id.btn_start_time_picker);
        btnEndTime = findViewById(R.id.btn_end_time_picker);
        tvDuration = findViewById(R.id.tv_lesson_duration);

        teacherId = SharedPreferencesUtil.getTeacherId(this);

        btnDatePicker.setOnClickListener(v -> showDatePicker());
        btnStartTime.setOnClickListener(v -> showTimePicker(btnStartTime, "שעת התחלה", true));
        btnEndTime.setOnClickListener(v -> showTimePicker(btnEndTime, "שעת סיום", false));

        loadSpinnersData();
        loadExistingLessons(); // טעינת השיעורים מה-DB לצורך בדיקת חפיפה
        btnSubmitLesson.setOnClickListener(v -> CreateLesson());
    }

    private void loadExistingLessons() {
        DatabaseService.getInstance().getLessonList(new DatabaseService.DatabaseCallback<List<Lesson>>() {
            @Override
            public void onCompleted(List<Lesson> lessons) {
                allExistingLessons = lessons;
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    private void showDatePicker() {
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("בחר תאריך לשיעור")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints)
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
            calculateDuration();
        });
    }

    private int getMinutes(String time) {
        try {
            String[] parts = time.split(":");
            return (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    private void calculateDuration() {
        if (!selectedStartTimeStr.isEmpty() && !selectedEndTimeStr.isEmpty()) {
            try {
                int startTotalMinutes = getMinutes(selectedStartTimeStr);
                int endTotalMinutes = getMinutes(selectedEndTimeStr);
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

    private boolean isLessonOverlapping() {
        int newStart = getMinutes(selectedStartTimeStr);
        int newEnd = getMinutes(selectedEndTimeStr);

        for (Lesson lesson : allExistingLessons) {
            if (lesson.getDate().equals(selectedDateStr)) {
                int existStart = getMinutes(lesson.getDayAndHours().getStartTime().toString());
                int existEnd = getMinutes(lesson.getDayAndHours().getEndTime().toString());

                // חפיפה אם השיעור החדש מתחיל לפני שהקיים מסתיים, ונגמר אחרי שהקיים מתחיל
                if (newStart < existEnd && newEnd > existStart) {
                    return true;
                }
            }
        }
        return false;
    }

    private void CreateLesson() {
        int studentPos = spStudent.getSelectedItemPosition();
        int carPos = spCar.getSelectedItemPosition();

        if (!checkInputLesson(studentPos, carPos)) return;

        Student selectedStudent = (Student) spStudent.getSelectedItem();
        Car selectedCar = (Car) spCar.getSelectedItem();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(selectedDateStr, formatter);
            Weekday weekday = Weekday.valueOf(localDate.getDayOfWeek().name());

            HourMinute startTime = new HourMinute(selectedStartTimeStr);
            HourMinute endTime = new HourMinute(selectedEndTimeStr);
            DayAndHours dayAndHours = new DayAndHours(weekday, startTime, endTime);

            String lessonId = DatabaseService.getInstance().generateLessonId();
            Lesson lesson = new Lesson(lessonId, teacherId, selectedStudent.getId(), selectedCar, dayAndHours, selectedDateStr, "");

            DatabaseService.getInstance().createNewLesson(lesson, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(AddLessonActivity.this, "השיעור נקבע בהצלחה!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(AddLessonActivity.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "שגיאה בעיבוד הנתונים", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInputLesson(int studentPos, int carPos) {
        if (studentPos == 0) {
            Toast.makeText(this, "אנא בחר תלמיד מהרשימה", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (carPos == 0) {
            Toast.makeText(this, "אנא בחר רכב מהרשימה", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDateStr.isEmpty() || selectedStartTimeStr.isEmpty() || selectedEndTimeStr.isEmpty()) {
            Toast.makeText(this, "אנא בחר תאריך ושעות", Toast.LENGTH_SHORT).show();
            return false;
        }

        int duration = getMinutes(selectedEndTimeStr) - getMinutes(selectedStartTimeStr);

        if (duration <= 0) {
            Toast.makeText(this, "שעת סיום חייבת להיות אחרי שעת התחלה", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (duration < 40) {
            Toast.makeText(this, "שיעור חייב להיות לפחות 40 דקות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isLessonOverlapping()) {
            Toast.makeText(this, "קיימת חפיפה בשעה עם שיעור אחר בתאריך זה", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void loadSpinnersData() {
        DatabaseService.getInstance().getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> students) {
                String teacherId = SharedPreferencesUtil.getTeacherId(AddLessonActivity.this);
                students.removeIf(student -> !Objects.equals(student.getTeacherId(), teacherId));
                setupStudentSpinner(students);
            }

            @Override
            public void onFailed(Exception e) {
            }
        });

        DatabaseService.getInstance().getUser(teacherId, new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                if (teacher != null && teacher.getCars() != null) {
                    setupCarSpinner(teacher.getCars());
                }
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    private void setupStudentSpinner(List<Student> students) {
        List<Student> studentList = new ArrayList<>();
        Student placeholder = new Student();
        placeholder.setName("בחר תלמיד");
        studentList.add(placeholder);
        if (students != null) studentList.addAll(students);

        ArrayAdapter<Student> adapter = new ArrayAdapter<Student>(this, android.R.layout.simple_spinner_item, studentList) {
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
        List<Car> carList = new ArrayList<>();
        Car placeholder = new Car();
        placeholder.setType("בחר רכב");
        carList.add(placeholder);
        if (cars != null) carList.addAll(cars);

        ArrayAdapter<Car> adapter = new ArrayAdapter<Car>(this, android.R.layout.simple_spinner_item, carList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getView(position, convertView, parent);
                Car current = getItem(position);
                label.setText(position == 0 ? current.getType() : current.getType() + " (" + current.getCarNumber() + ")");
                return label;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                Car current = getItem(position);
                label.setText(position == 0 ? current.getType() : current.getType() + " - " + current.getCarNumber());
                return label;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCar.setAdapter(adapter);
    }
}