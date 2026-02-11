package com.example.siduraboda.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.siduraboda.utils.Validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.UnaryOperator;

public class AddLessonActivity extends AppCompatActivity {

    EditText etLessonDate, etStartLesson;
    Spinner spStudent, spCar;
    Button btnSubmitLesson;
    String teacherId;

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
        etLessonDate = findViewById(R.id.et_add_lesson_date);
        etStartLesson = findViewById(R.id.et_long_lesson);
        spStudent = findViewById(R.id.sp_choose_student);
        spCar = findViewById(R.id.sp_choose_car);
        btnSubmitLesson = findViewById(R.id.btn_add_lesson_submit);

        teacherId = SharedPreferencesUtil.getTeacherId(this);

        //מציג לוח שנה
        etLessonDate.setOnClickListener(v -> {
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
        });

        DatabaseService.getInstance().getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> students) {
                setupStudentSpinner(students);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });



        // get list of cars that we will show in the spinner
        DatabaseService.getInstance().getUser(teacherId, new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                ArrayList<Car> cars = teacher.getCars();
                setupCarSpinner(cars);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        btnSubmitLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLesson();
            }
        });
    }

    private void CreateLesson() {
        // 1. Convert String to LocalDate (using your previous formatter)
        String dateInput = etLessonDate.getText().toString();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate lessonDate = LocalDate.parse(dateInput, dateFormatter);

        // 2. Convert String to LocalTime
        String timeInput = etStartLesson.getText().toString();
        // Assumes input is "14:30" or "2:30". Use "H:mm" for 24hr or "h:mm a" for AM/PM
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime lessonTime = LocalTime.parse(timeInput, timeFormatter);

        // Get the actual objects from the Spinners
        Student selectedStudent = (Student) spStudent.getSelectedItem();
        Car selectedCar = (Car) spCar.getSelectedItem();


        // Pass the objects to validation
        if(!checkInputLesson(lessonDate, lessonTime, selectedStudent, selectedCar)) {
            return;
        }

        String lessonId = DatabaseService.getInstance().generateLessonId();


        // Create lesson using the student name and car number/ID
        Lesson lesson = new Lesson(lessonId, lessonDate, lessonTime, teacherId, selectedStudent.getId(), selectedCar);

        DatabaseService.getInstance().createNewLesson(lesson, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void setupStudentSpinner(List<Student> students) {

        ArrayAdapter<Student> adapter = new ArrayAdapter<Student>(this, android.R.layout.simple_spinner_item, students) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getView(position, convertView, parent);
                Student student = getItem(position);
                label.setText(student.getName());
                return label;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                // You can customize the dropdown text specifically here
                Student student = getItem(position);
                label.setText(student.getName());
                return label;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStudent.setAdapter(adapter);
    }

    /// Check if the input is valid
    /// @return true if the input is valid, false otherwise
    private boolean checkInputLesson(LocalDate lessonDate, LocalTime lessonTime, Student student, Car car) {

        if (!Validator.isLessonDateValid(lessonDate)) {
            etLessonDate.setError("Please select a valid date");
            return false;
        }

        if (!Validator.isTimeValid(lessonTime)) {
            etStartLesson.setError("Please enter a valid time (H:mm)");
            return false;
        }

        if (student == null) {
            Toast.makeText(this, "Please select a student", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (car == null) {
            Toast.makeText(this, "Please select a car", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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